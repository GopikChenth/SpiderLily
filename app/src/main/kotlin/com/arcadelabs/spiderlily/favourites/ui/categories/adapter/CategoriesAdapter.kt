package com.arcadelabs.spiderlily.favourites.ui.categories.adapter

import com.arcadelabs.spiderlily.core.ui.ReorderableListAdapter
import com.arcadelabs.spiderlily.favourites.ui.categories.FavouriteCategoriesListListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.ListStateHolderListener
import com.arcadelabs.spiderlily.list.ui.adapter.emptyStateListAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class CategoriesAdapter(
	onItemClickListener: FavouriteCategoriesListListener,
	listListener: ListStateHolderListener,
) : ReorderableListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.CATEGORY_LARGE, categoryAD(onItemClickListener))
		addDelegate(ListItemType.NAV_ITEM, allCategoriesAD(onItemClickListener))
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(listListener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
	}
}
