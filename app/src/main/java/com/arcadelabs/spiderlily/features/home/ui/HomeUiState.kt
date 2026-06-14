package com.arcadelabs.spiderlily.features.home.ui

import com.arcadelabs.spiderlily.features.home.domain.model.HomeManga
import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection

data class HomeUiState(
    // Search
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false,
    val isSearching: Boolean = false,
    val searchResults: List<SearchResultSection> = emptyList(),

    // Home content
    val filters: List<String> = listOf("All", "New chapters", "Completed", "Unread"),
    val selectedFilter: String? = "All",
    val sections: List<HomeSection> = emptyList(),
    val isLoading: Boolean = true,
)

data class SearchResultSection(
    val title: String,
    val sourceId: Long = 0,
    val isLoading: Boolean = false,
    val manga: List<HomeManga> = emptyList(),
)
