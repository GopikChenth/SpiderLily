package com.arcadelabs.spiderlily.favourites.data

import com.arcadelabs.spiderlily.core.db.entity.toManga
import com.arcadelabs.spiderlily.core.db.entity.toMangaTags
import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.list.domain.ListSortOrder
import java.time.Instant

fun FavouriteCategoryEntity.toFavouriteCategory(id: Long = categoryId.toLong()) = FavouriteCategory(
	id = id,
	title = title,
	sortKey = sortKey,
	order = ListSortOrder(order, ListSortOrder.NEWEST),
	createdAt = Instant.ofEpochMilli(createdAt),
	isTrackingEnabled = track,
	isVisibleInLibrary = isVisibleInLibrary,
)

fun FavouriteManga.toManga() = manga.toManga(tags.toMangaTags(), null)

fun Collection<FavouriteManga>.toMangaList() = map { it.toManga() }
