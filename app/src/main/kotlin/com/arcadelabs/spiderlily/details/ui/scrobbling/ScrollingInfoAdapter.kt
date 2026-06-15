package com.arcadelabs.spiderlily.details.ui.scrobbling

import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class ScrollingInfoAdapter(
	router: AppRouter,
) : BaseListAdapter<ListModel>() {

	init {
		delegatesManager.addDelegate(scrobblingInfoAD(router))
	}
}
