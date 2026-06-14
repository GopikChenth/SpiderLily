package com.arcadelabs.spiderlily.features.explore.domain.repository

import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreMangaSource
import com.arcadelabs.spiderlily.features.explore.domain.model.ExploreSuggestion
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    fun observeSources(): Flow<List<ExploreMangaSource>>
    fun observeSuggestions(): Flow<List<ExploreSuggestion>>
    suspend fun togglePinSource(sourceId: String)
    suspend fun getRandomManga(): ExploreSuggestion
}
