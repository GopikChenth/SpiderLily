package com.arcadelabs.spiderlily.scrobbling.anilist.data

import com.arcadelabs.spiderlily.core.network.CommonHeaders
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.scrobbling.common.data.ScrobblerStorage
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerService
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerType
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class AniListAuthenticator @Inject constructor(
	@ScrobblerType(ScrobblerService.ANILIST) private val storage: ScrobblerStorage,
	private val repositoryProvider: Provider<AniListRepository>,
) : Authenticator {

	override fun authenticate(route: Route?, response: Response): Request? {
		val accessToken = storage.accessToken ?: return null
		if (!isRequestWithAccessToken(response)) {
			return null
		}
		synchronized(this) {
			val newAccessToken = storage.accessToken ?: return null
			if (accessToken != newAccessToken) {
				return newRequestWithAccessToken(response.request, newAccessToken)
			}
			val updatedAccessToken = refreshAccessToken() ?: return null
			return newRequestWithAccessToken(response.request, updatedAccessToken)
		}
	}

	private fun isRequestWithAccessToken(response: Response): Boolean {
		val header = response.request.header(CommonHeaders.AUTHORIZATION)
		return header?.startsWith("Bearer") == true
	}

	private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
		return request.newBuilder()
			.header(CommonHeaders.AUTHORIZATION, "Bearer $accessToken")
			.build()
	}

	private fun refreshAccessToken(): String? = runCatching {
		val repository = repositoryProvider.get()
		runBlocking { repository.authorize(null) }
		return storage.accessToken
	}.onFailure {
		it.printStackTraceDebug("AniListAuthenticator::refreshAccessToken")
	}.getOrNull()
}
