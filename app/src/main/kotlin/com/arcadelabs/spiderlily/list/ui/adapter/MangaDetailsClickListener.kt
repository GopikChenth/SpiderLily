package com.arcadelabs.spiderlily.list.ui.adapter

import android.view.View
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaTag

interface MangaDetailsClickListener : OnListItemClickListener<MangaListModel> {

	fun onReadClick(manga: Manga, view: View)

	fun onTagClick(manga: Manga, tag: MangaTag, view: View)
}
