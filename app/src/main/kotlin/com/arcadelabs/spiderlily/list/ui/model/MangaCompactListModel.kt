package com.arcadelabs.spiderlily.list.ui.model

import com.arcadelabs.spiderlily.core.ui.model.MangaOverride
import com.arcadelabs.spiderlily_parser.model.Manga

data class MangaCompactListModel(
	override val manga: Manga,
	override val override: MangaOverride?,
	val subtitle: String,
	override val counter: Int,
) : MangaListModel()
