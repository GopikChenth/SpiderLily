package com.arcadelabs.spiderlily.picker.ui.manga

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.favourites.domain.FavouritesRepository
import com.arcadelabs.spiderlily.history.data.HistoryRepository
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.ui.MangaListViewModel
import com.arcadelabs.spiderlily.list.ui.model.ListHeader
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import com.arcadelabs.spiderlily.local.data.LocalStorageChanges
import com.arcadelabs.spiderlily.local.domain.model.LocalManga

@HiltViewModel
class MangaPickerViewModel @Inject constructor(
	private val settings: AppSettings,
	mangaDataRepository: MangaDataRepository,
	private val historyRepository: HistoryRepository,
	private val favouritesRepository: FavouritesRepository,
	private val mangaListMapper: MangaListMapper,
	@LocalStorageChanges localStorageChanges: SharedFlow<LocalManga?>,
) : MangaListViewModel(settings, mangaDataRepository, localStorageChanges) {

	override val content: StateFlow<List<ListModel>>
		get() = flow {
			emit(loadList())
		}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, listOf(LoadingState))

	override fun onRefresh() = Unit

	override fun onRetry() = Unit

	private suspend fun loadList() = buildList {
		val history = historyRepository.getList(0, Int.MAX_VALUE)
		if (history.isNotEmpty()) {
			add(ListHeader(R.string.history))
			mangaListMapper.toListModelList(this, history, settings.listMode)
		}
		val categories = favouritesRepository.observeCategoriesForLibrary().first()
		for (category in categories) {
			val favorites = favouritesRepository.getManga(category.id)
			if (favorites.isNotEmpty()) {
				add(ListHeader(category.title))
				mangaListMapper.toListModelList(this, favorites, settings.listMode)
			}
		}
	}
}
