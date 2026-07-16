package com.arcadelabs.spiderlily.sync.ui

import android.accounts.AccountManager
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.sync.data.SyncAuthApi
import com.arcadelabs.spiderlily.sync.domain.SyncAuthResult
import javax.inject.Inject

@HiltViewModel
class SyncAuthViewModel @Inject constructor(
	@ApplicationContext context: Context,
	private val api: SyncAuthApi,
) : BaseViewModel() {

	val onAccountAlreadyExists = MutableEventFlow<Unit>()
	val onTokenObtained = MutableEventFlow<SyncAuthResult>()
	val syncURL = MutableStateFlow(context.resources.getStringArray(R.array.sync_url_list).first())

	init {
		launchJob(Dispatchers.IO) {
			val am = AccountManager.get(context)
			val accounts = am.getAccountsByType(context.getString(R.string.account_type_sync))
			if (accounts.isNotEmpty()) {
				onAccountAlreadyExists.call(Unit)
			}
		}
	}

	fun obtainToken(email: String, password: String) {
		val urlValue = syncURL.value
		launchLoadingJob(Dispatchers.IO) {
			val token = api.authenticate(urlValue, email, password)
			val result = SyncAuthResult(syncURL.value, email, password, token)
			onTokenObtained.call(result)
		}
	}
}
