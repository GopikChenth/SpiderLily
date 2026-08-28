package com.arcadelabs.spiderlily.local.domain

import android.util.Log
import com.arcadelabs.spiderlily.core.model.ids
import com.arcadelabs.spiderlily.core.model.isLocal
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.favourites.domain.FavouritesRepository
import com.arcadelabs.spiderlily.history.data.HistoryRepository
import com.arcadelabs.spiderlily.local.data.LocalMangaRepository
import com.arcadelabs.spiderlily.local.domain.model.LocalManga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaChapter
import com.arcadelabs.spiderlily_parser.util.findById
import com.arcadelabs.spiderlily_parser.util.recoverCatchingCancellable
import com.arcadelabs.spiderlily_parser.util.runCatchingCancellable
import javax.inject.Inject

class DeleteReadChaptersUseCase @Inject constructor(
	private val localMangaRepository: LocalMangaRepository,
	private val historyRepository: HistoryRepository,
	private val mangaRepositoryFactory: MangaRepository.Factory,
	private val favouritesRepository: FavouritesRepository,
) {

	suspend operator fun invoke(manga: Manga, oldestOnly: Boolean = false, ignoreFavorite: Boolean = false): Int {
		if (!ignoreFavorite && favouritesRepository.isFavorite(manga.id)) {
			Log.d("DeleteReadChapters", "Skipping deletion for favorite manga: ${manga.title}")
			return 0
		}
		val localManga = if (manga.isLocal) {
			LocalManga(manga)
		} else {
			localMangaRepository.findSavedManga(manga) ?: run {
				Log.d("DeleteReadChapters", "Local manga not found for: ${manga.title}")
				return 0
			}
		}
		val task = getDeletionTask(localManga, oldestOnly) ?: run {
			Log.d("DeleteReadChapters", "No chapters to delete for: ${manga.title}")
			return 0
		}
		Log.d("DeleteReadChapters", "Deleting ${task.chaptersIds.size} chapters for: ${manga.title}")
		localMangaRepository.deleteChapters(task.manga.manga, task.chaptersIds)
		return task.chaptersIds.size
	}

	suspend operator fun invoke(): Int {
		val list = localMangaRepository.getList(0, null, null)
		if (list.isEmpty()) {
			return 0
		}
		return channelFlow {
			for (manga in list) {
				if (favouritesRepository.isFavorite(manga.id)) {
					continue
				}
				launch(Dispatchers.IO) {
					val task = runCatchingCancellable {
						getDeletionTask(LocalManga(manga))
					}.onFailure {
						it.printStackTraceDebug("DeleteReadChaptersUseCase::invoke")
					}.getOrNull()
					if (task != null) {
						send(task)
					}
				}
			}
		}.buffer().map {
			runCatchingCancellable {
				localMangaRepository.deleteChapters(it.manga.manga, it.chaptersIds)
				it.chaptersIds.size
			}.onFailure {
				it.printStackTraceDebug("DeleteReadChaptersUseCase::invoke")
			}.getOrDefault(0)
		}.fold(0) { acc, x -> acc + x }
	}

	private suspend fun getDeletionTask(manga: LocalManga, oldestOnly: Boolean = false): DeletionTask? {
		val history = historyRepository.getOne(manga.manga) ?: run {
			Log.d("DeleteReadChapters", "No history found for ${manga.manga.title}")
			return null
		}
		val allChapters = getAllChapters(manga).sortedWith(compareBy({ it.volume }, { it.number })) // Ensure oldest first
		if (allChapters.isEmpty()) {
			Log.d("DeleteReadChapters", "No chapters list found for ${manga.manga.title}")
			return null
		}
		val historyChapter = allChapters.findById(history.chapterId) ?: run {
			Log.d("DeleteReadChapters", "History chapter ${history.chapterId} not found in all chapters for ${manga.manga.title}")
			return null
		}
		val branch = historyChapter.branch
		val readChapters = allChapters.filter { x -> x.branch == branch }.takeWhile { it.id != history.chapterId }
		
		if (readChapters.isEmpty()) {
			Log.d("DeleteReadChapters", "No read chapters before ${history.chapterId} (number: ${historyChapter.number}) in branch $branch for ${manga.manga.title}")
			return null
		}

		// Only consider chapters that are actually downloaded
		val downloadedChapters = localMangaRepository.getDetails(manga.manga).chapters.orEmpty()
		val downloadedIds = downloadedChapters.ids()
		
		val toDelete = readChapters.filter { it.id in downloadedIds }
		
		if (toDelete.isEmpty()) {
			Log.d("DeleteReadChapters", "All ${readChapters.size} read chapters in branch $branch are already deleted for ${manga.manga.title}")
			return null
		}

		Log.d("DeleteReadChapters", "Found ${toDelete.size} read and downloaded chapters for ${manga.manga.title}. oldestOnly=$oldestOnly. oldest chapter number: ${toDelete.first().number}")
		
		return DeletionTask(
			manga = manga,
			chaptersIds = if (oldestOnly) setOf(toDelete.first().id) else toDelete.ids(),
		)
	}

	private suspend fun getAllChapters(manga: LocalManga): List<MangaChapter> = runCatchingCancellable {
		val remoteManga = checkNotNull(localMangaRepository.getRemoteManga(manga.manga))
		checkNotNull(mangaRepositoryFactory.create(remoteManga.source).getDetails(remoteManga).chapters)
	}.recoverCatchingCancellable {
		checkNotNull(
			manga.manga.chapters.let {
				if (it.isNullOrEmpty()) {
					localMangaRepository.getDetails(manga.manga).chapters
				} else {
					it
				}
			},
		)
	}.getOrDefault(manga.manga.chapters.orEmpty())

	private class DeletionTask(
		val manga: LocalManga,
		val chaptersIds: Set<Long>,
	)
}
