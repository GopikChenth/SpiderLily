package com.arcadelabs.spiderlily.features.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcadelabs.spiderlily.features.feed.data.FakeFeedRepository
import com.arcadelabs.spiderlily.features.feed.domain.model.FeedUiGroup
import com.arcadelabs.spiderlily.features.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val searchQuery: String = "",
    val selectedFilter: String = "All",
    val filters: List<String> = listOf("All", "Unread"),
    val groups: List<FeedUiGroup> = emptyList(),
)

class FeedViewModel(
    private val repository: FeedRepository = FakeFeedRepository(),
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FeedUiState())

    val uiState: StateFlow<FeedUiState> = combine(
        viewModelState,
        repository.observeFeedGroups(),
    ) { state, groups ->
        val filteredGroups = groups.map { group ->
            group.copy(
                items = group.items.filter { item ->
                    val matchesQuery = item.mangaTitle.contains(state.searchQuery, ignoreCase = true) ||
                        item.source.contains(state.searchQuery, ignoreCase = true)
                    val matchesFilter = when (state.selectedFilter) {
                        "Unread" -> item.unread
                        else -> true
                    }
                    matchesQuery && matchesFilter
                },
            )
        }.filter { it.items.isNotEmpty() }

        state.copy(groups = filteredGroups)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        viewModelState.update { it.copy(searchQuery = query) }
    }

    fun onFilterSelected(filter: String) {
        viewModelState.update { it.copy(selectedFilter = filter) }
    }

    fun markAsRead(itemId: String) {
        viewModelScope.launch {
            repository.markAsRead(itemId)
        }
    }

    fun clearFeed() {
        viewModelScope.launch {
            repository.clearFeed()
        }
    }
}
