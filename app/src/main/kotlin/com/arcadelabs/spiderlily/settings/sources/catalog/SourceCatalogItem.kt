package com.arcadelabs.spiderlily.settings.sources.catalog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily_parser.model.MangaSource

sealed interface SourceCatalogItem : ListModel {

	data class Source(
		val source: MangaSource,
	) : SourceCatalogItem {

		override fun areItemsTheSame(other: ListModel): Boolean {
			return other is Source && other.source == source
		}
	}

	data class Hint(
		@DrawableRes val icon: Int,
		@StringRes val title: Int,
		@StringRes val text: Int,
	) : SourceCatalogItem {

		override fun areItemsTheSame(other: ListModel): Boolean {
			return other is Hint && other.title == title
		}
	}
}
