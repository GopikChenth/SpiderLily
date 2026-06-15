package com.arcadelabs.spiderlily.filter.ui.model

import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import org.koitharu.kotatsu.parsers.model.SortOrder

data class FilterHeaderModel(
	val chips: Collection<ChipsView.ChipModel>,
	val sortOrder: SortOrder?,
	val isFilterApplied: Boolean,
) {

	val textSummary: String
		get() = chips.mapNotNull { if (it.isChecked) it.title else null }.joinToString()
}
