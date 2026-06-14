package com.arcadelabs.spiderlily.features.home.ui

import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection

data class HomeUiState(
    val searchQuery: String = "",
    val filters: List<String> = listOf("On device", "New chapters", "Completed", "Unread"),
    val selectedFilter: String? = "On device",
    val sections: List<HomeSection> = emptyList(),
)
