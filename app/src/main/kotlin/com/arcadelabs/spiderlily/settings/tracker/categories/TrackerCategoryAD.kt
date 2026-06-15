package com.arcadelabs.spiderlily.settings.tracker.categories

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.core.ui.list.AdapterDelegateClickListenerAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.databinding.ItemCategoryCheckableMultipleBinding

fun trackerCategoryAD(
	listener: OnListItemClickListener<FavouriteCategory>,
) = adapterDelegateViewBinding<FavouriteCategory, FavouriteCategory, ItemCategoryCheckableMultipleBinding>(
	{ layoutInflater, parent -> ItemCategoryCheckableMultipleBinding.inflate(layoutInflater, parent, false) },
) {
	val eventListener = AdapterDelegateClickListenerAdapter(this, listener)
	itemView.setOnClickListener(eventListener)

	bind {
		binding.root.text = item.title
		binding.root.isChecked = item.isTrackingEnabled
	}
}
