package com.arcadelabs.spiderlily.favourites.ui.categories.select

import androidx.collection.MutableLongObjectMap
import androidx.collection.MutableLongSet
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.core.model.ids
import com.arcadelabs.spiderlily.core.model.parcelable.ParcelableManga
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.observeAsFlow
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.util.ext.require
import com.arcadelabs.spiderlily.favourites.domain.FavouritesRepository
import com.arcadelabs.spiderlily.favourites.ui.categories.select.model.MangaCategoryItem
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import javax.inject.Inject

@HiltViewModel
class FavoriteDialogViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val favouritesRepository: FavouritesRepository,
	settings: AppSettings,
) : BaseViewModel() {

	val manga = savedStateHandle.require<List<ParcelableManga>>(AppRouter.KEY_MANGA_LIST).map {
		it.manga
	}

	private val refreshTrigger = MutableStateFlow(Any())
	val content = combine(
		favouritesRepository.observeCategories(),
		refreshTrigger,
		settings.observeAsFlow(AppSettings.KEY_TRACKER_ENABLED) { isTrackerEnabled },
	) { categories, _, tracker ->
		mapList(categories, tracker)
	}.withErrorHandling()
		.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	fun setChecked(categoryId: Long, isChecked: Boolean) {
		launchJob(Dispatchers.IO) {
			if (isChecked) {
				favouritesRepository.addToCategory(categoryId, manga)
			} else {
				favouritesRepository.removeFromCategory(categoryId, manga.ids())
			}
			refreshTrigger.value = Any()
		}
	}

	private suspend fun mapList(categories: List<FavouriteCategory>, tracker: Boolean): List<ListModel> {
		if (categories.isEmpty()) {
			return listOf(
				EmptyState(
					icon = 0,
					textPrimary = R.string.empty_favourite_categories,
					textSecondary = 0,
					actionStringRes = 0,
				),
			)
		}
		val cats = MutableLongObjectMap<MutableLongSet>(categories.size)
		categories.forEach { cats[it.id] = MutableLongSet(manga.size) }
		for (m in manga) {
			val ids = favouritesRepository.getCategoriesIds(m.id)
			ids.forEach { id -> cats[id]?.add(m.id) }
		}
		return categories.map { cat ->
			MangaCategoryItem(
				category = cat,
				checkedState = when (cats[cat.id]?.size ?: 0) {
					0 -> MaterialCheckBox.STATE_UNCHECKED
					manga.size -> MaterialCheckBox.STATE_CHECKED
					else -> MaterialCheckBox.STATE_INDETERMINATE
				},
				isTrackerEnabled = tracker,
			)
		}
	}
}
