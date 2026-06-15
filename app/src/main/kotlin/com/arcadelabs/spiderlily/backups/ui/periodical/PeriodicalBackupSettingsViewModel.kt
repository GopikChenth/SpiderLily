package com.arcadelabs.spiderlily.backups.ui.periodical

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.backups.domain.BackupUtils
import com.arcadelabs.spiderlily.backups.domain.ExternalBackupStorage
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.ui.util.ReversibleAction
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.core.util.ext.resolveFile
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PeriodicalBackupSettingsViewModel @Inject constructor(
	private val settings: AppSettings,
	private val telegramUploader: TelegramBackupUploader,
	private val backupStorage: ExternalBackupStorage,
	@ApplicationContext private val appContext: Context,
) : BaseViewModel() {

	val isTelegramAvailable
		get() = telegramUploader.isAvailable

	val lastBackupDate = MutableStateFlow<Date?>(null)
	val backupsDirectory = MutableStateFlow<String?>("")
	val isTelegramCheckLoading = MutableStateFlow(false)
	val onActionDone = MutableEventFlow<ReversibleAction>()

	init {
		updateSummaryData()
	}

	fun checkTelegram() {
		launchJob(Dispatchers.IO) {
			try {
				isTelegramCheckLoading.value = true
				telegramUploader.sendTestMessage()
				onActionDone.call(ReversibleAction(R.string.connection_ok, null))
			} finally {
				isTelegramCheckLoading.value = false
			}
		}
	}

	fun updateSummaryData() {
		updateBackupsDirectory()
		updateLastBackupDate()
	}

	private fun updateBackupsDirectory() = launchJob(Dispatchers.IO) {
		val dir = settings.periodicalBackupDirectory
		backupsDirectory.value = if (dir != null) {
			dir.toUserFriendlyString()
		} else {
			BackupUtils.getAppBackupDir(appContext).path
		}
	}

	private fun updateLastBackupDate() = launchJob(Dispatchers.IO) {
		lastBackupDate.value = backupStorage.getLastBackupDate()
	}

	private fun Uri.toUserFriendlyString(): String? {
		val df = DocumentFile.fromTreeUri(appContext, this)
		if (df?.canWrite() != true) {
			return null
		}
		return resolveFile(appContext)?.path ?: toString()
	}
}
