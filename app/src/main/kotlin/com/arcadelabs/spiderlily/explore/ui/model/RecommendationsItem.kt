package com.arcadelabs.spiderlily.explore.ui.model

import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaCompactListModel

data class RecommendationsItem(
	val manga: List<MangaCompactListModel>
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is RecommendationsItem
	}
}
