package com.arcadelabs.spiderlily.history.domain

import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.core.util.ext.processLifecycleScope
import com.arcadelabs.spiderlily.history.data.HistoryRepository
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import com.arcadelabs.spiderlily.reader.ui.ReaderState
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryUpdateUseCase @Inject constructor(
	private val historyRepository: HistoryRepository,
) {

	suspend operator fun invoke(manga: Manga, readerState: ReaderState, percent: Float) {
		historyRepository.addOrUpdate(
			manga = manga,
			chapterId = readerState.chapterId,
			page = readerState.page,
			scroll = readerState.scroll,
			percent = percent,
			force = false,
		)
	}

	fun invokeAsync(
		manga: Manga,
		readerState: ReaderState,
		percent: Float
	) = processLifecycleScope.launch(Dispatchers.IO, CoroutineStart.ATOMIC) {
		runCatchingCancellable {
			withContext(NonCancellable) {
				invoke(manga, readerState, percent)
			}
		}.onFailure {
			it.printStackTraceDebug("HistoryUpdateUseCase::invokeAsync")
		}
	}
}
