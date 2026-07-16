package com.arcadelabs.spiderlily.settings.about.changelog

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import com.arcadelabs.spiderlily.core.github.AppUpdateRepository
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ChangelogViewModel @Inject constructor(
	private val appUpdateRepository: AppUpdateRepository,
) : BaseViewModel() {

	val changelog = MutableStateFlow<String?>(null)

	init {
		launchLoadingJob(Dispatchers.IO) {
			changelog.value = appUpdateRepository.fetchChangelog()
		}
	}
}
