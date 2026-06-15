package com.arcadelabs.spiderlily.favourites.ui.container

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.ui.util.PopupMenuMediator

class FavouritesTabConfigurationStrategy(
	private val adapter: FavouritesContainerAdapter,
	private val viewModel: FavouritesContainerViewModel,
	private val router: AppRouter,
) : TabConfigurationStrategy {

	override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
		val item = adapter.getItem(position)
		tab.text = item.title ?: tab.view.context.getString(R.string.all_favourites)
		tab.tag = item
		PopupMenuMediator(
			FavouriteTabPopupMenuProvider(tab.view.context, router, viewModel, item.id)
		).attach(tab.view)
	}
}
