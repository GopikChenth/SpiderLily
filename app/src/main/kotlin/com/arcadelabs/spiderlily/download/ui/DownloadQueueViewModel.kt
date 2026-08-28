package com.arcadelabs.spiderlily.download.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.TriStateOption
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.download.data.entity.DownloadQueueEntity
import com.arcadelabs.spiderlily.download.data.repository.DownloadQueueRepository
import com.arcadelabs.spiderlily.download.domain.usecase.QueueAllUnreadFromFavoritesUseCase
import com.arcadelabs.spiderlily.download.ui.worker.DownloadSchedulerWorker
import com.arcadelabs.spiderlily.local.domain.EnforceStorageQuotaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class DownloadQueueViewModel @Inject constructor(
    private val downloadQueueRepository: DownloadQueueRepository,
    private val mangaDataRepository: MangaDataRepository,
    private val queueAllUnreadFromFavoritesUseCase: QueueAllUnreadFromFavoritesUseCase,
    private val enforceStorageQuotaUseCase: EnforceStorageQuotaUseCase,
    private val settings: AppSettings,
    private val workManager: androidx.work.WorkManager,
) : BaseViewModel() {

    val storageUsage = MutableStateFlow<EnforceStorageQuotaUseCase.StorageUsage?>(null)

    val queue: StateFlow<List<DownloadQueueItem>> = downloadQueueRepository.observeQueue()
        .map { entities ->
            entities.map { entity ->
                val manga = mangaDataRepository.findMangaById(entity.mangaId, withChapters = false)
                DownloadQueueItem(entity, manga)
            }
        }
        .stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, emptyList())

    init {
        refreshStorageUsage()
    }

    fun refreshStorageUsage() {
        launchJob(Dispatchers.IO) {
            storageUsage.value = enforceStorageQuotaUseCase.getUsage()
        }
    }

    fun queueAllUnreadFromFavorites() {
        launchJob(Dispatchers.IO) {
            val wifiOnly = settings.allowDownloadOnMeteredNetwork == TriStateOption.DISABLED
            queueAllUnreadFromFavoritesUseCase(
                wifiOnly = wifiOnly,
                chargingOnly = settings.isDownloadOnlyOnChargingEnabled,
                offPeakOnly = settings.isDownloadOffPeakEnabled
            )
        }
    }

    fun removeFromQueue(id: Long) {
        launchJob(Dispatchers.IO) {
            downloadQueueRepository.removeFromQueue(id)
        }
    }

    fun updatePaused(id: Long, isPaused: Boolean) {
        launchJob(Dispatchers.IO) {
            downloadQueueRepository.updatePaused(id, isPaused)
        }
    }

    fun reorderQueue(ids: List<Long>) {
        launchJob(Dispatchers.IO) {
            downloadQueueRepository.updateQueueOrder(ids)
        }
    }

    fun clearQueue() {
        launchJob(Dispatchers.IO) {
            downloadQueueRepository.clearQueue()
        }
    }

    fun triggerScheduler() {
        DownloadSchedulerWorker.enqueue(workManager, force = true)
    }
}

data class DownloadQueueItem(
    val entity: DownloadQueueEntity,
    val manga: com.arcadelabs.spiderlily_parser.model.Manga?,
)
