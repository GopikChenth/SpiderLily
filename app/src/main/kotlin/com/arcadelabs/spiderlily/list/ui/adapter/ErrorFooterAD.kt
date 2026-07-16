package com.arcadelabs.spiderlily.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.databinding.ItemErrorFooterBinding
import com.arcadelabs.spiderlily.list.ui.model.ErrorFooter
import com.arcadelabs.spiderlily.list.ui.model.ListModel

fun errorFooterAD(
	listener: ListStateHolderListener?,
) = adapterDelegateViewBinding<ErrorFooter, ListModel, ItemErrorFooterBinding>(
	{ inflater, parent -> ItemErrorFooterBinding.inflate(inflater, parent, false) },
) {

	if (listener != null) {
		binding.root.setOnClickListener {
			listener.onRetryClick(item.exception)
		}
	}

	bind {
		binding.textViewTitle.text = item.exception.getDisplayMessage(context.resources)
	}
}
