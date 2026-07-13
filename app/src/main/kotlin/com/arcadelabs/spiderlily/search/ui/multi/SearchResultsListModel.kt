package com.arcadelabs.spiderlily.search.ui.multi

import android.content.Context
import androidx.annotation.StringRes
import com.arcadelabs.spiderlily.core.model.getTitle
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily_parser.model.MangaListFilter
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.model.SortOrder

data class SearchResultsListModel(
	@StringRes val titleResId: Int,
	val source: MangaSource,
	val listFilter: MangaListFilter?,
	val sortOrder: SortOrder?,
	val list: List<MangaListModel>,
	val error: Throwable?,
) : ListModel {

	fun getTitle(context: Context): String = if (titleResId != 0) {
		context.getString(titleResId)
	} else {
		source.getTitle(context)
	}

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is SearchResultsListModel && source == other.source && titleResId == other.titleResId
	}

	override fun getChangePayload(previousState: ListModel): Any? {
		return if (previousState is SearchResultsListModel && previousState.list != list) {
			ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
		} else {
			super.getChangePayload(previousState)
		}
	}
}
