package com.arcadelabs.spiderlily.scrobbling.common.ui.config.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.ui.list.AdapterDelegateClickListenerAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.databinding.ItemScrobblingMangaBinding
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblingInfo

fun scrobblingMangaAD(
	clickListener: OnListItemClickListener<ScrobblingInfo>,
) = adapterDelegateViewBinding<ScrobblingInfo, ListModel, ItemScrobblingMangaBinding>(
	{ layoutInflater, parent -> ItemScrobblingMangaBinding.inflate(layoutInflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)

	bind {
		binding.imageViewCover.setImageAsync(item.coverUrl, null)
		binding.textViewTitle.text = item.title
		binding.ratingBar.rating = item.rating * binding.ratingBar.numStars
	}
}
