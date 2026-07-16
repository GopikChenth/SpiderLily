package com.arcadelabs.spiderlily.stats.domain

import com.arcadelabs.spiderlily.details.data.ReadingTime
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily_parser.model.Manga
import java.util.concurrent.TimeUnit

data class StatsRecord(
	val manga: Manga?,
	val tagName: String? = null,
	val duration: Long,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is StatsRecord && other.manga == manga && other.tagName == tagName
	}

	val time: ReadingTime

	init {
		val minutes = TimeUnit.MILLISECONDS.toMinutes(duration).toInt()
		time = ReadingTime(
			minutes = minutes % 60,
			hours = minutes / 60,
			isContinue = false,
		)
	}
}
