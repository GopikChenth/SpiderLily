package com.arcadelabs.spiderlily.picker.ui.manga

import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.list.ui.MangaListFragment
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.picker.ui.PageImagePickActivity

@AndroidEntryPoint
class MangaPickerFragment : MangaListFragment() {

	override val isSwipeRefreshEnabled = false

	override val viewModel by viewModels<MangaPickerViewModel>()

	override fun onScrolledToEnd() = Unit

	override fun onItemClick(item: MangaListModel, view: View) {
		(activity as PageImagePickActivity).onMangaPicked(item.manga)
	}

	override fun onResume() {
		super.onResume()
		activity?.setTitle(R.string.pick_manga_page)
	}

	override fun onItemLongClick(item: MangaListModel, view: View): Boolean = false

	override fun onItemContextClick(item: MangaListModel, view: View): Boolean = false
}
