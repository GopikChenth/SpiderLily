package com.arcadelabs.spiderlily.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.local.data.CacheDir
import com.arcadelabs.spiderlily.local.data.LocalStorageManager
import com.arcadelabs.spiderlily.settings.userdata.storage.StorageUsage
import javax.inject.Inject

@HiltViewModel
class StorageAndNetworkSettingsViewModel @Inject constructor(
    private val storageManager: LocalStorageManager,
) : BaseViewModel() {

    val storageUsage: StateFlow<StorageUsage?> = flow {
        emit(loadStorageUsage())
    }.withErrorHandling()
        .stateIn(viewModelScope + Dispatchers.IO, SharingStarted.WhileSubscribed(1000), null)

    private suspend fun loadStorageUsage(): StorageUsage {
        val pagesCacheSize = storageManager.computeCacheSize(CacheDir.PAGES)
        val otherCacheSize = storageManager.computeCacheSize() - pagesCacheSize
        val storageSize = storageManager.computeStorageSize()
        val availableSpace = storageManager.computeAvailableSize()
        val totalBytes = pagesCacheSize + otherCacheSize + storageSize + availableSpace
        return StorageUsage(
            savedManga = StorageUsage.Item(
                bytes = storageSize,
                percent = (storageSize.toDouble() / totalBytes).toFloat(),
            ),
            pagesCache = StorageUsage.Item(
                bytes = pagesCacheSize,
                percent = (pagesCacheSize.toDouble() / totalBytes).toFloat(),
            ),
            otherCache = StorageUsage.Item(
                bytes = otherCacheSize,
                percent = (otherCacheSize.toDouble() / totalBytes).toFloat(),
            ),
            available = StorageUsage.Item(
                bytes = availableSpace,
                percent = (availableSpace.toDouble() / totalBytes).toFloat(),
            ),
        )
    }
}
