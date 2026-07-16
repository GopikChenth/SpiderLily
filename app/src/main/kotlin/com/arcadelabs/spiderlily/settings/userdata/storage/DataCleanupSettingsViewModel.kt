package com.arcadelabs.spiderlily.settings.userdata.storage

import android.annotation.SuppressLint
import android.webkit.WebStorage
import androidx.webkit.WebStorageCompat
import androidx.webkit.WebViewFeature
import coil3.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runInterruptible
import okhttp3.Cache
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.network.cookies.MutableCookieJar
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.ui.util.ReversibleAction
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.local.data.CacheDir
import com.arcadelabs.spiderlily.local.data.LocalStorageManager
import com.arcadelabs.spiderlily.local.domain.DeleteReadChaptersUseCase
import com.arcadelabs.spiderlily.search.domain.MangaSearchRepository
import com.arcadelabs.spiderlily.tracker.domain.TrackingRepository
import java.util.EnumMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class DataCleanupSettingsViewModel @Inject constructor(
    private val storageManager: LocalStorageManager,
    private val httpCache: Cache,
    private val searchRepository: MangaSearchRepository,
    private val trackingRepository: TrackingRepository,
    private val cookieJar: MutableCookieJar,
    private val deleteReadChaptersUseCase: DeleteReadChaptersUseCase,
    private val mangaDataRepositoryProvider: Provider<MangaDataRepository>,
    private val coil: ImageLoader,
) : BaseViewModel() {

    val onActionDone = MutableEventFlow<ReversibleAction>()
    val loadingKeys = MutableStateFlow(emptySet<String>())

    val searchHistoryCount = MutableStateFlow(-1)
    val feedItemsCount = MutableStateFlow(-1)
    val httpCacheSize = MutableStateFlow(-1L)
    val cacheSizes = EnumMap<CacheDir, MutableStateFlow<Long>>(CacheDir::class.java)

    val onChaptersCleanedUp = MutableEventFlow<Pair<Int, Long>>()

    val isBrowserDataCleanupEnabled: Boolean
        get() = WebViewFeature.isFeatureSupported(WebViewFeature.DELETE_BROWSING_DATA)

    init {
        CacheDir.entries.forEach {
            cacheSizes[it] = MutableStateFlow(-1L)
        }
        launchJob(Dispatchers.IO) {
            searchHistoryCount.value = searchRepository.getSearchHistoryCount()
        }
        launchJob(Dispatchers.IO) {
            feedItemsCount.value = trackingRepository.getLogsCount()
        }
        CacheDir.entries.forEach { cache ->
            launchJob(Dispatchers.IO) {
                checkNotNull(cacheSizes[cache]).value = storageManager.computeCacheSize(cache)
            }
        }
        launchJob(Dispatchers.IO) {
            httpCacheSize.value = runInterruptible { httpCache.size() }
        }
    }

    fun clearCache(key: String, vararg caches: CacheDir) {
        launchJob(Dispatchers.IO) {
            try {
                loadingKeys.update { it + key }
                for (cache in caches) {
                    storageManager.clearCache(cache)
                    checkNotNull(cacheSizes[cache]).value = storageManager.computeCacheSize(cache)
                    if (cache == CacheDir.THUMBS) {
                        coil.memoryCache?.clear()
                    }
                }
            } finally {
                loadingKeys.update { it - key }
            }
        }
    }

    fun clearHttpCache() {
        launchJob(Dispatchers.IO) {
            try {
                loadingKeys.update { it + AppSettings.KEY_HTTP_CACHE_CLEAR }
                val size = runInterruptible(Dispatchers.IO) {
                    httpCache.evictAll()
                    httpCache.size()
                }
                httpCacheSize.value = size
            } finally {
                loadingKeys.update { it - AppSettings.KEY_HTTP_CACHE_CLEAR }
            }
        }
    }

    fun clearSearchHistory() {
        launchJob(Dispatchers.IO) {
            searchRepository.clearSearchHistory()
            searchHistoryCount.value = searchRepository.getSearchHistoryCount()
            onActionDone.call(ReversibleAction(R.string.search_history_cleared, null))
        }
    }

    fun clearCookies() {
        launchJob {
            cookieJar.clear()
            onActionDone.call(ReversibleAction(R.string.cookies_cleared, null))
        }
    }

    @SuppressLint("RequiresFeature")
    fun clearBrowserData() {
        launchJob {
            try {
                loadingKeys.update { it + AppSettings.KEY_WEBVIEW_CLEAR }
                val storage = WebStorage.getInstance()
                suspendCoroutine { cont ->
                    WebStorageCompat.deleteBrowsingData(storage) {
                        cont.resume(Unit)
                    }
                }
                onActionDone.call(ReversibleAction(R.string.updates_feed_cleared, null))
            } finally {
                loadingKeys.update { it - AppSettings.KEY_WEBVIEW_CLEAR }
            }
        }
    }

    fun clearUpdatesFeed() {
        launchJob(Dispatchers.IO) {
            try {
                loadingKeys.update { it + AppSettings.KEY_UPDATES_FEED_CLEAR }
                trackingRepository.clearLogs()
                feedItemsCount.value = trackingRepository.getLogsCount()
                onActionDone.call(ReversibleAction(R.string.updates_feed_cleared, null))
            } finally {
                loadingKeys.update { it - AppSettings.KEY_UPDATES_FEED_CLEAR }
            }
        }
    }

    fun clearMangaData() {
        launchJob(Dispatchers.IO) {
            try {
                loadingKeys.update { it + AppSettings.KEY_CLEAR_MANGA_DATA }
                trackingRepository.gc()
                val repository = mangaDataRepositoryProvider.get()
                repository.cleanupLocalManga()
                repository.cleanupDatabase()
                onActionDone.call(ReversibleAction(R.string.updates_feed_cleared, null))
            } finally {
                loadingKeys.update { it - AppSettings.KEY_CLEAR_MANGA_DATA }
            }
        }
    }

    fun cleanupChapters() {
        launchJob(Dispatchers.IO) {
            try {
                loadingKeys.update { it + AppSettings.KEY_CHAPTERS_CLEAR }
                val oldSize = storageManager.computeStorageSize()
                val chaptersCount = deleteReadChaptersUseCase.invoke()
                val newSize = storageManager.computeStorageSize()
                onChaptersCleanedUp.call(chaptersCount to oldSize - newSize)
            } finally {
                loadingKeys.update { it - AppSettings.KEY_CHAPTERS_CLEAR }
            }
        }
    }
}
