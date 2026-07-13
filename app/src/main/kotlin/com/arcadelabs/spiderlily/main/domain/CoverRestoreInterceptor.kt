package com.arcadelabs.spiderlily.main.domain

import androidx.collection.ArraySet
import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageResult
import com.arcadelabs.spiderlily.bookmarks.domain.Bookmark
import com.arcadelabs.spiderlily.bookmarks.domain.BookmarksRepository
import com.arcadelabs.spiderlily.core.model.isLocal
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.util.ext.bookmarkKey
import com.arcadelabs.spiderlily.core.util.ext.mangaKey
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.util.findById
import com.arcadelabs.spiderlily_parser.util.ifNullOrEmpty
import com.arcadelabs.spiderlily_parser.util.runCatchingCancellable
import java.util.Collections
import javax.inject.Inject

class CoverRestoreInterceptor @Inject constructor(
	private val dataRepository: MangaDataRepository,
	private val bookmarksRepository: BookmarksRepository,
	private val repositoryFactory: MangaRepository.Factory,
) : Interceptor {

	private val blacklist = Collections.synchronizedSet(ArraySet<String>())

	override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
		val request = chain.request
		val result = chain.proceed()
		if (result is ErrorResult && result.throwable.shouldRestore()) {
			request.extras[bookmarkKey]?.let {
				return if (restoreBookmark(it)) {
					chain.withRequest(request.newBuilder().build()).proceed()
				} else {
					result
				}
			}
			request.extras[mangaKey]?.let {
				return if (restoreManga(it)) {
					chain.withRequest(request.newBuilder().build()).proceed()
				} else {
					result
				}
			}
		}
		return result
	}

	private suspend fun restoreManga(manga: Manga): Boolean {
		val key = manga.publicUrl
		if (!blacklist.add(key)) {
			return false
		}
		val restored = runCatchingCancellable {
			restoreMangaImpl(manga)
		}.onFailure { e ->
			e.printStackTraceDebug("CoverRestoreInterceptor::restoreManga")
		}.getOrDefault(false)
		if (restored) {
			blacklist.remove(key)
		}
		return restored
	}

	private suspend fun restoreMangaImpl(manga: Manga): Boolean {
		if (dataRepository.findMangaById(manga.id, withChapters = false) == null || manga.isLocal) {
			return false
		}
		val repo = repositoryFactory.create(manga.source)
		val fixed = repo.find(manga) ?: return false
		return if (fixed != manga) {
			dataRepository.storeManga(fixed, replaceExisting = true)
			fixed.coverUrl != manga.coverUrl
		} else {
			false
		}
	}

	private suspend fun restoreBookmark(bookmark: Bookmark): Boolean {
		val key = bookmark.imageUrl
		if (!blacklist.add(key)) {
			return false
		}
		val restored = runCatchingCancellable {
			restoreBookmarkImpl(bookmark)
		}.onFailure { e ->
			e.printStackTraceDebug("CoverRestoreInterceptor::restoreBookmark")
		}.getOrDefault(false)
		if (restored) {
			blacklist.remove(key)
		}
		return restored
	}

	private suspend fun restoreBookmarkImpl(bookmark: Bookmark): Boolean {
		if (bookmark.manga.isLocal) {
			return false
		}
		val repo = repositoryFactory.create(bookmark.manga.source)
		val chapter = repo.getDetails(bookmark.manga).chapters?.findById(bookmark.chapterId) ?: return false
		val page = repo.getPages(chapter)[bookmark.page]
		val imageUrl = page.preview.ifNullOrEmpty { page.url }
		return if (imageUrl != bookmark.imageUrl) {
			bookmarksRepository.updateBookmark(bookmark, imageUrl)
			true
		} else {
			false
		}
	}

	private fun Throwable.shouldRestore(): Boolean {
		return this is Exception // any Exception but not Error
	}
}
