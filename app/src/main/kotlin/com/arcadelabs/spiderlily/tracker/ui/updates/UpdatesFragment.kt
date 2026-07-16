package com.arcadelabs.spiderlily.tracker.ui.updates

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.ListSelectionController
import com.arcadelabs.spiderlily.list.ui.MangaListFragment

@AndroidEntryPoint
class UpdatesFragment : MangaListFragment() {

	override val viewModel by viewModels<UpdatesViewModel>()
	override val isSwipeRefreshEnabled = false

	override fun onScrolledToEnd() = Unit

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu
	): Boolean {
		menuInflater.inflate(R.menu.mode_updates, menu)
		return super.onCreateActionMode(controller, menuInflater, menu)
	}

	override fun onActionItemClicked(controller: ListSelectionController, mode: ActionMode?, item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_remove -> {
				viewModel.remove(controller.snapshot())
				mode?.finish()
				true
			}

			else -> super.onActionItemClicked(controller, mode, item)
		}
	}
}
