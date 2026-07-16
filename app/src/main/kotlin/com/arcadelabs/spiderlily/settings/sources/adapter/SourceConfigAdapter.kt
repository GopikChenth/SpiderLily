package com.arcadelabs.spiderlily.settings.sources.adapter

import com.arcadelabs.spiderlily.core.ui.ReorderableListAdapter
import com.arcadelabs.spiderlily.settings.sources.model.SourceConfigItem

class SourceConfigAdapter(
	listener: SourceConfigListener,
) : ReorderableListAdapter<SourceConfigItem>() {

	init {
		with(delegatesManager) {
			addDelegate(sourceConfigItemDelegate2(listener))
			addDelegate(sourceConfigEmptySearchDelegate())
			addDelegate(sourceConfigTipDelegate(listener))
		}
	}
}
