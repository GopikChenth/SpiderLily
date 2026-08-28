package com.arcadelabs.spiderlily.download.domain.usecase

import androidx.work.WorkManager
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.download.ui.worker.DownloadSchedulerWorker
import com.arcadelabs.spiderlily.favourites.data.toManga
import com.arcadelabs.spiderlily.mihon.parsers.util.runCatchingCancellable
import javax.inject.Inject

class QueueAllUnreadFromFavoritesUseCase @Inject constructor(
    private val db: MangaDatabase,
    private val addUnreadToQueueUseCase: AddUnreadToQueueUseCase,
    private val workManager: WorkManager,
) {
    suspend operator fun invoke(wifiOnly: Boolean, chargingOnly: Boolean, offPeakOnly: Boolean) {
        runCatchingCancellable {
            val favorites = db.getFavouritesDao().findAll()
            favorites.forEach { favorite ->
                addUnreadToQueueUseCase(
                    manga = favorite.toManga(),
                    wifiOnly = wifiOnly,
                    chargingOnly = chargingOnly,
                    offPeakOnly = offPeakOnly,
                )
            }
            DownloadSchedulerWorker.enqueue(workManager)
        }.onFailure {
            it.printStackTraceDebug()
        }
    }
}
