package com.arcadelabs.spiderlily.features.home.data

import androidx.compose.ui.graphics.Color
import com.arcadelabs.spiderlily.features.home.domain.model.HomeManga
import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection
import com.arcadelabs.spiderlily.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeHomeRepository : HomeRepository {
    override fun observeHomeSections(): Flow<List<HomeSection>> = flowOf(
        listOf(
            HomeSection(
                title = "Today",
                manga = listOf(
                    HomeManga(
                        id = "last-train",
                        title = "My Bias Gets on the Last Train",
                        source = "MangaDex",
                        chapterLabel = "Chapter 28",
                        progressPercent = 72,
                        accentColor = Color(0xFFE35A7A),
                    ),
                    HomeManga(
                        id = "fragrant-flower",
                        title = "The Fragrant Flower Blooms With Dignity",
                        source = "MangaDex",
                        chapterLabel = "Chapter 11",
                        progressPercent = 100,
                        accentColor = Color(0xFF5EB7D9),
                    ),
                ),
            ),
            HomeSection(
                title = "Yesterday",
                manga = listOf(
                    HomeManga(
                        id = "novels-extra",
                        title = "The Novel's Extra (Remake)",
                        source = "MangaBuddy",
                        chapterLabel = "Chapter 159",
                        progressPercent = 100,
                        accentColor = Color(0xFF735CE8),
                    ),
                    HomeManga(
                        id = "ember-path",
                        title = "The Ember Path",
                        source = "MangaDex",
                        chapterLabel = "Chapter 34",
                        progressPercent = 41,
                        accentColor = Color(0xFFFF8A55),
                    ),
                ),
            ),
            HomeSection(
                title = "2 days ago",
                manga = listOf(
                    HomeManga(
                        id = "night-market",
                        title = "Night Market Alchemist",
                        source = "Local",
                        chapterLabel = "Chapter 7",
                        progressPercent = 18,
                        accentColor = Color(0xFF32A86F),
                    ),
                ),
            ),
        ),
    )
}
