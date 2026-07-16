package com.arcadelabs.spiderlily.tracker.domain

import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.favourites.domain.FavouritesRepository
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.domain.MangaListQuickFilter
import javax.inject.Inject

class UpdatesListQuickFilter @Inject constructor(
	private val favouritesRepository: FavouritesRepository,
	settings: AppSettings,
) : MangaListQuickFilter(settings) {

	override suspend fun getAvailableFilterOptions(): List<ListFilterOption> =
		favouritesRepository.getMostUpdatedCategories(
			limit = 4,
		).map {
			ListFilterOption.Favorite(it)
		}
}
