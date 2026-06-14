package com.arcadelabs.spiderlily.features.favourites.ui

import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteCategory
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteManga

data class FavouritesUiState(
    val searchQuery: String = "",
    val categories: List<FavouriteCategory> = emptyList(),
    val selectedCategoryId: String = ALL_CATEGORY_ID,
    val favourites: List<FavouriteManga> = emptyList(),
) {
    val selectedCategoryTitle: String
        get() = categories.firstOrNull { it.id == selectedCategoryId }?.title ?: "All"
}

const val ALL_CATEGORY_ID = "all"
