package com.arcadelabs.spiderlily.scrobbling.discord.ui

import android.graphics.Bitmap
import android.webkit.WebView
import com.arcadelabs.spiderlily.browser.BrowserCallback
import com.arcadelabs.spiderlily.browser.BrowserClient
import org.koitharu.kotatsu.parsers.util.removeSurrounding

class DiscordTokenWebClient(private val callback: Callback) : BrowserClient(callback, null) {

	override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
		super.onPageStarted(view, url, favicon)
		if (view != null) {
			checkToken(view)
		}
	}

	private fun checkToken(view: WebView) {
		view.evaluateJavascript("window.localStorage.token") { result ->
			val token = result
				?.replace("\\\"", "")
				?.removeSurrounding('"')
				?.takeUnless { it == "null" }
			if (!token.isNullOrEmpty()) {
				callback.onTokenObtained(token)
			}
		}
	}

	interface Callback : BrowserCallback {

		fun onTokenObtained(token: String)
	}
}
