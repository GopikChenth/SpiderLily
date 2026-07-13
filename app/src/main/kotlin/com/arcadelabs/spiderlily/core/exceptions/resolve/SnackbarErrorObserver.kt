package com.arcadelabs.spiderlily.core.exceptions.resolve

import android.view.View
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.core.util.ext.isSerializable
import com.arcadelabs.spiderlily.main.ui.owners.BottomNavOwner
import com.arcadelabs.spiderlily.main.ui.owners.BottomSheetOwner
import com.arcadelabs.spiderlily_parser.exception.ParseException

class SnackbarErrorObserver(
	host: View,
	fragment: Fragment?,
	resolver: ExceptionResolver?,
	onResolved: Consumer<Boolean>?,
) : ErrorObserver(host, fragment, resolver, onResolved) {

	constructor(
		host: View,
		fragment: Fragment?,
	) : this(host, fragment, null, null)

	override suspend fun emit(value: Throwable) {
		val snackbar = Snackbar.make(host, value.getDisplayMessage(host.context.resources), Snackbar.LENGTH_SHORT)
		when (activity) {
			is BottomNavOwner -> snackbar.anchorView = activity.bottomNav
			is BottomSheetOwner -> snackbar.anchorView = activity.bottomSheet
		}
		if (canResolve(value)) {
			snackbar.setAction(ExceptionResolver.getResolveStringId(value)) {
				resolve(value)
			}
		} else if (value is ParseException) {
			val router = router()
			if (router != null && value.isSerializable()) {
				snackbar.setAction(R.string.details) {
					router.showErrorDialog(value)
				}
			}
		}
		snackbar.show()
	}
}
