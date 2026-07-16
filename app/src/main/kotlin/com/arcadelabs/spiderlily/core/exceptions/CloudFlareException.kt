package com.arcadelabs.spiderlily.core.exceptions

import okio.IOException
import com.arcadelabs.spiderlily_parser.model.MangaSource

abstract class CloudFlareException(
	message: String,
	val state: Int,
) : IOException(message) {

	abstract val url: String

	abstract val source: MangaSource
}
