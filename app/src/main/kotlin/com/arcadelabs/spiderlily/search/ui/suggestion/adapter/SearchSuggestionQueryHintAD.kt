package com.arcadelabs.spiderlily.search.ui.suggestion.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.databinding.ItemSearchSuggestionQueryHintBinding
import com.arcadelabs.spiderlily.search.domain.SearchKind
import com.arcadelabs.spiderlily.search.ui.suggestion.SearchSuggestionListener
import com.arcadelabs.spiderlily.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionQueryHintAD(
	listener: SearchSuggestionListener,
) = adapterDelegateViewBinding<SearchSuggestionItem.Hint, SearchSuggestionItem, ItemSearchSuggestionQueryHintBinding>(
	{ inflater, parent -> ItemSearchSuggestionQueryHintBinding.inflate(inflater, parent, false) },
) {

	val viewClickListener = View.OnClickListener { _ ->
		listener.onQueryClick(item.query, SearchKind.SIMPLE, true)
	}

	binding.root.setOnClickListener(viewClickListener)

	bind {
		binding.root.text = item.query
	}
}
