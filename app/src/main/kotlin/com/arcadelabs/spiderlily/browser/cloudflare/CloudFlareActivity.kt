package com.arcadelabs.spiderlily.browser.cloudflare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.yield
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.browser.BaseBrowserActivity
import com.arcadelabs.spiderlily.core.exceptions.CloudFlareProtectedException
import com.arcadelabs.spiderlily.core.exceptions.resolve.CaptchaHandler
import com.arcadelabs.spiderlily.core.model.MangaSource
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.network.cookies.MutableCookieJar
import com.arcadelabs.spiderlily.core.parser.ParserMangaRepository
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily_parser.config.ConfigKey
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.network.CloudFlareHelper
import com.arcadelabs.spiderlily_parser.util.ifNullOrEmpty
import com.arcadelabs.spiderlily_parser.util.runCatchingCancellable
import javax.inject.Inject

@AndroidEntryPoint
class CloudFlareActivity : BaseBrowserActivity(), CloudFlareCallback {

	private var pendingResult = RESULT_CANCELED

	@Inject
	lateinit var cookieJar: MutableCookieJar

	@Inject
	lateinit var captchaHandler: CaptchaHandler

	private lateinit var cfClient: CloudFlareClient

	override fun onCreate2(savedInstanceState: Bundle?, source: MangaSource, repository: ParserMangaRepository?) {
		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = true)
		val url = intent?.dataString
		if (url.isNullOrEmpty()) {
			finishAfterTransition()
			return
		}

		// Check if source needs header interception
		val needsInterception = shouldUseInterception(source, repository)
		Log.d(TAG, "Source: ${source.name}, needsInterception: $needsInterception")

		cfClient = if (needsInterception) {
			Log.d(TAG, "Using CloudFlareInterceptClient with header filtering")
			CloudFlareInterceptClient(cookieJar, this, adBlock, url)
		} else {
			Log.d(TAG, "Using regular CloudFlareClient (no interception)")
			CloudFlareClient(cookieJar, this, adBlock, url)
		}

		viewBinding.webView.webViewClient = cfClient
		lifecycleScope.launch {
			try {
				proxyProvider.applyWebViewConfig()
			} catch (e: Exception) {
				Snackbar.make(viewBinding.webView, e.getDisplayMessage(resources), Snackbar.LENGTH_LONG).show()
			}
			if (savedInstanceState == null) {
				onTitleChanged(getString(R.string.loading_), url)
				viewBinding.webView.loadUrl(url)
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.opt_captcha, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		android.R.id.home -> {
			viewBinding.webView.stopLoading()
			finishAfterTransition()
			true
		}

		R.id.action_retry -> {
			restartCheck()
			true
		}

		else -> super.onOptionsItemSelected(item)
	}

	override fun finish() {
		setResult(pendingResult)
		super.finish()
	}

	override fun onLoadingStateChanged(isLoading: Boolean) = Unit

	override fun onPageLoaded() {
		viewBinding.progressBar.isInvisible = true
	}

	override fun onLoopDetected() {
		restartCheck()
	}

	override fun onCheckPassed() {
		pendingResult = RESULT_OK
		lifecycleScope.launch {
			val source = intent?.getStringExtra(AppRouter.KEY_SOURCE)
			if (source != null) {
				runCatchingCancellable {
					captchaHandler.discard(MangaSource(source))
				}.onFailure {
					it.printStackTraceDebug("CloudFlareActivity::onCheckPassed")
				}
			}
			finishAfterTransition()
		}
	}

	override fun onTitleChanged(title: CharSequence, subtitle: CharSequence?) {
		setTitle(title)
		supportActionBar?.subtitle = subtitle?.toString()?.toHttpUrlOrNull()?.host.ifNullOrEmpty { subtitle }
	}

	private fun restartCheck() {
		lifecycleScope.launch {
			viewBinding.webView.stopLoading()
			yield()
			cfClient.reset()
			val targetUrl = intent?.dataString?.toHttpUrlOrNull()
			if (targetUrl != null) {
				clearCfCookies(targetUrl)
				viewBinding.webView.loadUrl(targetUrl.toString())
			}
		}
	}

	private suspend fun clearCfCookies(url: HttpUrl) = runInterruptible(Dispatchers.IO) {
		cookieJar.removeCookies(url) { cookie ->
			CloudFlareHelper.isCloudFlareCookie(cookie.name)
		}
	}

	private fun shouldUseInterception(source: MangaSource, repository: ParserMangaRepository?): Boolean {
		Log.d(TAG, "shouldUseInterception called for source: ${source.name}")
		Log.d(TAG, "Repository type: ${repository?.javaClass?.simpleName}")

		if (repository !is ParserMangaRepository) {
			Log.d(TAG, "Repository is not ParserMangaRepository, returning false")
			return false
		}

		// Check if parser has InterceptCloudflare ConfigKey
		val configKeys = repository.getConfigKeys()
		Log.d(TAG, "Config keys count: ${configKeys.size}")
		Log.d(TAG, "Config keys: ${configKeys.map { it.javaClass.simpleName }}")

		val interceptKey = configKeys.filterIsInstance<ConfigKey.InterceptCloudflare>().firstOrNull()
		Log.d(TAG, "InterceptCloudflare key found: ${interceptKey != null}")
		if (interceptKey != null) {
			Log.d(TAG, "InterceptCloudflare defaultValue: ${interceptKey.defaultValue}")
		}

		val result = interceptKey?.defaultValue == true
		Log.d(TAG, "Returning: $result")
		return result
	}

	class Contract : ActivityResultContract<CloudFlareProtectedException, Boolean>() {
		override fun createIntent(context: Context, input: CloudFlareProtectedException): Intent {
			return AppRouter.cloudFlareResolveIntent(context, input)
		}

		override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
			return resultCode == RESULT_OK
		}
	}

	companion object {

		const val TAG = "CloudFlareActivity"
	}
}
