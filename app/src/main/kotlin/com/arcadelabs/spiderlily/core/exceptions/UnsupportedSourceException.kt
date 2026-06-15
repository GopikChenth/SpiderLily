package com.arcadelabs.spiderlily.core.exceptions

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource

class UnsupportedSourceException(
	message: String?,
	val manga: Manga? = null,
	val source: MangaSource? = null,
) : IllegalArgumentException(message)
