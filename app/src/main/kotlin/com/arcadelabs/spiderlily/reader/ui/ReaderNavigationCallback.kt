package com.arcadelabs.spiderlily.reader.ui

import com.arcadelabs.spiderlily.bookmarks.domain.Bookmark
import org.koitharu.kotatsu.parsers.model.MangaChapter
import com.arcadelabs.spiderlily.reader.ui.pager.ReaderPage

interface ReaderNavigationCallback {

	fun onPageSelected(page: ReaderPage): Boolean

	fun onChapterSelected(chapter: MangaChapter): Boolean

	fun onBookmarkSelected(bookmark: Bookmark): Boolean = onPageSelected(
		ReaderPage(bookmark.toMangaPage(), bookmark.page, bookmark.chapterId),
	)
}
