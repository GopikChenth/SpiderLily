package com.arcadelabs.spiderlily.favourites.domain.model

import com.arcadelabs.spiderlily.core.model.MangaSource

data class Cover(
	val url: String?,
	val source: String,
) {
	val mangaSource by lazy { MangaSource(source) }
}
