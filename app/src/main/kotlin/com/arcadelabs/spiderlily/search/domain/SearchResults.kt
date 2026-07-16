package com.arcadelabs.spiderlily.search.domain

import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaListFilter
import com.arcadelabs.spiderlily_parser.model.SortOrder

data class SearchResults(
	val listFilter: MangaListFilter,
	val sortOrder: SortOrder,
	val manga: List<Manga>,
)
