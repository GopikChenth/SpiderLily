package com.arcadelabs.spiderlily.download.ui.worker

import android.os.SystemClock
import androidx.collection.MutableObjectLongMap
import kotlinx.coroutines.delay
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.parser.ParserMangaRepository
import com.arcadelabs.spiderlily_parser.model.MangaSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadSlowdownDispatcher @Inject constructor(
	private val mangaRepositoryFactory: MangaRepository.Factory,
) {
	private val timeMap = MutableObjectLongMap<MangaSource>()
	private val defaultDelay = 1_600L

	suspend fun delay(source: MangaSource) {
		val repo = mangaRepositoryFactory.create(source) as? ParserMangaRepository ?: return
		if (!repo.isSlowdownEnabled()) {
			return
		}
		val lastRequest = synchronized(timeMap) {
			val res = timeMap.getOrDefault(source, 0L)
			timeMap[source] = SystemClock.elapsedRealtime()
			res
		}
		if (lastRequest != 0L) {
			delay(lastRequest + defaultDelay - SystemClock.elapsedRealtime())
		}
	}
}
