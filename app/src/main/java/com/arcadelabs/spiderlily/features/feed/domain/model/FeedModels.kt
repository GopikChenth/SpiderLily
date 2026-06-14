package com.arcadelabs.spiderlily.features.feed.domain.model

data class FeedLogItem(
    val id: String,
    val mangaTitle: String,
    val source: String,
    val coverUrl: String? = null,
    val description: String, // e.g., "Added Chapter 15"
    val timestamp: Long,
    val unread: Boolean,
)

data class FeedUiGroup(
    val dateLabel: String,
    val items: List<FeedLogItem>,
)
