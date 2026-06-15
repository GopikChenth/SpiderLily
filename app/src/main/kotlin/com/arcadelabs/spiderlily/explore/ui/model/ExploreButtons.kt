package com.arcadelabs.spiderlily.explore.ui.model

import com.arcadelabs.spiderlily.list.ui.model.ListModel

data class ExploreButtons(
	val isRandomLoading: Boolean,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is ExploreButtons
	}
}
