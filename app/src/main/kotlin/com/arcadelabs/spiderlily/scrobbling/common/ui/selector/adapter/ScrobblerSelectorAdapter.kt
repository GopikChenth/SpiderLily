package com.arcadelabs.spiderlily.scrobbling.common.ui.selector.adapter

import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.ListStateHolderListener
import com.arcadelabs.spiderlily.list.ui.adapter.loadingFooterAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerManga

class ScrobblerSelectorAdapter(
	clickListener: OnListItemClickListener<ScrobblerManga>,
	stateHolderListener: ListStateHolderListener,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.MANGA_SCROBBLING, scrobblingMangaAD(clickListener))
		addDelegate(ListItemType.FOOTER_LOADING, loadingFooterAD())
		addDelegate(ListItemType.HINT_EMPTY, scrobblerHintAD(stateHolderListener))
	}
}
