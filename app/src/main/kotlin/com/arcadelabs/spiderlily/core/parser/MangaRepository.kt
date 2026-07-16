package com.arcadelabs.spiderlily.core.parser

import android.content.Context
import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import dagger.hilt.android.qualifiers.ApplicationContext
import com.arcadelabs.spiderlily.core.cache.MemoryContentCache
import com.arcadelabs.spiderlily.core.model.LocalMangaSource
import com.arcadelabs.spiderlily.core.model.MangaSourceInfo
import com.arcadelabs.spiderlily.core.model.TestMangaSource
import com.arcadelabs.spiderlily.core.model.UnknownMangaSource
import com.arcadelabs.spiderlily.core.parser.external.ExternalMangaRepository
import com.arcadelabs.spiderlily.core.parser.external.ExternalMangaSource
import com.arcadelabs.spiderlily.local.data.LocalMangaRepository
import com.arcadelabs.spiderlily.mihon.MihonExtensionManager
import com.arcadelabs.spiderlily.mihon.MihonMangaRepository
import com.arcadelabs.spiderlily.mihon.model.MihonMangaSource
import com.arcadelabs.spiderlily_parser.MangaLoaderContext
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaChapter
import com.arcadelabs.spiderlily_parser.model.MangaListFilter
import com.arcadelabs.spiderlily_parser.model.MangaListFilterCapabilities
import com.arcadelabs.spiderlily_parser.model.MangaListFilterOptions
import com.arcadelabs.spiderlily_parser.model.MangaPage
import com.arcadelabs.spiderlily_parser.model.MangaParserSource
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.model.SortOrder
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

interface MangaRepository {

	val source: MangaSource

	val sortOrders: Set<SortOrder>

	var defaultSortOrder: SortOrder

	val filterCapabilities: MangaListFilterCapabilities

	suspend fun getList(offset: Int, order: SortOrder?, filter: MangaListFilter?): List<Manga>

	suspend fun getDetails(manga: Manga): Manga

	suspend fun getPages(chapter: MangaChapter): List<MangaPage>

	suspend fun getPageUrl(page: MangaPage): String

	suspend fun getFilterOptions(): MangaListFilterOptions

	suspend fun getRelated(seed: Manga): List<Manga>

	suspend fun find(manga: Manga): Manga? {
		val list = getList(0, SortOrder.RELEVANCE, MangaListFilter(query = manga.title))
		return list.find { x -> x.id == manga.id }
	}

	@Singleton
	class Factory @Inject constructor(
		@ApplicationContext private val context: Context,
		private val localMangaRepository: LocalMangaRepository,
		private val loaderContext: MangaLoaderContext,
		private val contentCache: MemoryContentCache,
		private val mirrorSwitcher: MirrorSwitcher,
		private val mihonExtensionManager: MihonExtensionManager,
	) {

		private val cache = ArrayMap<MangaSource, WeakReference<MangaRepository>>()

		@AnyThread
		fun create(source: MangaSource): MangaRepository {
			when (source) {
				is MangaSourceInfo -> return create(source.mangaSource)
				LocalMangaSource -> return localMangaRepository
				UnknownMangaSource -> return EmptyMangaRepository(source)
			}
			cache[source]?.get()?.let { return it }
			return synchronized(cache) {
				cache[source]?.get()?.let { return it }
				val repository = createRepository(source)
				if (repository != null) {
					cache[source] = WeakReference(repository)
					repository
				} else {
					EmptyMangaRepository(source)
				}
			}
		}

		private fun createRepository(source: MangaSource): MangaRepository? = when (source) {
			is MangaParserSource -> ParserMangaRepository(
				parser = loaderContext.newParserInstance(source),
				cache = contentCache,
				mirrorSwitcher = mirrorSwitcher,
			)

			TestMangaSource -> TestMangaRepository(
				loaderContext = loaderContext,
				cache = contentCache,
			)

			is ExternalMangaSource -> if (source.isAvailable(context)) {
				ExternalMangaRepository(
					contentResolver = context.contentResolver,
					source = source,
					cache = contentCache,
				)
			} else {
				EmptyMangaRepository(source)
			}

			is MihonMangaSource -> MihonMangaRepository(
				source = source,
				cache = contentCache,
			)

			else -> {
				if (source.name.startsWith("mihon:") || source.name.startsWith("MIHON_")) {
					mihonExtensionManager.getMihonMangaSourceByName(source.name)?.let {
						return MihonMangaRepository(
							source = it,
							cache = contentCache,
						)
					}
				}
				null
			}
		}
	}
}
