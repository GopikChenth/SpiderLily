package com.arcadelabs.spiderlily.details.ui.pager.pages

import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.reader.ui.pager.ReaderPage

data class PageThumbnail(
	val isCurrent: Boolean,
	val page: ReaderPage,
) : ListModel {

	val number
		get() = page.index + 1

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is PageThumbnail && page == other.page
	}
}
