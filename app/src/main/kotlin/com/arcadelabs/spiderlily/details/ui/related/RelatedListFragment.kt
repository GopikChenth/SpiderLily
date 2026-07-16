package com.arcadelabs.spiderlily.details.ui.related

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.ListSelectionController
import com.arcadelabs.spiderlily.list.ui.MangaListFragment

@AndroidEntryPoint
class RelatedListFragment : MangaListFragment() {

	override val viewModel by viewModels<RelatedListViewModel>()
	override val isSwipeRefreshEnabled = false

	override fun onScrolledToEnd() = Unit

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu
	): Boolean {
		menuInflater.inflate(R.menu.mode_remote, menu)
		return super.onCreateActionMode(controller, menuInflater, menu)
	}
}

