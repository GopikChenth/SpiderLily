package com.arcadelabs.spiderlily.details.ui.pager.pages

import coil3.key.Keyer
import coil3.request.Options
import com.arcadelabs.spiderlily_parser.model.MangaPage

class MangaPageKeyer : Keyer<MangaPage> {

	override fun key(data: MangaPage, options: Options) = data.url
}
