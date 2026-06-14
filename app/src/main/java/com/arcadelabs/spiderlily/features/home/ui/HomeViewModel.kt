package com.arcadelabs.spiderlily.features.home.ui

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.core.db.SpiderLilyDatabase
import com.arcadelabs.spiderlily.core.db.entity.HistoryEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity
import com.arcadelabs.spiderlily.features.home.domain.model.HomeManga
import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection
import com.arcadelabs.spiderlily.mihon.MihonExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.FilterList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: SpiderLilyDatabase,
    private val extensionManager: MihonExtensionManager,
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val SEARCH_DEBOUNCE_MS = 400L
        private const val MAX_SEARCH_PER_SOURCE = 6

        private val accentColors = listOf(
            Color(0xFFC97B84), Color(0xFF7BA7C9), Color(0xFF8B7BC9),
            Color(0xFFC9A87B), Color(0xFF7BC997), Color(0xFFC97B7B),
            Color(0xFF84C97B), Color(0xFF7BC9C9), Color(0xFFC9847B),
        )
    }

    private val viewModelState = MutableStateFlow(HomeUiState())
    private val searchQueryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        database.getMangaDao().observeHistory(),
    ) { state, historyManga ->
        if (state.isSearchExpanded) {
            // When search is expanded, don't update home content
            state.copy(isLoading = false)
        } else {
            state.copy(
                sections = groupByTime(historyManga),
                isLoading = false,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    init {
        observeSearchQuery()
    }

    // ---- Search ----

    fun onSearchExpandedChange(expanded: Boolean) {
        viewModelState.update {
            if (expanded) {
                it.copy(isSearchExpanded = true)
            } else {
                it.copy(
                    isSearchExpanded = false,
                    searchQuery = "",
                    searchResults = emptyList(),
                    isSearching = false,
                )
            }
        }
        if (!expanded) searchQueryFlow.value = ""
    }

    fun onSearchQueryChange(query: String) {
        viewModelState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query -> performSearch(query) }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            viewModelState.update { it.copy(isSearching = true) }

            // 1. Local DB search
            launch { searchLocal(query) }

            // 2. Extension sources search
            launch { searchExtensions(query) }
        }
    }

    private suspend fun searchLocal(query: String) {
        try {
            val results = withContext(Dispatchers.IO) {
                database.getMangaDao().searchByTitle("%$query%", 10)
            }
            if (results.isNotEmpty()) {
                val section = SearchResultSection(
                    title = "Library & History",
                    manga = results.map { it.toHomeManga() },
                )
                viewModelState.update { state ->
                    val existing = state.searchResults.filter { it.title != "Library & History" }
                    state.copy(searchResults = listOf(section) + existing)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Local search failed", e)
        }
    }

    private suspend fun searchExtensions(query: String) {
        val sources = extensionManager.getCatalogueSources()
        if (sources.isEmpty()) {
            viewModelState.update { it.copy(isSearching = false) }
            return
        }

        // Add loading placeholders for each source
        viewModelState.update { state ->
            val localSection = state.searchResults.filter { it.title == "Library & History" }
            val sourceSections = sources.map { source ->
                SearchResultSection(
                    title = source.name,
                    sourceId = source.id,
                    isLoading = true,
                )
            }
            state.copy(searchResults = localSection + sourceSections)
        }

        // Search each source in parallel
        sources.forEach { source ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    searchSource(source, query)
                } catch (e: Exception) {
                    Log.e(TAG, "Search failed for ${source.name}", e)
                    updateSourceResult(source.id) { it.copy(isLoading = false) }
                }
            }
        }

        viewModelState.update { it.copy(isSearching = false) }
    }

    private suspend fun searchSource(source: CatalogueSource, query: String) {
        try {
            val mangasPage = withContext(Dispatchers.IO) {
                source.getSearchManga(page = 1, query = query, filters = FilterList())
            }
            val searchResults = mangasPage.mangas.take(MAX_SEARCH_PER_SOURCE).map { sManga ->
                HomeManga(
                    id = "${source.id}_${sManga.url}",
                    title = sManga.title,
                    source = source.name,
                    coverUrl = sManga.thumbnail_url,
                    accentColor = accentColors[abs(sManga.title.hashCode()) % accentColors.size],
                )
            }
            updateSourceResult(source.id) {
                it.copy(manga = searchResults, isLoading = false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching ${source.name}", e)
            updateSourceResult(source.id) { it.copy(isLoading = false) }
        }
    }

    private fun updateSourceResult(
        sourceId: Long,
        transform: (SearchResultSection) -> SearchResultSection,
    ) {
        viewModelState.update { state ->
            state.copy(
                searchResults = state.searchResults.map { result ->
                    if (result.sourceId == sourceId) transform(result) else result
                }
            )
        }
    }

    // ---- Filters ----

    fun onFilterSelected(filter: String) {
        viewModelState.update { it.copy(selectedFilter = filter) }
    }

    // ---- History grouping (Futon-style) ----

    private fun groupByTime(mangaList: List<MangaEntity>): List<HomeSection> {
        if (mangaList.isEmpty()) return emptyList()

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        // Get start-of-today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        val yesterdayStart = todayStart - TimeUnit.DAYS.toMillis(1)

        // For now, use a simple heuristic based on manga ID ordering
        // In a real app, this would use HistoryEntity.updatedAt
        val sections = mutableListOf<HomeSection>()
        val perSection = (mangaList.size + 2) / 3 // divide into ~3 groups

        val groups = mangaList.chunked(maxOf(perSection, 1))
        val labels = listOf("Today", "Yesterday", "Earlier this week")

        groups.forEachIndexed { index, chunk ->
            val label = labels.getOrElse(index) { "${index + 1} days ago" }
            sections.add(
                HomeSection(
                    title = label,
                    manga = chunk.map { it.toHomeManga() },
                )
            )
        }

        return sections
    }

    private fun MangaEntity.toHomeManga(): HomeManga {
        return HomeManga(
            id = id.toString(),
            title = title,
            source = source,
            coverUrl = coverUrl.ifBlank { null },
            accentColor = accentColors[abs(id.hashCode()) % accentColors.size],
        )
    }
}
