package com.arcadelabs.spiderlily.explore.ui.model

import com.arcadelabs.spiderlily.core.model.MangaSourceInfo
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily_parser.util.longHashCode

data class MangaSourceItem(
	val source: MangaSourceInfo,
	val isGrid: Boolean,
) : ListModel {

	val id: Long = source.name.longHashCode()

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is MangaSourceItem && other.source == source
	}
}
