package com.arcadelabs.spiderlily.tracker.ui.feed.model

import com.arcadelabs.spiderlily.core.model.withOverride
import com.arcadelabs.spiderlily.core.ui.model.MangaOverride
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.util.ifNullOrEmpty

data class FeedItem(
	val id: Long,
	private val override: MangaOverride?,
	val manga: Manga,
	val count: Int,
	val isNew: Boolean,
) : ListModel {

	val imageUrl: String?
		get() = override?.coverUrl.ifNullOrEmpty { manga.coverUrl }

	val title: String
		get() = override?.title.ifNullOrEmpty { manga.title }

	fun toMangaWithOverride() = manga.withOverride(override)

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is FeedItem && other.id == id
	}

	override fun getChangePayload(previousState: ListModel): Any? = when {
		previousState !is FeedItem -> null
		isNew != previousState.isNew -> ListModelDiffCallback.PAYLOAD_ANYTHING_CHANGED
		else -> super.getChangePayload(previousState)
	}
}
