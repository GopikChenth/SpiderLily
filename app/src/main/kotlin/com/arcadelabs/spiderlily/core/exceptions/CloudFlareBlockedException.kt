package com.arcadelabs.spiderlily.core.exceptions

import com.arcadelabs.spiderlily.core.model.UnknownMangaSource
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.network.CloudFlareHelper

class CloudFlareBlockedException(
	override val url: String,
	source: MangaSource?,
) : CloudFlareException("Blocked by CloudFlare", CloudFlareHelper.PROTECTION_BLOCKED) {

	override val source: MangaSource = source ?: UnknownMangaSource
}
