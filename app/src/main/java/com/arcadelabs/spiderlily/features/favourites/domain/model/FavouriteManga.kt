package com.arcadelabs.spiderlily.features.favourites.domain.model

import androidx.compose.ui.graphics.Color

data class FavouriteManga(
    val id: String,
    val title: String,
    val source: String,
    val latestChapter: String,
    val unreadChapters: Int,
    val progressPercent: Int,
    val categoryIds: Set<String>,
    val accentColor: Color,
)
