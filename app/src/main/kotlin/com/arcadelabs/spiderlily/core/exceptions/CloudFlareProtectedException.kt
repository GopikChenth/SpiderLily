package com.arcadelabs.spiderlily.core.exceptions

import okhttp3.Headers
import com.arcadelabs.spiderlily.core.model.UnknownMangaSource
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.network.CloudFlareHelper

class CloudFlareProtectedException(
	override val url: String,
	source: MangaSource?,
	@Transient val headers: Headers,
) : CloudFlareException("Protected by CloudFlare", CloudFlareHelper.PROTECTION_CAPTCHA) {

	override val source: MangaSource = source ?: UnknownMangaSource
}
