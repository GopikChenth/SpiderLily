package com.arcadelabs.spiderlily.features.home.domain.model

import androidx.compose.ui.graphics.Color

data class HomeManga(
    val id: String,
    val title: String,
    val source: String,
    val chapterLabel: String,
    val progressPercent: Int,
    val accentColor: Color,
)
