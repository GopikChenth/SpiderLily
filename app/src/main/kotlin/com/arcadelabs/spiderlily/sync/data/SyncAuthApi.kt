package com.arcadelabs.spiderlily.sync.data

import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.arcadelabs.spiderlily.core.exceptions.SyncApiException
import com.arcadelabs.spiderlily.core.network.BaseHttpClient
import com.arcadelabs.spiderlily.core.util.ext.toRequestBody
import com.arcadelabs.spiderlily_parser.util.await
import com.arcadelabs.spiderlily_parser.util.parseJson
import com.arcadelabs.spiderlily_parser.util.parseRaw
import com.arcadelabs.spiderlily_parser.util.removeSurrounding
import javax.inject.Inject

@Reusable
class SyncAuthApi @Inject constructor(
	@BaseHttpClient private val okHttpClient: OkHttpClient,
) {

	suspend fun authenticate(syncURL: String, email: String, password: String): String {
		val body = JSONObject(
			mapOf("email" to email, "password" to password),
		).toRequestBody()
		val request = Request.Builder()
			.url("$syncURL/auth")
			.post(body)
			.build()
		val response = okHttpClient.newCall(request).await()
		if (response.isSuccessful) {
			return response.parseJson().getString("token")
		} else {
			val code = response.code
			val message = response.parseRaw().removeSurrounding('"')
			throw SyncApiException(message, code)
		}
	}
}
