package com.arcadelabs.spiderlily.settings.userdata

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.observeAsFlow
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class BackupsSettingsViewModel @Inject constructor(
    private val settings: AppSettings,
) : BaseViewModel() {

    val periodicalBackupFrequency = settings.observeAsFlow(
        key = AppSettings.KEY_BACKUP_PERIODICAL_ENABLED,
        valueProducer = { isPeriodicalBackupEnabled },
    ).flatMapLatest { isEnabled ->
        if (isEnabled) {
            settings.observeAsFlow(
                key = AppSettings.KEY_BACKUP_PERIODICAL_FREQUENCY,
                valueProducer = { periodicalBackupFrequency },
            )
        } else {
            flowOf(0)
        }
    }
}
