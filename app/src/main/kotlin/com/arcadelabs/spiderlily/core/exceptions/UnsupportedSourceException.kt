package com.arcadelabs.spiderlily.core.exceptions

import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaSource

class UnsupportedSourceException(
	message: String?,
	val manga: Manga? = null,
	val source: MangaSource? = null,
) : IllegalArgumentException(message)
