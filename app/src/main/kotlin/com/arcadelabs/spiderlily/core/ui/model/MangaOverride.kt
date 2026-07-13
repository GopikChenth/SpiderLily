package com.arcadelabs.spiderlily.core.ui.model

import com.arcadelabs.spiderlily_parser.model.ContentRating

data class MangaOverride(
	val coverUrl: String?,
	val title: String?,
	val contentRating: ContentRating?,
)
