package com.arcadelabs.spiderlily.search.ui.suggestion.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.model.getSummary
import com.arcadelabs.spiderlily.core.model.getTitle
import com.arcadelabs.spiderlily.databinding.ItemSearchSuggestionSourceBinding
import com.arcadelabs.spiderlily.search.ui.suggestion.SearchSuggestionListener
import com.arcadelabs.spiderlily.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionSourceAD(
	listener: SearchSuggestionListener,
) = adapterDelegateViewBinding<SearchSuggestionItem.Source, SearchSuggestionItem, ItemSearchSuggestionSourceBinding>(
	{ inflater, parent -> ItemSearchSuggestionSourceBinding.inflate(inflater, parent, false) },
) {

	binding.switchLocal.setOnCheckedChangeListener { _, isChecked ->
		listener.onSourceToggle(item.source, isChecked)
	}
	binding.root.setOnClickListener {
		listener.onSourceClick(item.source)
	}

	bind {
		binding.textViewTitle.text = item.source.getTitle(context)
		binding.textViewSubtitle.text = item.source.getSummary(context)
		binding.switchLocal.isChecked = item.isEnabled
		binding.imageViewCover.setImageAsync(item.source)
	}
}
