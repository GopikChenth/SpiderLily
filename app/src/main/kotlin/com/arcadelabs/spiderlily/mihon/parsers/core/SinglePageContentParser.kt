package com.arcadelabs.spiderlily.mihon.parsers.core

import com.arcadelabs.spiderlily.mihon.parsers.ContentLoaderContext
import com.arcadelabs.spiderlily.mihon.parsers.InternalParsersApi
import com.arcadelabs.spiderlily.mihon.parsers.model.Content
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentListFilter
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentSource
import com.arcadelabs.spiderlily.mihon.parsers.model.SortOrder

@InternalParsersApi
public abstract class SinglePageContentParser(
	context: ContentLoaderContext,
	source: ContentSource,
) : AbstractContentParser(context, source) {

	final override suspend fun getList(offset: Int, order: SortOrder, filter: ContentListFilter): List<Content> {
		if (offset > 0) {
			return emptyList()
		}
		return getList(order, filter)
	}

	public abstract suspend fun getList(order: SortOrder, filter: ContentListFilter): List<Content>
}

