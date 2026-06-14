package com.arcadelabs.spiderlily.features.library.ui

import com.arcadelabs.spiderlily.features.library.domain.model.LibraryCategory
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga

data class LibraryUiState(
    val searchQuery: String = "",
    val categories: List<LibraryCategory> = emptyList(),
    val selectedCategoryId: String = ALL_CATEGORY_ID,
    val libraryManga: List<LibraryManga> = emptyList(),
) {
    val selectedCategoryTitle: String
        get() = categories.firstOrNull { it.id == selectedCategoryId }?.title ?: "All"
}

const val ALL_CATEGORY_ID = "all"
