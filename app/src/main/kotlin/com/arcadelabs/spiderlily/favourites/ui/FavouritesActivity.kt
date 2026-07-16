package com.arcadelabs.spiderlily.favourites.ui

import android.os.Bundle
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.ui.FragmentContainerActivity
import com.arcadelabs.spiderlily.favourites.ui.list.FavouritesListFragment

class FavouritesActivity : FragmentContainerActivity(FavouritesListFragment::class.java) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val categoryTitle = intent.getStringExtra(AppRouter.KEY_TITLE)
		if (categoryTitle != null) {
			title = categoryTitle
		}
	}
}
