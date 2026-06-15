package com.arcadelabs.spiderlily.scrobbling.common.ui.selector.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.util.ext.textAndVisible
import com.arcadelabs.spiderlily.databinding.ItemMangaListBinding
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerManga

fun scrobblingMangaAD(
	clickListener: OnListItemClickListener<ScrobblerManga>,
) = adapterDelegateViewBinding<ScrobblerManga, ListModel, ItemMangaListBinding>(
	{ inflater, parent -> ItemMangaListBinding.inflate(inflater, parent, false) },
) {
	itemView.setOnClickListener {
		clickListener.onItemClick(item, it)
	}

	bind {
		binding.textViewTitle.text = item.name
		val endIcon = if (item.isBestMatch) R.drawable.ic_star_small else 0
		binding.textViewTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, endIcon, 0)
		binding.textViewSubtitle.textAndVisible = item.altName
		binding.imageViewCover.setImageAsync(item.cover, null)
	}
}
