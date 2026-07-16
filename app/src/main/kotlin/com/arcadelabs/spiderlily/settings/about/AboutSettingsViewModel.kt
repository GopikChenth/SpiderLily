package com.arcadelabs.spiderlily.settings.about

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import com.arcadelabs.spiderlily.core.github.AppUpdateRepository
import com.arcadelabs.spiderlily.core.github.AppVersion
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import javax.inject.Inject

@HiltViewModel
class AboutSettingsViewModel @Inject constructor(
	private val appUpdateRepository: AppUpdateRepository,
) : BaseViewModel() {

	val isUpdateSupported = flow {
		emit(appUpdateRepository.isUpdateSupported())
	}.stateIn(viewModelScope, SharingStarted.Eagerly, false)

	val onUpdateAvailable = MutableEventFlow<AppVersion?>()

	fun checkForUpdates() {
		launchLoadingJob {
			val update = appUpdateRepository.fetchUpdate()
			onUpdateAvailable.call(update)
		}
	}
}
