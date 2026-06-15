package com.arcadelabs.spiderlily.search.ui.multi.adapter

import android.annotation.SuppressLint
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.UnknownMangaSource
import com.arcadelabs.spiderlily.core.ui.list.AdapterDelegateClickListenerAdapter
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.list.decor.SpacingItemDecoration
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.core.util.ext.textAndVisible
import com.arcadelabs.spiderlily.databinding.ItemListGroupBinding
import com.arcadelabs.spiderlily.list.ui.MangaSelectionDecoration
import com.arcadelabs.spiderlily.list.ui.adapter.mangaGridItemAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.list.ui.size.ItemSizeResolver
import com.arcadelabs.spiderlily.search.ui.multi.SearchResultsListModel

@SuppressLint("NotifyDataSetChanged")
fun searchResultsAD(
	sharedPool: RecycledViewPool,
	sizeResolver: ItemSizeResolver,
	selectionDecoration: MangaSelectionDecoration,
	listener: OnListItemClickListener<MangaListModel>,
	itemClickListener: OnListItemClickListener<SearchResultsListModel>,
) = adapterDelegateViewBinding<SearchResultsListModel, ListModel, ItemListGroupBinding>(
	{ layoutInflater, parent -> ItemListGroupBinding.inflate(layoutInflater, parent, false) },
) {

	binding.recyclerView.setRecycledViewPool(sharedPool)
	val adapter = ListDelegationAdapter(mangaGridItemAD(sizeResolver, listener))
	binding.recyclerView.addItemDecoration(selectionDecoration)
	binding.recyclerView.adapter = adapter
	val spacing = context.resources.getDimensionPixelOffset(R.dimen.grid_spacing_outer)
	binding.recyclerView.addItemDecoration(SpacingItemDecoration(spacing, withBottomPadding = true))
	val eventListener = AdapterDelegateClickListenerAdapter(this, itemClickListener)
	binding.buttonMore.setOnClickListener(eventListener)

	bind {
		binding.textViewTitle.text = item.getTitle(context)
		binding.buttonMore.isVisible = item.source !== UnknownMangaSource
		adapter.items = item.list
		adapter.notifyDataSetChanged()
		binding.recyclerView.isGone = item.list.isEmpty()
		binding.textViewError.textAndVisible = item.error?.getDisplayMessage(context.resources)
	}
}
