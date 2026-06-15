package com.arcadelabs.spiderlily.widget.shelf

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.arcadelabs.spiderlily.backups.data.model.HistoryBackup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.favourites.domain.FavouritesRepository
import com.arcadelabs.spiderlily.history.data.HistoryRepository
import com.arcadelabs.spiderlily.widget.shelf.model.CategoryItem
import javax.inject.Inject

@HiltViewModel
class ShelfConfigViewModel @Inject constructor(
    favouritesRepository: FavouritesRepository,
    private val historyRepository: HistoryRepository,
) : BaseViewModel() {

	private val selectedCategoryId = MutableStateFlow(0L)
    private val selectedSourceType = MutableStateFlow("favourites")

	val content: StateFlow<List<CategoryItem>> = combine(
		favouritesRepository.observeCategories(),
		selectedCategoryId,
        selectedSourceType,
	) { categories, selectedId, sourceType ->
		val list = ArrayList<CategoryItem>(categories.size + 2)
        list += CategoryItem(-1L, "Recent", sourceType == "recent")
        list += CategoryItem(0L, null, selectedId == 0L && sourceType == "favorites")
        categories.mapTo(list) {
            CategoryItem(it.id, it.title, selectedId == it.id && sourceType == "favorites")
        }
		list
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, emptyList())

	var checkedId: Long
		get() = selectedCategoryId.value
		set(value) {
			selectedCategoryId.value = value
            selectedSourceType.value = "favourites"
		}

    val sourceType = selectedSourceType

    fun selectRecent() {
        selectedSourceType.value = "recent"
        selectedCategoryId.value = 0L
    }
}
