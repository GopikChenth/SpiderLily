package com.arcadelabs.spiderlily.tracker.domain.model

import com.arcadelabs.spiderlily_parser.model.Manga
import java.time.Instant

data class TrackingLogItem(
	val id: Long,
	val manga: Manga,
	val chapters: List<String>,
	val createdAt: Instant,
	val isNew: Boolean,
)
