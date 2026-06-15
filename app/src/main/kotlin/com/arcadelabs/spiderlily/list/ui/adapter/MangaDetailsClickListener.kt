package com.arcadelabs.spiderlily.list.ui.adapter

import android.view.View
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaTag

interface MangaDetailsClickListener : OnListItemClickListener<MangaListModel> {

	fun onReadClick(manga: Manga, view: View)

	fun onTagClick(manga: Manga, tag: MangaTag, view: View)
}
