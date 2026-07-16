package com.arcadelabs.spiderlily.core.parser

import com.arcadelabs.spiderlily.core.exceptions.UnsupportedSourceException
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaChapter
import com.arcadelabs.spiderlily_parser.model.MangaListFilter
import com.arcadelabs.spiderlily_parser.model.MangaListFilterCapabilities
import com.arcadelabs.spiderlily_parser.model.MangaListFilterOptions
import com.arcadelabs.spiderlily_parser.model.MangaPage
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.model.SortOrder
import java.util.EnumSet

open class EmptyMangaRepository(override val source: MangaSource) : MangaRepository {

	override val sortOrders: Set<SortOrder>
		get() = EnumSet.allOf(SortOrder::class.java)

	override var defaultSortOrder: SortOrder
		get() = SortOrder.NEWEST
		set(value) = Unit

	override val filterCapabilities: MangaListFilterCapabilities
		get() = MangaListFilterCapabilities()

	override suspend fun getList(offset: Int, order: SortOrder?, filter: MangaListFilter?): List<Manga> = stub()

	override suspend fun getDetails(manga: Manga): Manga = stub(manga)

	override suspend fun getPages(chapter: MangaChapter): List<MangaPage> = stub()

	override suspend fun getPageUrl(page: MangaPage): String = stub()

	override suspend fun getFilterOptions(): MangaListFilterOptions = stub()

	override suspend fun getRelated(seed: Manga): List<Manga> = stub(seed)

	private fun stub(manga: Manga? = null): Nothing {
		throw UnsupportedSourceException("This manga source is not supported: ${source.name}", manga, source)
	}
}
