package com.arcadelabs.spiderlily.filter.ui.tags

import com.arcadelabs.spiderlily_parser.model.MangaTag
import java.text.Collator
import java.util.Locale

class TagTitleComparator(lc: String?) : Comparator<MangaTag> {

	private val collator = lc?.let { Collator.getInstance(Locale(it)) }

	override fun compare(o1: MangaTag, o2: MangaTag): Int {
		val t1 = o1.title.lowercase()
		val t2 = o2.title.lowercase()
		return collator?.compare(t1, t2) ?: compareValues(t1, t2)
	}
}
