package com.arcadelabs.spiderlily.core.exceptions

import okio.IOException
import com.arcadelabs.spiderlily_parser.model.MangaSource

class InteractiveActionRequiredException(
	val source: MangaSource,
	val url: String,
) : IOException("Interactive action is required for ${source.name}")
