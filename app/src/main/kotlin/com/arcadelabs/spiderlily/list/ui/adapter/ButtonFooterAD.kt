package com.arcadelabs.spiderlily.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.databinding.ItemButtonFooterBinding
import com.arcadelabs.spiderlily.list.ui.model.ButtonFooter
import com.arcadelabs.spiderlily.list.ui.model.ListModel

fun buttonFooterAD(
	listener: ListStateHolderListener,
) = adapterDelegateViewBinding<ButtonFooter, ListModel, ItemButtonFooterBinding>(
	{ inflater, parent -> ItemButtonFooterBinding.inflate(inflater, parent, false) },
) {

	binding.button.setOnClickListener {
		listener.onFooterButtonClick()
	}

	bind {
		binding.button.setText(item.textResId)
	}
}
