package com.arcadelabs.spiderlily.features.favourites.data

import androidx.compose.ui.graphics.Color
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteCategory
import com.arcadelabs.spiderlily.features.favourites.domain.model.FavouriteManga
import com.arcadelabs.spiderlily.features.favourites.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFavouritesRepository : FavouritesRepository {
    override fun observeCategories(): Flow<List<FavouriteCategory>> = flowOf(sampleCategories)

    override fun observeFavourites(): Flow<List<FavouriteManga>> = flowOf(sampleFavourites)

    private companion object {
        val sampleCategories = listOf(
            FavouriteCategory(id = "all", title = "All"),
            FavouriteCategory(id = "reading", title = "Reading"),
            FavouriteCategory(id = "saved", title = "Saved"),
            FavouriteCategory(id = "completed", title = "Completed"),
            FavouriteCategory(id = "offline", title = "On device"),
        )

        val sampleFavourites = listOf(
            FavouriteManga(
                id = "fragrant-flower",
                title = "The Fragrant Flower Blooms With Dignity",
                source = "MangaDex",
                latestChapter = "Chapter 119",
                unreadChapters = 2,
                progressPercent = 86,
                categoryIds = setOf("reading", "saved"),
                accentColor = Color(0xFF5EB7D9),
            ),
            FavouriteManga(
                id = "last-train",
                title = "My Bias Gets on the Last Train",
                source = "MangaDex",
                latestChapter = "Chapter 28",
                unreadChapters = 5,
                progressPercent = 72,
                categoryIds = setOf("reading", "offline"),
                accentColor = Color(0xFFE35A7A),
            ),
            FavouriteManga(
                id = "novels-extra",
                title = "The Novel's Extra (Remake)",
                source = "MangaBuddy",
                latestChapter = "Chapter 159",
                unreadChapters = 0,
                progressPercent = 100,
                categoryIds = setOf("completed", "saved", "offline"),
                accentColor = Color(0xFF735CE8),
            ),
            FavouriteManga(
                id = "ember-path",
                title = "The Ember Path",
                source = "MangaDex",
                latestChapter = "Chapter 34",
                unreadChapters = 1,
                progressPercent = 41,
                categoryIds = setOf("reading"),
                accentColor = Color(0xFFFF8A55),
            ),
            FavouriteManga(
                id = "night-market",
                title = "Night Market Alchemist",
                source = "Local",
                latestChapter = "Chapter 7",
                unreadChapters = 0,
                progressPercent = 18,
                categoryIds = setOf("saved", "offline"),
                accentColor = Color(0xFF32A86F),
            ),
        )
    }
}
