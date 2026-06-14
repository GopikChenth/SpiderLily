package com.arcadelabs.spiderlily.features.home.domain.model

import androidx.compose.ui.graphics.Color

data class HomeManga(
    val id: String,
    val title: String,
    val source: String,
    val coverUrl: String? = null,
    val chapterLabel: String = "",
    val progressPercent: Int = 0,
    val accentColor: Color = Color(0xFF5C4033),
)
