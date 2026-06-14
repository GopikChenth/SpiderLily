package com.arcadelabs.spiderlily.features.explore.data

import androidx.compose.ui.graphics.Color
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreMangaSource
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreSuggestion
import com.arcadelabs.spiderlily.features.explore.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeExploreRepository : ExploreRepository {
    private val _sources = MutableStateFlow(
        listOf(
            ExploreMangaSource("mangadex", "MangaDex", "EN", isPinned = true),
            ExploreMangaSource("mangareader", "MangaReader", "EN", isPinned = true),
            ExploreMangaSource("mangabuddy", "MangaBuddy", "EN", isPinned = false),
            ExploreMangaSource("local", "Local Source", "ALL", isPinned = false),
            ExploreMangaSource("nhentai", "nHentai", "EN", isPinned = false),
            ExploreMangaSource("readmanga", "ReadManga", "RU", isPinned = false),
        )
    )

    private val _suggestions = MutableStateFlow(
        listOf(
            ExploreSuggestion(
                id = "bias-train",
                title = "My Bias Gets on the Last Train",
                source = "MangaDex",
                tags = listOf("Comedy", "Romance", "Slice of Life"),
                progressPercent = 72,
                accentColor = Color(0xFFE35A7A),
            ),
            ExploreSuggestion(
                id = "fragrant-flower",
                title = "The Fragrant Flower Blooms With Dignity",
                source = "MangaDex",
                tags = listOf("Drama", "Romance", "School Life"),
                progressPercent = 100,
                accentColor = Color(0xFF5EB7D9),
            ),
            ExploreSuggestion(
                id = "novels-extra",
                title = "The Novel's Extra (Remake)",
                source = "MangaBuddy",
                tags = listOf("Action", "Fantasy", "Harem"),
                progressPercent = 100,
                accentColor = Color(0xFF735CE8),
            ),
            ExploreSuggestion(
                id = "ember-path",
                title = "The Ember Path",
                source = "MangaDex",
                tags = listOf("Action", "Adventure", "Historical"),
                progressPercent = 41,
                accentColor = Color(0xFFFF8A55),
            ),
        )
    )

    override fun observeSources(): Flow<List<ExploreMangaSource>> = _sources.asStateFlow()

    override fun observeSuggestions(): Flow<List<ExploreSuggestion>> = _suggestions.asStateFlow()

    override suspend fun togglePinSource(sourceId: String) {
        _sources.update { list ->
            list.map { source ->
                if (source.id == sourceId) {
                    source.copy(isPinned = !source.isPinned)
                } else {
                    source
                }
            }
        }
    }

    override suspend fun getRandomManga(): ExploreSuggestion {
        return _suggestions.value.random()
    }
}
