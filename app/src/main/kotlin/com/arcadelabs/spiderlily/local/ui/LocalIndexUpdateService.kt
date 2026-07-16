package com.arcadelabs.spiderlily.local.ui

import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.core.ui.CoroutineIntentService
import com.arcadelabs.spiderlily.local.data.index.LocalMangaIndex
import javax.inject.Inject

@AndroidEntryPoint
class LocalIndexUpdateService : CoroutineIntentService() {

	@Inject
	lateinit var localMangaIndex: LocalMangaIndex

	override suspend fun IntentJobContext.processIntent(intent: Intent) {
		localMangaIndex.update()
	}

	override fun IntentJobContext.onError(error: Throwable) = Unit
}
