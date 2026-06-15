package com.arcadelabs.spiderlily.mihon.parsers.core

import com.arcadelabs.spiderlily.mihon.parsers.ContentParser
import com.arcadelabs.spiderlily.mihon.parsers.ContentParserAuthProvider
import com.arcadelabs.spiderlily.mihon.parsers.FavoritesProvider
import com.arcadelabs.spiderlily.mihon.parsers.FavoritesSyncProvider
import com.arcadelabs.spiderlily.mihon.parsers.model.Content
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentChapter
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentListFilter
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentListFilterOptions
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentPage
import com.arcadelabs.spiderlily.mihon.parsers.model.Favicons
import com.arcadelabs.spiderlily.mihon.parsers.model.SortOrder
import com.arcadelabs.spiderlily.mihon.parsers.model.search.ContentSearchQuery
import com.arcadelabs.spiderlily.mihon.parsers.util.mergeWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class ContentParserWrapper(
    private val delegate: ContentParser,
) : ContentParser by delegate {

	internal val favoritesProvider: FavoritesProvider? = delegate as? FavoritesProvider
	internal val favoritesSyncProvider: FavoritesSyncProvider? = delegate as? FavoritesSyncProvider

	override val authorizationProvider: ContentParserAuthProvider?
		get() = delegate as? ContentParserAuthProvider

	@Deprecated("Too complex. Use getList with filter instead")
	override suspend fun getList(query: ContentSearchQuery): List<Content> = withContext(Dispatchers.Default) {
		if (!query.skipValidation) {
			searchQueryCapabilities.validate(query)
		}
		delegate.getList(query)
	}

	override suspend fun getList(
        offset: Int,
        order: SortOrder,
        filter: ContentListFilter,
	): List<Content> = withContext(Dispatchers.Default) {
		delegate.getList(offset, order, filter)
	}

	override suspend fun getDetails(manga: Content): Content = withContext(Dispatchers.Default) {
		delegate.getDetails(manga)
	}

	override suspend fun getPages(chapter: ContentChapter): List<ContentPage> = withContext(Dispatchers.Default) {
		delegate.getPages(chapter)
	}

	override suspend fun getPageUrl(page: ContentPage): String = withContext(Dispatchers.Default) {
		delegate.getPageUrl(page)
	}

	override suspend fun getFilterOptions(): ContentListFilterOptions = withContext(Dispatchers.Default) {
		delegate.getFilterOptions()
	}

	override suspend fun getFavicons(): Favicons = withContext(Dispatchers.Default) {
		delegate.getFavicons()
	}

	override suspend fun getRelatedContent(seed: Content): List<Content> = withContext(Dispatchers.Default) {
		delegate.getRelatedContent(seed)
	}

	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val headers = request.headers.newBuilder()
			.mergeWith(delegate.getRequestHeaders(), replaceExisting = false)
			.build()
		val newRequest = request.newBuilder().headers(headers).build()
		return delegate.intercept(ProxyChain(chain, newRequest))
	}

	private class ProxyChain(
		private val delegate: Interceptor.Chain,
		private val request: Request,
	) : Interceptor.Chain by delegate {

		override fun request(): Request = request
	}
}
