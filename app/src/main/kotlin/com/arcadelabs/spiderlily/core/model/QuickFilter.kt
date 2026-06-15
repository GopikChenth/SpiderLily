package com.arcadelabs.spiderlily.core.model

import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.list.domain.ListFilterOption

fun ListFilterOption.toChipModel(isChecked: Boolean) = ChipsView.ChipModel(
	title = titleText,
	titleResId = titleResId,
	icon = iconResId,
	iconData = getIconData(),
	isChecked = isChecked,
	counter = if (this is ListFilterOption.Branch) chaptersCount else 0,
	data = this,
)
