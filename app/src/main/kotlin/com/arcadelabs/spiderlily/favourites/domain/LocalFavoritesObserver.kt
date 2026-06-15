package com.arcadelabs.spiderlily.favourites.domain

import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.db.entity.toManga
import com.arcadelabs.spiderlily.core.db.entity.toMangaTags
import com.arcadelabs.spiderlily.favourites.data.FavouriteManga
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.domain.ListSortOrder
import com.arcadelabs.spiderlily.local.data.index.LocalMangaIndex
import com.arcadelabs.spiderlily.local.domain.LocalObserveMapper
import org.koitharu.kotatsu.parsers.model.Manga
import javax.inject.Inject

@Reusable
class LocalFavoritesObserver @Inject constructor(
	localMangaIndex: LocalMangaIndex,
	private val db: MangaDatabase,
) : LocalObserveMapper<FavouriteManga, Manga>(localMangaIndex) {

	fun observeAll(
		order: ListSortOrder,
		filterOptions: Set<ListFilterOption>,
		limit: Int
	): Flow<List<Manga>> = db.getFavouritesDao().observeAll(order, filterOptions, limit).mapToLocal()

	fun observeAll(
		categoryId: Long,
		order: ListSortOrder,
		filterOptions: Set<ListFilterOption>,
		limit: Int
	): Flow<List<Manga>> = db.getFavouritesDao().observeAll(categoryId, order, filterOptions, limit).mapToLocal()

	override fun toManga(e: FavouriteManga) = e.manga.toManga(e.tags.toMangaTags(), null)

	override fun toResult(e: FavouriteManga, manga: Manga) = manga
}
