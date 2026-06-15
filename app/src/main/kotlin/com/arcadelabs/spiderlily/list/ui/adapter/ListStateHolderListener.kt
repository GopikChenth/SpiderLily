package com.arcadelabs.spiderlily.list.ui.adapter

interface ListStateHolderListener {

	fun onRetryClick(error: Throwable)

	fun onSecondaryErrorActionClick(error: Throwable) = Unit

	fun onEmptyActionClick()

	fun onFooterButtonClick() = Unit
}
