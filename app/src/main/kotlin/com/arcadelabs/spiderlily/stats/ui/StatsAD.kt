package com.arcadelabs.spiderlily.stats.ui

import android.content.res.ColorStateList
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.list.OnListItemClickListener
import com.arcadelabs.spiderlily.core.util.SpiderLilyColors
import com.arcadelabs.spiderlily.databinding.ItemStatsBinding
import com.arcadelabs.spiderlily.stats.domain.StatsRecord
import org.koitharu.kotatsu.parsers.model.Manga

fun statsAD(
	listener: OnListItemClickListener<Manga>,
) = adapterDelegateViewBinding<StatsRecord, StatsRecord, ItemStatsBinding>(
	{ layoutInflater, parent -> ItemStatsBinding.inflate(layoutInflater, parent, false) },
) {

	binding.root.setOnClickListener { v ->
		item.manga?.let { listener.onItemClick(it, v) }
	}

	bind {
		binding.textViewTitle.text = item.manga?.title ?: item.tagName ?: getString(R.string.other_manga)
		binding.textViewSummary.text = item.time.format(context.resources)
		binding.imageViewBadge.imageTintList = ColorStateList.valueOf(SpiderLilyColors.ofManga(context, item.manga))
		binding.root.isClickable = item.manga != null
	}
}
