package com.arcadelabs.spiderlily.scrobbling.common.ui.selector.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.core.util.ext.setTextAndVisible
import com.arcadelabs.spiderlily.core.util.ext.textAndVisible
import com.arcadelabs.spiderlily.databinding.ItemEmptyHintBinding
import com.arcadelabs.spiderlily.list.ui.adapter.ListStateHolderListener
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.scrobbling.common.ui.selector.model.ScrobblerHint

fun scrobblerHintAD(
	listener: ListStateHolderListener,
) = adapterDelegateViewBinding<ScrobblerHint, ListModel, ItemEmptyHintBinding>(
	{ inflater, parent -> ItemEmptyHintBinding.inflate(inflater, parent, false) },
) {

	binding.buttonRetry.setOnClickListener {
		val e = item.error
		if (e != null) {
			listener.onRetryClick(e)
		} else {
			listener.onEmptyActionClick()
		}
	}

	bind {
		binding.icon.setImageResource(item.icon)
		binding.textPrimary.setText(item.textPrimary)
		if (item.error != null) {
			binding.textSecondary.textAndVisible = item.error?.getDisplayMessage(context.resources)
		} else {
			binding.textSecondary.setTextAndVisible(item.textSecondary)
		}
		binding.buttonRetry.setTextAndVisible(item.actionStringRes)
	}
}
