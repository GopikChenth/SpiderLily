package com.arcadelabs.spiderlily.list.ui

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.nav.router
import com.arcadelabs.spiderlily.favourites.ui.list.FavouritesListFragment
import com.arcadelabs.spiderlily.history.ui.HistoryListFragment
import com.arcadelabs.spiderlily.list.ui.config.ListConfigSection
import com.arcadelabs.spiderlily.suggestions.ui.SuggestionsFragment
import com.arcadelabs.spiderlily.tracker.ui.updates.UpdatesFragment

class MangaListMenuProvider(
	private val fragment: Fragment,
) : MenuProvider {

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.opt_list, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
		R.id.action_list_mode -> {
			val section: ListConfigSection = when (fragment) {
				is HistoryListFragment -> ListConfigSection.History
				is SuggestionsFragment -> ListConfigSection.Suggestions
				is FavouritesListFragment -> ListConfigSection.Favorites(fragment.categoryId)
				is UpdatesFragment -> ListConfigSection.Updated
				else -> ListConfigSection.General
			}
			fragment.router.showListConfigSheet(section)
			true
		}

		else -> false
	}
}
