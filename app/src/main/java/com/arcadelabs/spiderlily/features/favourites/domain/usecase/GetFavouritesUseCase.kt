package com.arcadelabs.spiderlily.features.favourites.domain.usecase

import com.arcadelabs.spiderlily.features.favourites.domain.repository.FavouritesRepository

class GetFavouriteCategoriesUseCase(
    private val repository: FavouritesRepository,
) {
    operator fun invoke() = repository.observeCategories()
}

class GetFavouriteMangaUseCase(
    private val repository: FavouritesRepository,
) {
    operator fun invoke() = repository.observeFavourites()
}
