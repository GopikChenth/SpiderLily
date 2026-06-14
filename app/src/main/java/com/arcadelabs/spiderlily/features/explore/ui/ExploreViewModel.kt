package com.arcadelabs.spiderlily.features.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.features.explore.data.FakeExploreRepository
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreMangaSource
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreSuggestion
import com.arcadelabs.spiderlily.features.explore.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val searchQuery: String = "",
    val suggestions: List<ExploreSuggestion> = emptyList(),
    val sources: List<ExploreMangaSource> = emptyList(),
)

class ExploreViewModel(
    private val repository: ExploreRepository = FakeExploreRepository(),
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ExploreUiState())

    val uiState: StateFlow<ExploreUiState> = combine(
        viewModelState,
        repository.observeSources(),
        repository.observeSuggestions(),
    ) { state, sources, suggestions ->
        val filteredSources = if (state.searchQuery.isBlank()) {
            sources
        } else {
            sources.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        }
        state.copy(
            sources = filteredSources,
            suggestions = suggestions,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExploreUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        viewModelState.update { it.copy(searchQuery = query) }
    }

    fun togglePinSource(sourceId: String) {
        viewModelScope.launch {
            repository.togglePinSource(sourceId)
        }
    }
}
