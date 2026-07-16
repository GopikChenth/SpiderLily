package com.arcadelabs.spiderlily.settings.tracker.categories

import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener

class TrackerCategoriesConfigAdapter(
	listener: OnListItemClickListener<FavouriteCategory>,
) : BaseListAdapter<FavouriteCategory>() {

	init {
		delegatesManager.addDelegate(trackerCategoryAD(listener))
	}
}
