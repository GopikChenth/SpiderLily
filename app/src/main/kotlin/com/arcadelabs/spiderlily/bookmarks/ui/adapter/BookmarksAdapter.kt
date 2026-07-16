package com.arcadelabs.spiderlily.bookmarks.ui.adapter

import android.content.Context
import com.arcadelabs.spiderlily.bookmarks.domain.Bookmark
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.list.fastscroll.FastScroller
import com.arcadelabs.spiderlily.list.ui.adapter.ListHeaderClickListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.emptyStateListAD
import com.arcadelabs.spiderlily.list.ui.adapter.errorStateListAD
import com.arcadelabs.spiderlily.list.ui.adapter.listHeaderAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingFooterAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class BookmarksAdapter(
	clickListener: OnListItemClickListener<Bookmark>,
	headerClickListener: ListHeaderClickListener?,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.PAGE_THUMB, bookmarkLargeAD(clickListener))
		addDelegate(ListItemType.HEADER, listHeaderAD(headerClickListener))
		addDelegate(ListItemType.STATE_ERROR, errorStateListAD(null))
		addDelegate(ListItemType.FOOTER_LOADING, loadingFooterAD())
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
