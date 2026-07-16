package com.arcadelabs.spiderlily.reader.ui.pager.webtoon

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.arcadelabs.spiderlily.core.exceptions.resolve.ExceptionResolver
import com.arcadelabs.spiderlily.core.os.NetworkState
import com.arcadelabs.spiderlily.databinding.ItemPageWebtoonBinding
import com.arcadelabs.spiderlily.reader.domain.PageLoader
import com.arcadelabs.spiderlily.reader.ui.config.ReaderSettings
import com.arcadelabs.spiderlily.reader.ui.pager.BasePageHolder

class WebtoonHolder(
	owner: LifecycleOwner,
	binding: ItemPageWebtoonBinding,
	loader: PageLoader,
	readerSettingsProducer: ReaderSettings.Producer,
	networkState: NetworkState,
	exceptionResolver: ExceptionResolver,
) : BasePageHolder<ItemPageWebtoonBinding>(
	binding = binding,
	loader = loader,
	readerSettingsProducer = readerSettingsProducer,
	networkState = networkState,
	exceptionResolver = exceptionResolver,
	lifecycleOwner = owner,
) {

	override val ssiv = binding.ssiv

	private var scrollToRestore = 0

	init {
		bindingInfo.progressBar.setVisibilityAfterHide(View.GONE)
	}

	override fun onReady() {
		binding.ssiv.colorFilter = settings.colorFilter?.toColorFilter()
		with(binding.ssiv) {
			scrollTo(
				when {
					scrollToRestore != 0 -> scrollToRestore
					itemView.top < 0 -> getScrollRange()
					else -> 0
				},
			)
			scrollToRestore = 0
		}
	}

	fun getScrollY() = binding.ssiv.getScroll()

	fun restoreScroll(scroll: Int) {
		if (binding.ssiv.isReady) {
			binding.ssiv.scrollTo(scroll)
		} else {
			scrollToRestore = scroll
		}
	}
}
