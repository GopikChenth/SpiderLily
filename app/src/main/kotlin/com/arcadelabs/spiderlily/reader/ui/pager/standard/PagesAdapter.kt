package com.arcadelabs.spiderlily.reader.ui.pager.standard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.arcadelabs.spiderlily.core.exceptions.resolve.ExceptionResolver
import com.arcadelabs.spiderlily.core.os.NetworkState
import com.arcadelabs.spiderlily.databinding.ItemPageBinding
import com.arcadelabs.spiderlily.reader.domain.PageLoader
import com.arcadelabs.spiderlily.reader.ui.config.ReaderSettings
import com.arcadelabs.spiderlily.reader.ui.pager.BaseReaderAdapter

class PagesAdapter(
	private val lifecycleOwner: LifecycleOwner,
	loader: PageLoader,
	readerSettingsProducer: ReaderSettings.Producer,
	networkState: NetworkState,
	exceptionResolver: ExceptionResolver,
) : BaseReaderAdapter<PageHolder>(
	loader = loader,
	readerSettingsProducer = readerSettingsProducer,
	networkState = networkState,
	exceptionResolver = exceptionResolver,
) {

	override fun onCreateViewHolder(
		parent: ViewGroup,
		loader: PageLoader,
		readerSettingsProducer: ReaderSettings.Producer,
		networkState: NetworkState,
		exceptionResolver: ExceptionResolver,
	) = PageHolder(
		owner = lifecycleOwner,
		binding = ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
		loader = loader,
		readerSettingsProducer = readerSettingsProducer,
		networkState = networkState,
		exceptionResolver = exceptionResolver,
	)
}
