package com.arcadelabs.spiderlily.core.github

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.network.BaseHttpClient
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily_parser.util.await
import com.arcadelabs.spiderlily_parser.util.runCatchingCancellable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateRepository @Inject constructor(
	@BaseHttpClient private val okHttp: OkHttpClient,
	@ApplicationContext context: Context,
) {
	private val changelogUrl = buildString {
		append("https://raw.githubusercontent.com/")
		append(context.getString(R.string.github_updates_repo))
		append("/refs/heads/devel/CHANGELOG.md")
	}

	suspend fun fetchChangelog(): String? = withContext(Dispatchers.IO) {
		runCatchingCancellable {
			val request = Request.Builder()
				.get()
				.url(changelogUrl)
				.build()
			okHttp.newCall(request).await().body?.string()
		}.onFailure {
			it.printStackTraceDebug("AppUpdateRepository::fetchChangelog")
		}.getOrNull()
	}
}
