package com.arcadelabs.spiderlily.settings.tracker.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.FavouriteCategory
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.ui.sheet.BaseAdaptiveSheet
import com.arcadelabs.spiderlily.core.util.ext.consume
import com.arcadelabs.spiderlily.core.util.ext.observe
import com.arcadelabs.spiderlily.databinding.SheetBaseBinding

@AndroidEntryPoint
class TrackerCategoriesConfigSheet :
	BaseAdaptiveSheet<SheetBaseBinding>(),
	OnListItemClickListener<FavouriteCategory> {

	private val viewModel by viewModels<TrackerCategoriesConfigViewModel>()

	override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?): SheetBaseBinding {
		return SheetBaseBinding.inflate(inflater, container, false)
	}

	override fun onViewBindingCreated(binding: SheetBaseBinding, savedInstanceState: Bundle?) {
		super.onViewBindingCreated(binding, savedInstanceState)
		binding.headerBar.setTitle(R.string.favourites_categories)
		val adapter = TrackerCategoriesConfigAdapter(this)
		binding.recyclerView.adapter = adapter

		viewModel.content.observe(viewLifecycleOwner, adapter)
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val typeMask = WindowInsetsCompat.Type.systemBars()
		viewBinding?.recyclerView?.updatePadding(
			bottom = insets.getInsets(typeMask).bottom,
		)
		return insets.consume(v, typeMask, bottom = true)
	}

	override fun onItemClick(item: FavouriteCategory, view: View) {
		viewModel.toggleItem(item)
	}
}
