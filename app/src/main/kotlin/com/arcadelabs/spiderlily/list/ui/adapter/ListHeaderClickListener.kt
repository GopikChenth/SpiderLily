package com.arcadelabs.spiderlily.list.ui.adapter

import android.view.View
import com.arcadelabs.spiderlily.list.ui.model.ListHeader

interface ListHeaderClickListener {

	fun onListHeaderClick(item: ListHeader, view: View)
}
