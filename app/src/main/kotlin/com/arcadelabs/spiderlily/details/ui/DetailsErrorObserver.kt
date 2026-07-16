package com.arcadelabs.spiderlily.details.ui

import com.google.android.material.snackbar.Snackbar
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.exceptions.UnsupportedSourceException
import com.arcadelabs.spiderlily.core.exceptions.resolve.ErrorObserver
import com.arcadelabs.spiderlily.core.exceptions.resolve.ExceptionResolver
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.core.util.ext.isNetworkError
import com.arcadelabs.spiderlily.core.util.ext.isSerializable
import com.arcadelabs.spiderlily_parser.exception.NotFoundException
import com.arcadelabs.spiderlily_parser.exception.ParseException

class DetailsErrorObserver(
	override val activity: DetailsActivity,
	private val viewModel: DetailsViewModel,
	resolver: ExceptionResolver?,
) : ErrorObserver(
	activity.viewBinding.scrollView, null, resolver,
	{ isResolved ->
		if (isResolved) {
			viewModel.reload()
		}
	},
) {

	override suspend fun emit(value: Throwable) {
		val snackbar = Snackbar.make(host, value.getDisplayMessage(host.context.resources), Snackbar.LENGTH_SHORT)
		snackbar.setAnchorView(activity.viewBinding.containerBottomSheet)
		if (value is NotFoundException || value is UnsupportedSourceException) {
			snackbar.duration = Snackbar.LENGTH_INDEFINITE
		}
		when {
			canResolve(value) -> {
				snackbar.setAction(ExceptionResolver.getResolveStringId(value)) {
					resolve(value)
				}
			}

			value is ParseException -> {
				val router = router()
				if (router != null && value.isSerializable()) {
					snackbar.setAction(R.string.details) {
						router.showErrorDialog(value)
					}
				}
			}

			value.isNetworkError() -> {
				snackbar.setAction(R.string.try_again) {
					viewModel.reload()
				}
			}
		}
		snackbar.show()
	}
}
