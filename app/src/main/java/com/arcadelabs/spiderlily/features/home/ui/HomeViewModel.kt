package com.arcadelabs.spiderlily.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.features.home.data.FakeHomeRepository
import com.arcadelabs.spiderlily.features.home.domain.repository.HomeRepository
import com.arcadelabs.spiderlily.features.home.domain.usecase.GetHomeSectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(
    repository: HomeRepository = FakeHomeRepository(),
    getHomeSections: GetHomeSectionsUseCase = GetHomeSectionsUseCase(repository),
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        getHomeSections(),
    ) { state, sections ->
        state.copy(sections = sections)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun onFilterSelected(filter: String) {
        viewModelState.update { state ->
            state.copy(selectedFilter = filter)
        }
    }
}
