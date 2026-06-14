package com.arcadelabs.spiderlily.features.explore.domain.model

import androidx.compose.ui.graphics.Color

data class ExploreMangaSource(
    val id: String,
    val name: String,
    val language: String,
    val isPinned: Boolean,
    val iconUrl: String? = null,
)

data class ExploreSuggestion(
    val id: String,
    val title: String,
    val source: String,
    val coverUrl: String? = null,
    val tags: List<String> = emptyList(),
    val progressPercent: Int = 0,
    val accentColor: Color = Color(0xFFE35A7A),
)
