package com.arcadelabs.spiderlily.favourites.ui.container

import com.arcadelabs.spiderlily.list.ui.model.ListModel

data class FavouriteTabModel(
	val id: Long,
	val title: String?,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is FavouriteTabModel && other.id == id
	}
}
