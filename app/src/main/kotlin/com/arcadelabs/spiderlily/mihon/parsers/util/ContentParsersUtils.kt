@file:JvmName("ContentParsersUtils")

package com.arcadelabs.spiderlily.mihon.parsers.util

import com.arcadelabs.spiderlily.mihon.parsers.model.ContentChapter
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentListFilter
import kotlin.contracts.contract

fun ContentListFilter?.isNullOrEmpty(): Boolean {
	contract {
		returns(false) implies (this@isNullOrEmpty != null)
	}
	return this == null || this.isEmpty()
}

fun Collection<ContentChapter>.findById(chapterId: Long): ContentChapter? = find { x ->
	x.id == chapterId
}
