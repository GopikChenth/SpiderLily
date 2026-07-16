package com.arcadelabs.spiderlily.list.ui.adapter

import android.view.View
import com.arcadelabs.spiderlily.core.ui.widgets.TipView

interface MangaListListener : MangaDetailsClickListener, ListStateHolderListener, ListHeaderClickListener,
	TipView.OnButtonClickListener, QuickFilterClickListener {

	fun onFilterClick(view: View?)
}
