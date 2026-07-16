package com.arcadelabs.spiderlily.settings.nav.model

import com.arcadelabs.spiderlily.list.ui.model.ListModel

data class NavItemAddModel(
	val canAdd: Boolean,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean = other is NavItemAddModel
}
