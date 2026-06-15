package com.arcadelabs.spiderlily.tracker.ui.feed.model

import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel

data class UpdatedMangaHeader(
	val list: List<MangaListModel>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is UpdatedMangaHeader
	}

	override fun getChangePayload(previousState: ListModel): Any {
		return ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
	}
}
