package com.arcadelabs.spiderlily.features.library.data

import androidx.compose.ui.graphics.Color
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryCategory
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga
import com.arcadelabs.spiderlily.features.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLibraryRepository : LibraryRepository {
    override fun observeCategories(): Flow<List<LibraryCategory>> = flowOf(sampleCategories)

    override fun observeLibrary(): Flow<List<LibraryManga>> = flowOf(sampleFavourites)

    private companion object {
        val sampleCategories = listOf(
            LibraryCategory(id = "all", title = "All"),
            LibraryCategory(id = "reading", title = "Reading"),
            LibraryCategory(id = "saved", title = "Saved"),
            LibraryCategory(id = "completed", title = "Completed"),
            LibraryCategory(id = "offline", title = "On device"),
        )

        val sampleFavourites = listOf(
            LibraryManga(
                id = "fragrant-flower",
                title = "The Fragrant Flower Blooms With Dignity",
                source = "MangaDex",
                latestChapter = "Chapter 119",
                unreadChapters = 2,
                progressPercent = 86,
                categoryIds = setOf("reading", "saved"),
                accentColor = Color(0xFF5EB7D9),
            ),
            LibraryManga(
                id = "last-train",
                title = "My Bias Gets on the Last Train",
                source = "MangaDex",
                latestChapter = "Chapter 28",
                unreadChapters = 5,
                progressPercent = 72,
                categoryIds = setOf("reading", "offline"),
                accentColor = Color(0xFFE35A7A),
            ),
            LibraryManga(
                id = "novels-extra",
                title = "The Novel's Extra (Remake)",
                source = "MangaBuddy",
                latestChapter = "Chapter 159",
                unreadChapters = 0,
                progressPercent = 100,
                categoryIds = setOf("completed", "saved", "offline"),
                accentColor = Color(0xFF735CE8),
            ),
            LibraryManga(
                id = "ember-path",
                title = "The Ember Path",
                source = "MangaDex",
                latestChapter = "Chapter 34",
                unreadChapters = 1,
                progressPercent = 41,
                categoryIds = setOf("reading"),
                accentColor = Color(0xFFFF8A55),
            ),
            LibraryManga(
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
