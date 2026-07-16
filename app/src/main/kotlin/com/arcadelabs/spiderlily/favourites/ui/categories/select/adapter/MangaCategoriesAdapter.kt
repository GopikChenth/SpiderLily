package com.arcadelabs.spiderlily.favourites.ui.categories.select.adapter

import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.favourites.ui.categories.select.model.MangaCategoryItem
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.emptyStateListAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class MangaCategoriesAdapter(
	clickListener: OnListItemClickListener<MangaCategoryItem>,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.NAV_ITEM, mangaCategoryAD(clickListener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
	}
}
