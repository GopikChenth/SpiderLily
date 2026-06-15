package com.arcadelabs.spiderlily.settings.sources.catalog

import android.content.Context
import com.arcadelabs.spiderlily.core.model.getTitle
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.list.fastscroll.FastScroller
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class SourcesCatalogAdapter(
	listener: OnListItemClickListener<SourceCatalogItem.Source>,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.CHAPTER_LIST, sourceCatalogItemSourceAD(listener))
		addDelegate(ListItemType.HINT_EMPTY, sourceCatalogItemHintAD())
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return (items.getOrNull(position) as? SourceCatalogItem.Source)?.source?.getTitle(context)?.take(1)
	}
}
