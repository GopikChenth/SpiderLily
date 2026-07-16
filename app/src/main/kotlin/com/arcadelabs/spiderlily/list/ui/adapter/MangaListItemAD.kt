package com.arcadelabs.spiderlily.list.ui.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.ui.list.AdapterDelegateClickListenerAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.util.ext.setTooltipCompat
import com.arcadelabs.spiderlily.core.util.ext.textAndVisible
import com.arcadelabs.spiderlily.databinding.ItemMangaListBinding
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaCompactListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel

fun mangaListItemAD(
	clickListener: OnListItemClickListener<MangaListModel>,
) = adapterDelegateViewBinding<MangaCompactListModel, ListModel, ItemMangaListBinding>(
	{ inflater, parent -> ItemMangaListBinding.inflate(inflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)

	bind {
		itemView.setTooltipCompat(item.getSummary(context))
		binding.textViewTitle.text = item.title
		binding.textViewSubtitle.textAndVisible = item.subtitle
		binding.imageViewCover.setImageAsync(item.coverUrl, item.manga)
		binding.badge.number = item.counter
		binding.badge.isVisible = item.counter > 0
	}
}
