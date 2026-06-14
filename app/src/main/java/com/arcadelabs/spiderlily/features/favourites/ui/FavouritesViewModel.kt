package com.arcadelabs.spiderlily.features.favourites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.features.favourites.data.FakeFavouritesRepository
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteManga
import com.arcadelabs.spiderlily.features.favourites.domain.repository.FavouritesRepository
import com.arcadelabs.spiderlily.features.favourites.domain.usecase.GetFavouriteCategoriesUseCase
import com.arcadelabs.spiderlily.features.favourites.domain.usecase.GetFavouriteMangaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FavouritesViewModel(
    repository: FavouritesRepository = FakeFavouritesRepository(),
    getCategories: GetFavouriteCategoriesUseCase = GetFavouriteCategoriesUseCase(repository),
    getFavourites: GetFavouriteMangaUseCase = GetFavouriteMangaUseCase(repository),
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FavouritesUiState())

    val uiState: StateFlow<FavouritesUiState> = combine(
        viewModelState,
        getCategories(),
        getFavourites(),
    ) { state, categories, favourites ->
        state.copy(
            categories = categories,
            favourites = favourites.visibleFor(state.selectedCategoryId),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavouritesUiState(),
    )

    fun onCategorySelected(categoryId: String) {
        viewModelState.update { state ->
            state.copy(selectedCategoryId = categoryId)
        }
    }

    private fun List<FavouriteManga>.visibleFor(categoryId: String): List<FavouriteManga> {
        if (categoryId == ALL_CATEGORY_ID) {
            return this
        }
        return filter { categoryId in it.categoryIds }
    }
}
