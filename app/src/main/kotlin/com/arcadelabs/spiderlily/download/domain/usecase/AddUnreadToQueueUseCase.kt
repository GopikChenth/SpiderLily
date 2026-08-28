package com.arcadelabs.spiderlily.download.domain.usecase

import android.util.Log
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.download.data.repository.DownloadQueueRepository
import com.arcadelabs.spiderlily.mihon.parsers.util.runCatchingCancellable
import com.arcadelabs.spiderlily_parser.model.Manga
import javax.inject.Inject

class AddUnreadToQueueUseCase @Inject constructor(
    private val db: MangaDatabase,
    private val downloadQueueRepository: DownloadQueueRepository,
) {
    suspend operator fun invoke(manga: Manga, wifiOnly: Boolean, chargingOnly: Boolean, offPeakOnly: Boolean) {
        runCatchingCancellable {
            val history = db.getHistoryDao().find(manga.id)
            val lastChapterId = history?.chapterId ?: -1L
            
            val chapters = db.getChaptersDao().findAll(manga.id)
            val lastChapterIndex = chapters.find { it.chapterId == lastChapterId }?.index ?: -1
            
            val unreadChaptersIds = chapters
                .filter { it.index > lastChapterIndex }
                .map { it.chapterId }
                .toLongArray()

            Log.d("AddUnreadToQueue", "Found ${unreadChaptersIds.size} unread chapters for ${manga.title}")

            if (unreadChaptersIds.isNotEmpty()) {
                downloadQueueRepository.addToQueue(
                    manga = manga,
                    chaptersIds = unreadChaptersIds,
                    wifiOnly = wifiOnly,
                    chargingOnly = chargingOnly,
                    offPeakOnly = offPeakOnly
                )
            }
        }.onFailure {
            it.printStackTraceDebug()
        }
    }
}
