package com.arcadelabs.spiderlily.core.ui.model

import androidx.annotation.StringRes
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.SortDirection
import com.arcadelabs.spiderlily_parser.model.SortOrder
import com.arcadelabs.spiderlily_parser.model.SortOrder.ADDED
import com.arcadelabs.spiderlily_parser.model.SortOrder.ADDED_ASC
import com.arcadelabs.spiderlily_parser.model.SortOrder.ALPHABETICAL
import com.arcadelabs.spiderlily_parser.model.SortOrder.ALPHABETICAL_DESC
import com.arcadelabs.spiderlily_parser.model.SortOrder.NEWEST
import com.arcadelabs.spiderlily_parser.model.SortOrder.NEWEST_ASC
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_ASC
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_HOUR
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_MONTH
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_TODAY
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_WEEK
import com.arcadelabs.spiderlily_parser.model.SortOrder.POPULARITY_YEAR
import com.arcadelabs.spiderlily_parser.model.SortOrder.RATING
import com.arcadelabs.spiderlily_parser.model.SortOrder.RATING_ASC
import com.arcadelabs.spiderlily_parser.model.SortOrder.RELEVANCE
import com.arcadelabs.spiderlily_parser.model.SortOrder.UPDATED
import com.arcadelabs.spiderlily_parser.model.SortOrder.UPDATED_ASC

@get:StringRes
val SortOrder.titleRes: Int
	get() = when (this) {
		UPDATED -> R.string.updated
		POPULARITY -> R.string.popular
		RATING -> R.string.by_rating
		NEWEST -> R.string.newest
		ALPHABETICAL -> R.string.by_name
		ALPHABETICAL_DESC -> R.string.by_name_reverse
		UPDATED_ASC -> R.string.updated_long_ago
		POPULARITY_ASC -> R.string.unpopular
		RATING_ASC -> R.string.low_rating
		NEWEST_ASC -> R.string.order_oldest
		ADDED -> R.string.recently_added
		ADDED_ASC -> R.string.added_long_ago
		RELEVANCE -> R.string.by_relevance
		POPULARITY_HOUR -> R.string.popular_in_hour
		POPULARITY_TODAY -> R.string.popular_today
		POPULARITY_WEEK -> R.string.popular_in_week
		POPULARITY_MONTH -> R.string.popular_in_month
		POPULARITY_YEAR -> R.string.popular_in_year
	}

val SortOrder.direction: SortDirection
	get() = when (this) {
		UPDATED_ASC,
		POPULARITY_ASC,
		RATING_ASC,
		NEWEST_ASC,
		ADDED_ASC,
		ALPHABETICAL -> SortDirection.ASC

		UPDATED,
		POPULARITY,
		POPULARITY_HOUR,
		POPULARITY_TODAY,
		POPULARITY_WEEK,
		POPULARITY_MONTH,
		POPULARITY_YEAR,
		RATING,
		NEWEST,
		ADDED,
		RELEVANCE,
		ALPHABETICAL_DESC -> SortDirection.DESC
	}
