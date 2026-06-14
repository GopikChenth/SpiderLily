package com.arcadelabs.spiderlily.features.feed.domain.repository

import com.arcadelabs.spiderlily.features.feed.domain.model.FeedUiGroup
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun observeFeedGroups(): Flow<List<FeedUiGroup>>
    suspend fun markAsRead(itemId: String)
    suspend fun clearFeed()
}
