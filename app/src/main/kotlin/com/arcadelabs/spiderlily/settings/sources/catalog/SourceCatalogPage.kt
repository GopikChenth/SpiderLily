package com.arcadelabs.spiderlily.settings.sources.catalog

import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily_parser.model.ContentType

data class SourceCatalogPage(
	val type: ContentType,
	val items: List<SourceCatalogItem>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is SourceCatalogPage && other.type == type
	}

	override fun getChangePayload(previousState: ListModel): Any {
		return ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
	}
}
