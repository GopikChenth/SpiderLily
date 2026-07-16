package com.arcadelabs.spiderlily.list.ui.model

import com.arcadelabs.spiderlily.core.ui.model.MangaOverride
import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.list.domain.ReadingProgress
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback.Companion.PAYLOAD_ANYTHING_CHANGED
import com.arcadelabs.spiderlily.list.ui.ListModelDiffCallback.Companion.PAYLOAD_PROGRESS_CHANGED
import com.arcadelabs.spiderlily_parser.model.Manga

data class MangaDetailedListModel(
	override val manga: Manga,
	override val override: MangaOverride?,
	val subtitle: String?,
	override val counter: Int,
	val progress: ReadingProgress?,
	val isFavorite: Boolean,
	val isSaved: Boolean,
	val tags: List<ChipsView.ChipModel>,
) : MangaListModel() {

	override fun getChangePayload(previousState: ListModel): Any? = when {
		previousState !is MangaDetailedListModel || previousState.manga != manga -> null

		previousState.progress != progress -> PAYLOAD_PROGRESS_CHANGED
		previousState.isFavorite != isFavorite ||
			previousState.isSaved != isSaved -> PAYLOAD_ANYTHING_CHANGED

		else -> super.getChangePayload(previousState)
	}
}
