package com.arcadelabs.spiderlily.mihon.extensions.runtime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun registerExternalExtensionPackageObserver(
	context: Context,
	onPackageChanged: suspend () -> Unit,
): BroadcastReceiver {
	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			val pendingResult = goAsync()
			ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
				try {
					onPackageChanged()
				} finally {
					pendingResult?.finish()
				}
			}
		}
	}
	ContextCompat.registerReceiver(
		context,
		receiver,
		IntentFilter().apply {
			addAction(Intent.ACTION_PACKAGE_ADDED)
			addAction(Intent.ACTION_PACKAGE_REPLACED)
			addAction(Intent.ACTION_PACKAGE_REMOVED)
			addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
			addDataScheme("package")
		},
		ContextCompat.RECEIVER_EXPORTED,
	)
	return receiver
}
