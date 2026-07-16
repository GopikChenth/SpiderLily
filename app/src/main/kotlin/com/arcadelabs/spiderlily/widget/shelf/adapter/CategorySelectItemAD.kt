package com.arcadelabs.spiderlily.widget.shelf.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.databinding.ItemCategoryCheckableSingleBinding
import com.arcadelabs.spiderlily.widget.shelf.model.CategoryItem

fun categorySelectItemAD(
	clickListener: OnListItemClickListener<CategoryItem>
) = adapterDelegateViewBinding<CategoryItem, CategoryItem, ItemCategoryCheckableSingleBinding>(
	{ inflater, parent -> ItemCategoryCheckableSingleBinding.inflate(inflater, parent, false) },
) {

	itemView.setOnClickListener {
		clickListener.onItemClick(item, it)
	}

	bind {
		with(binding.checkedTextView) {
			text = item.name ?: getString(R.string.all_favourites)
			isChecked = item.isSelected
		}
	}
}
