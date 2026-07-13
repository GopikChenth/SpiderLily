package com.arcadelabs.spiderlily.history.data

import dagger.Reusable
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.db.entity.toManga
import com.arcadelabs.spiderlily.core.db.entity.toMangaTags
import com.arcadelabs.spiderlily.history.domain.model.MangaWithHistory
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.domain.ListSortOrder
import com.arcadelabs.spiderlily.local.data.index.LocalMangaIndex
import com.arcadelabs.spiderlily.local.domain.LocalObserveMapper
import com.arcadelabs.spiderlily_parser.model.Manga
import javax.inject.Inject

@Reusable
class HistoryLocalObserver @Inject constructor(
	localMangaIndex: LocalMangaIndex,
	private val db: MangaDatabase,
) : LocalObserveMapper<HistoryWithManga, MangaWithHistory>(localMangaIndex) {

	fun observeAll(
		order: ListSortOrder,
		filterOptions: Set<ListFilterOption>,
		limit: Int
	) = db.getHistoryDao().observeAll(order, filterOptions, limit).mapToLocal()

	override fun toManga(e: HistoryWithManga) = e.manga.toManga(e.tags.toMangaTags(), null)

	override fun toResult(e: HistoryWithManga, manga: Manga) = MangaWithHistory(
		manga = manga,
		history = e.history.toMangaHistory(),
	)
}
