package com.arcadelabs.spiderlily.core.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import coil3.request.ImageResult
import com.arcadelabs.spiderlily.core.model.unwrap
import com.arcadelabs.spiderlily.core.network.CommonHeaders
import com.arcadelabs.spiderlily.core.util.ext.mangaSourceKey
import com.arcadelabs.spiderlily_parser.model.MangaParserSource

class MangaSourceHeaderInterceptor : Interceptor {

	override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
		val mangaSource = chain.request.extras[mangaSourceKey]?.unwrap() as? MangaParserSource ?: return chain.proceed()
		val request = chain.request
		val newHeaders = request.httpHeaders.newBuilder()
			.set(CommonHeaders.MANGA_SOURCE, mangaSource.name)
			.build()
		val newRequest = request.newBuilder()
			.httpHeaders(newHeaders)
			.build()
		return chain.withRequest(newRequest).proceed()
	}
}
