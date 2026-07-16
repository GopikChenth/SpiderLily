package com.arcadelabs.spiderlily.download.ui.list

import androidx.lifecycle.LifecycleOwner
import com.arcadelabs.spiderlily.core.ui.BaseListAdapter
import com.arcadelabs.spiderlily.list.ui.adapter.ListItemType
import com.arcadelabs.spiderlily.list.ui.adapter.emptyStateListAD
import com.arcadelabs.spiderlily.list.ui.adapter.listHeaderAD
import com.arcadelabs.spiderlily.list.ui.adapter.loadingStateAD
import com.arcadelabs.spiderlily.list.ui.model.ListModel

class DownloadsAdapter(
	lifecycleOwner: LifecycleOwner,
	listener: DownloadItemListener,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.DOWNLOAD, downloadItemAD(lifecycleOwner, listener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
		addDelegate(ListItemType.HEADER, listHeaderAD(null))
	}
}
