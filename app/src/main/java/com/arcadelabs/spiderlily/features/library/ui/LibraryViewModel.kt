package com.arcadelabs.spiderlily.features.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.features.library.data.FakeLibraryRepository
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga
import com.arcadelabs.spiderlily.features.library.domain.repository.LibraryRepository
import com.arcadelabs.spiderlily.features.library.domain.usecase.GetLibraryCategoriesUseCase
import com.arcadelabs.spiderlily.features.library.domain.usecase.GetFavoritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LibraryViewModel(
    repository: LibraryRepository = FakeLibraryRepository(),
    getCategories: GetLibraryCategoriesUseCase = GetLibraryCategoriesUseCase(repository),
    getLibrary: GetFavoritesUseCase = GetFavoritesUseCase(repository),
) : ViewModel() {

    private val viewModelState = MutableStateFlow(LibraryUiState())

    val uiState: StateFlow<LibraryUiState> = combine(
        viewModelState,
        getCategories(),
        getLibrary(),
    ) { state, categories, libraryManga ->
        val filteredManga = libraryManga
            .visibleFor(state.selectedCategoryId)
            .filter {
                state.searchQuery.isBlank() ||
                        it.title.contains(state.searchQuery, ignoreCase = true) ||
                        it.source.contains(state.searchQuery, ignoreCase = true)
            }
        state.copy(
            categories = categories,
            libraryManga = filteredManga,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState(),
    )

    fun onCategorySelected(categoryId: String) {
        viewModelState.update { state ->
            state.copy(selectedCategoryId = categoryId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelState.update { state ->
            state.copy(searchQuery = query)
        }
    }

    private fun List<LibraryManga>.visibleFor(categoryId: String): List<LibraryManga> {
        if (categoryId == ALL_CATEGORY_ID) {
            return this
        }
        return filter { categoryId in it.categoryIds }
    }
}
