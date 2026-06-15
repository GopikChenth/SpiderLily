package com.arcadelabs.spiderlily.browser.cloudflare

import com.arcadelabs.spiderlily.browser.BrowserCallback

interface CloudFlareCallback : BrowserCallback {

	override fun onTitleChanged(title: CharSequence, subtitle: CharSequence?) = Unit

	fun onPageLoaded()

	fun onCheckPassed()

	fun onLoopDetected()
}
