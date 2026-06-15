package com.arcadelabs.spiderlily.details.ui.pager.pages

import android.content.Context
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.list.fastscroll.FastScroller
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.listHeaderAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class PageThumbnailAdapter(
	clickListener: OnListItemClickListener<PageThumbnail>,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.PAGE_THUMB, pageThumbnailAD(clickListener))
		addDelegate(ListItemType.HEADER, listHeaderAD(null))
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
