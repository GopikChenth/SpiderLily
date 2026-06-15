package com.arcadelabs.spiderlily.history.ui

import android.content.Context
import com.arcadelabs.spiderlily.core.ui.list.fastscroll.FastScroller
import com.arcadelabs.spiderlily.list.ui.adapter.MangaListAdapter
import com.arcadelabs.spiderlily.list.ui.adapter.MangaListListener
import com.arcadelabs.spiderlily.list.ui.size.ItemSizeResolver

class HistoryListAdapter(
	listener: MangaListListener,
	sizeResolver: ItemSizeResolver,
) : MangaListAdapter(listener, sizeResolver), FastScroller.SectionIndexer {

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
