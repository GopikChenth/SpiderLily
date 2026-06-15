package com.arcadelabs.spiderlily.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.ui.widgets.TipView
import com.arcadelabs.spiderlily.databinding.ItemTip2Binding
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.TipModel

fun tipAD(
	listener: TipView.OnButtonClickListener,
) = adapterDelegateViewBinding<TipModel, ListModel, ItemTip2Binding>(
	{ layoutInflater, parent -> ItemTip2Binding.inflate(layoutInflater, parent, false) }
) {

	binding.root.onButtonClickListener = listener

	bind {
		with(binding.root) {
			tag = item
			setTitle(item.title)
			setText(item.text)
			setIcon(item.icon)
			setPrimaryButtonText(item.primaryButtonText)
			setSecondaryButtonText(item.secondaryButtonText)
		}
	}
}
