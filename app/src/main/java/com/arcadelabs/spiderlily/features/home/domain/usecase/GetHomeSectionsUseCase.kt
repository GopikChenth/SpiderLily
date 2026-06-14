package com.arcadelabs.spiderlily.features.home.domain.usecase

import com.arcadelabs.spiderlily.features.home.domain.repository.HomeRepository

class GetHomeSectionsUseCase(
    private val repository: HomeRepository,
) {
    operator fun invoke() = repository.observeHomeSections()
}
