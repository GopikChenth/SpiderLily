package com.arcadelabs.spiderlily.list.ui.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.AdapterDelegateClickListenerAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.util.ext.setTooltipCompat
import com.arcadelabs.spiderlily.databinding.ItemMangaGridBinding
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback.Companion.PAYLOAD_PROGRESS_CHANGED
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaGridModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.list.ui.size.ItemSizeResolver

fun mangaGridItemAD(
	sizeResolver: ItemSizeResolver,
	clickListener: OnListItemClickListener<MangaListModel>,
) = adapterDelegateViewBinding<MangaGridModel, ListModel, ItemMangaGridBinding>(
	{ inflater, parent -> ItemMangaGridBinding.inflate(inflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)
	sizeResolver.attachToView(itemView, binding.textViewTitle, binding.progressView)

	bind { payloads ->
		itemView.setTooltipCompat(item.getSummary(context))
		binding.textViewTitle.text = item.title
		binding.progressView.setProgress(item.progress, PAYLOAD_PROGRESS_CHANGED in payloads)
		with(binding.iconsView) {
			clearIcons()
			if (item.isSaved) addIcon(R.drawable.ic_storage)
			if (item.isFavorite) addIcon(R.drawable.ic_heart_outline)
			isVisible = iconsCount > 0
		}
		binding.imageViewCover.setImageAsync(item.coverUrl, item.manga)
		binding.badge.number = item.counter
		binding.badge.isVisible = item.counter > 0
	}
}
