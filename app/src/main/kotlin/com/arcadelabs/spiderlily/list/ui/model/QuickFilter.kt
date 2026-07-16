package com.arcadelabs.spiderlily.list.ui.model

import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback

data class QuickFilter(
	val items: List<ChipsView.ChipModel>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean = other is QuickFilter

	override fun getChangePayload(previousState: ListModel) = ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
}
