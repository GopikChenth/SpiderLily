package com.arcadelabs.spiderlily.features.library.domain.usecase

import com.arcadelabs.spiderlily.features.library.domain.repository.LibraryRepository

class GetLibraryCategoriesUseCase(
    private val repository: LibraryRepository,
) {
    operator fun invoke() = repository.observeCategories()
}

class GetFavoritesUseCase(
    private val repository: LibraryRepository,
) {
    operator fun invoke() = repository.observeLibrary()
}
