package com.arcadelabs.spiderlily.settings.sources.catalog

import com.arcadelabs.spiderlily_parser.model.ContentType

data class SourcesCatalogFilter(
	val types: Set<ContentType>,
	val locale: String?,
	val isNewOnly: Boolean,
)
