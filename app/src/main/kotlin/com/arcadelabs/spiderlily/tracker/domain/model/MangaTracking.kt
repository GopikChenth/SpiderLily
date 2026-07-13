package com.arcadelabs.spiderlily.tracker.domain.model

import com.arcadelabs.spiderlily_parser.model.Manga
import java.time.Instant

data class MangaTracking(
	val manga: Manga,
	val lastChapterId: Long,
	val lastCheck: Instant?,
	val lastChapterDate: Instant?,
	val newChapters: Int,
) {

	fun isEmpty(): Boolean {
		return lastChapterId == 0L
	}
}
