package com.arcadelabs.spiderlily.settings.nav.model

import androidx.annotation.StringRes
import com.arcadelabs.spiderlily.core.prefs.NavItem
import com.arcadelabs.spiderlily.list.ui.model.ListModel

data class NavItemConfigModel(
	val item: NavItem,
	@StringRes val disabledHintResId: Int,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is NavItemConfigModel && other.item == item
	}
}
