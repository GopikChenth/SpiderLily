package com.arcadelabs.spiderlily.favourites.ui.categories.select.model

import com.google.android.material.checkbox.MaterialCheckBox.CheckedState
import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import com.arcadelabs.spiderlily.list.ui.model.ListModel

data class MangaCategoryItem(
	val category: FavouriteCategory,
	@CheckedState val checkedState: Int,
	val isTrackerEnabled: Boolean,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is MangaCategoryItem && other.category.id == category.id
	}

	override fun getChangePayload(previousState: ListModel): Any? {
		return if (previousState is MangaCategoryItem && previousState.checkedState != checkedState) {
			ListModelDiffCallback.PAYLOAD_CHECKED_CHANGED
		} else {
			super.getChangePayload(previousState)
		}
	}
}
