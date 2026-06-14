package com.arcadelabs.spiderlily.features.favourites.domain.repository

import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteCategory
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteManga
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    fun observeCategories(): Flow<List<FavouriteCategory>>
    fun observeFavourites(): Flow<List<FavouriteManga>>
}
