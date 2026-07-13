package com.arcadelabs.spiderlily.search.ui.suggestion.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.databinding.ItemSearchSuggestionTagsBinding
import com.arcadelabs.spiderlily_parser.model.MangaTag
import com.arcadelabs.spiderlily.search.ui.suggestion.SearchSuggestionListener
import com.arcadelabs.spiderlily.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionTagsAD(
	listener: SearchSuggestionListener,
) = adapterDelegateViewBinding<SearchSuggestionItem.Tags, SearchSuggestionItem, ItemSearchSuggestionTagsBinding>(
	{ layoutInflater, parent -> ItemSearchSuggestionTagsBinding.inflate(layoutInflater, parent, false) },
) {

	binding.chipsGenres.onChipClickListener = ChipsView.OnChipClickListener { _, data ->
		listener.onTagClick(data as? MangaTag ?: return@OnChipClickListener)
	}

	bind {
		binding.chipsGenres.setChips(item.tags)
	}
}
