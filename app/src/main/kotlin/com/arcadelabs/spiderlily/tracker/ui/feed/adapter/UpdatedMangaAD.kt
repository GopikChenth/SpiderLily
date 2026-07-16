package com.arcadelabs.spiderlily.tracker.ui.feed.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.databinding.ItemListGroupBinding
import com.arcadelabs.spiderlily.list.ui.adapter.ListHeaderClickListener
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.mangaGridItemAD
import com.arcadelabs.spiderlily.list.ui.model.ListHeader
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.list.ui.size.ItemSizeResolver
import com.arcadelabs.spiderlily.tracker.ui.feed.model.UpdatedMangaHeader

fun updatedMangaAD(
	sizeResolver: ItemSizeResolver,
	listener: OnListItemClickListener<MangaListModel>,
	headerClickListener: ListHeaderClickListener,
) = adapterDelegateViewBinding<UpdatedMangaHeader, ListModel, ItemListGroupBinding>(
	{ layoutInflater, parent -> ItemListGroupBinding.inflate(layoutInflater, parent, false) },
) {

	val adapter = BaseListAdapter<ListModel>()
		.addDelegate(ListItemType.MANGA_GRID, mangaGridItemAD(sizeResolver, listener))
	binding.recyclerView.adapter = adapter
	binding.buttonMore.setOnClickListener { v ->
		headerClickListener.onListHeaderClick(ListHeader(0, payload = item), v)
	}
	binding.textViewTitle.setText(R.string.updates)
	binding.buttonMore.setText(R.string.more)

	bind {
		adapter.items = item.list
	}
}
