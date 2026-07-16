package com.arcadelabs.spiderlily.core.model

import com.arcadelabs.spiderlily_parser.model.MangaSource

data class MangaSourceInfo(
	val mangaSource: MangaSource,
	val isEnabled: Boolean,
	val isPinned: Boolean,
) : MangaSource by mangaSource
