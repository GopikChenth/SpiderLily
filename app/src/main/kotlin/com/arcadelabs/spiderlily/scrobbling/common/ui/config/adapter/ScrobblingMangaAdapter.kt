package com.arcadelabs.spiderlily.scrobbling.common.ui.config.adapter

import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.emptyStateListAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblingInfo

class ScrobblingMangaAdapter(
	clickListener: OnListItemClickListener<ScrobblingInfo>,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.HEADER, scrobblingHeaderAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
		addDelegate(ListItemType.MANGA_SCROBBLING, scrobblingMangaAD(clickListener))
	}
}
