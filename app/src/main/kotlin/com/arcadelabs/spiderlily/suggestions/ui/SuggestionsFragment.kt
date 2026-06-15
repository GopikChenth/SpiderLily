package com.arcadelabs.spiderlily.suggestions.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.nav.router
import com.arcadelabs.spiderlily.core.ui.list.ListSelectionController
import com.arcadelabs.spiderlily.core.util.ext.addMenuProvider
import com.arcadelabs.spiderlily.databinding.FragmentListBinding
import com.arcadelabs.spiderlily.list.ui.MangaListFragment

class SuggestionsFragment : MangaListFragment() {

	override val viewModel by viewModels<SuggestionsViewModel>()
	override val isSwipeRefreshEnabled = false

	override fun onViewBindingCreated(binding: FragmentListBinding, savedInstanceState: Bundle?) {
		super.onViewBindingCreated(binding, savedInstanceState)
		addMenuProvider(SuggestionMenuProvider())
	}

	override fun onScrolledToEnd() = Unit

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu,
	): Boolean {
		menuInflater.inflate(R.menu.mode_remote, menu)
		return super.onCreateActionMode(controller, menuInflater, menu)
	}

	private inner class SuggestionMenuProvider : MenuProvider {

		override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
			menuInflater.inflate(R.menu.opt_suggestions, menu)
		}

		override fun onPrepareMenu(menu: Menu) {
			super.onPrepareMenu(menu)
			menu.findItem(R.id.action_settings_suggestions)?.isVisible =
				menu.findItem(R.id.action_settings) == null
		}

		override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
			R.id.action_update -> {
				viewModel.updateSuggestions()
				Snackbar.make(
					requireViewBinding().recyclerView,
					R.string.suggestions_updating,
					Snackbar.LENGTH_LONG,
				).show()
				true
			}

			R.id.action_settings_suggestions -> {
				router.openSuggestionsSettings()
				true
			}

			else -> false
		}
	}

	companion object {

		@Deprecated(
			"",
			ReplaceWith(
				"SuggestionsFragment()",
				"com.arcadelabs.spiderlily.suggestions.ui.SuggestionsFragment",
			),
		)
		fun newInstance() = SuggestionsFragment()
	}
}
