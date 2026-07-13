package com.arcadelabs.spiderlily.history.domain.model

import com.arcadelabs.spiderlily.core.model.MangaHistory
import com.arcadelabs.spiderlily_parser.model.Manga

data class MangaWithHistory(
	val manga: Manga,
	val history: MangaHistory
)
