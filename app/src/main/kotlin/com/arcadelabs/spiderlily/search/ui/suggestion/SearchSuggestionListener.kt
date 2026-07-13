package com.arcadelabs.spiderlily.search.ui.suggestion

import android.text.TextWatcher
import android.widget.TextView
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.model.MangaTag
import com.arcadelabs.spiderlily.search.domain.SearchKind

interface SearchSuggestionListener : TextWatcher, TextView.OnEditorActionListener {

	fun onMangaClick(manga: Manga)

	fun onQueryClick(query: String, kind: SearchKind, submit: Boolean)

	fun onSourceToggle(source: MangaSource, isEnabled: Boolean)

	fun onSourceClick(source: MangaSource)

	fun onTagClick(tag: MangaTag)
}
