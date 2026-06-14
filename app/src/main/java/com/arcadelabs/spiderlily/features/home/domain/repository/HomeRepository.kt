package com.arcadelabs.spiderlily.features.home.domain.repository

import com.arcadelabs.spiderlily.features.home.domain.model.HomeSection
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeHomeSections(): Flow<List<HomeSection>>
}
