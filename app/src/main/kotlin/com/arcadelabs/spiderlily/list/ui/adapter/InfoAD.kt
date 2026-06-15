package com.arcadelabs.spiderlily.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.util.ext.setTextAndVisible
import com.arcadelabs.spiderlily.databinding.ItemInfoBinding
import com.arcadelabs.spiderlily.list.ui.model.InfoModel
import com.arcadelabs.spiderlily.list.ui.model.ListModel

fun infoAD() = adapterDelegateViewBinding<InfoModel, ListModel, ItemInfoBinding>(
	{ layoutInflater, parent -> ItemInfoBinding.inflate(layoutInflater, parent, false) },
) {

	bind {
		binding.textViewTitle.setText(item.title)
		binding.textViewBody.setTextAndVisible(item.text)
		binding.textViewTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
			item.icon, 0, 0, 0,
		)
	}
}
