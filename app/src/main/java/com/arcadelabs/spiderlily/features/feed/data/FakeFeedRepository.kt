package com.arcadelabs.spiderlily.features.feed.data

import com.arcadelabs.spiderlily.features.feed.domain.model.FeedLogItem
import com.arcadelabs.spiderlily.features.feed.domain.model.FeedUiGroup
import com.arcadelabs.spiderlily.features.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeFeedRepository : FeedRepository {
    private val _feedGroups = MutableStateFlow(
        listOf(
            FeedUiGroup(
                dateLabel = "Today",
                items = listOf(
                    FeedLogItem(
                        id = "1",
                        mangaTitle = "My Bias Gets on the Last Train",
                        source = "MangaDex",
                        description = "Added Chapter 28",
                        timestamp = System.currentTimeMillis(),
                        unread = true,
                    ),
                    FeedLogItem(
                        id = "2",
                        mangaTitle = "The Fragrant Flower Blooms With Dignity",
                        source = "MangaDex",
                        description = "Added Chapter 11",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                        unread = true,
                    ),
                ),
            ),
            FeedUiGroup(
                dateLabel = "Yesterday",
                items = listOf(
                    FeedLogItem(
                        id = "3",
                        mangaTitle = "The Novel's Extra (Remake)",
                        source = "MangaBuddy",
                        description = "Added Chapter 159",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                        unread = false,
                    ),
                    FeedLogItem(
                        id = "4",
                        mangaTitle = "The Ember Path",
                        source = "MangaDex",
                        description = "Added Chapter 34",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 26,
                        unread = true,
                    ),
                ),
            ),
            FeedUiGroup(
                dateLabel = "2 days ago",
                items = listOf(
                    FeedLogItem(
                        id = "5",
                        mangaTitle = "Night Market Alchemist",
                        source = "Local",
                        description = "Added Chapter 7",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 50,
                        unread = false,
                    ),
                ),
            ),
        ),
    )

    override fun observeFeedGroups(): Flow<List<FeedUiGroup>> = _feedGroups.asStateFlow()

    override suspend fun markAsRead(itemId: String) {
        _feedGroups.update { groups ->
            groups.map { group ->
                group.copy(
                    items = group.items.map { item ->
                        if (item.id == itemId) item.copy(unread = false) else item
                    },
                )
            }
        }
    }

    override suspend fun clearFeed() {
        _feedGroups.update { emptyList() }
    }
}
