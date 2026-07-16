package com.arcadelabs.spiderlily.mihon.model

import com.arcadelabs.spiderlily.mihon.parsers.model.ContentSource

data class ContentSourceInfo(
    val mangaSource: ContentSource,
    val isEnabled: Boolean,
    val isPinned: Boolean,
) : ContentSource by mangaSource
