package com.arcadelabs.spiderlily.reader.data

import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaChapter

fun Manga.filterChapters(branch: String?): Manga {
	if (chapters.isNullOrEmpty()) return this
	return withChapters(chapters = chapters?.filter { it.branch == branch })
}

private fun Manga.withChapters(chapters: List<MangaChapter>?) = copy(
	chapters = chapters,
)
