package com.arcadelabs.spiderlily.features.library.domain.repository

import com.arcadelabs.spiderlily.features.library.domain.model.LibraryCategory
import com.arcadelabs.spiderlily.features.library.domain.model.LibraryManga
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun observeCategories(): Flow<List<LibraryCategory>>
    fun observeLibrary(): Flow<List<LibraryManga>>
}
