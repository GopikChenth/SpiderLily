package com.arcadelabs.spiderlily.backups.ui.periodical

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.backups.data.BackupRepository
import com.arcadelabs.spiderlily.backups.domain.BackupUtils
import com.arcadelabs.spiderlily.backups.domain.ExternalBackupStorage
import com.arcadelabs.spiderlily.backups.ui.BaseBackupRestoreService
import com.arcadelabs.spiderlily.core.ErrorReporterReceiver
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.CoroutineIntentService
import com.arcadelabs.spiderlily.core.util.ext.checkNotificationPermission
import com.arcadelabs.spiderlily.core.util.ext.getDisplayMessage
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class PeriodicalBackupService : CoroutineIntentService() {

	@Inject
	lateinit var externalBackupStorage: ExternalBackupStorage

	@Inject
	lateinit var telegramBackupUploader: TelegramBackupUploader

	@Inject
	lateinit var repository: BackupRepository

	@Inject
	lateinit var settings: AppSettings

	override suspend fun IntentJobContext.processIntent(intent: Intent) {
		if (!settings.isPeriodicalBackupEnabled || settings.periodicalBackupDirectory == null) {
			return
		}
		val lastBackupDate = externalBackupStorage.getLastBackupDate()
		if (lastBackupDate != null && lastBackupDate.time + settings.periodicalBackupFrequencyMillis > System.currentTimeMillis()) {
			return
		}
		val output = BackupUtils.createTempFile(applicationContext)
		try {
			ZipOutputStream(output.outputStream()).use {
				repository.createBackup(it, null)
			}
			externalBackupStorage.put(output)
			externalBackupStorage.trim(settings.periodicalBackupMaxCount)
			if (settings.isBackupTelegramUploadEnabled && telegramBackupUploader.isAvailable) {
				telegramBackupUploader.uploadBackup(output)
			}
		} finally {
			output.delete()
		}
	}

	override fun IntentJobContext.onError(error: Throwable) {
		if (!applicationContext.checkNotificationPermission(CHANNEL_ID)) {
			return
		}
		BaseBackupRestoreService.createNotificationChannel(applicationContext)
		val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setDefaults(0)
			.setSilent(true)
			.setAutoCancel(true)
		val title = getString(R.string.periodic_backups)
		val message = getString(
			R.string.inline_preference_pattern,
			getString(R.string.packup_creation_failed),
			error.getDisplayMessage(resources),
		)
		notification
			.setContentText(message)
			.setSmallIcon(android.R.drawable.stat_notify_error)
			.setStyle(
				NotificationCompat.BigTextStyle()
					.bigText(message)
					.setSummaryText(getString(R.string.packup_creation_failed))
					.setBigContentTitle(title),
			)
		ErrorReporterReceiver.getNotificationAction(applicationContext, error, startId, TAG)?.let { action ->
			notification.addAction(action)
		}
		notification.setContentIntent(
			PendingIntentCompat.getActivity(
				applicationContext,
				0,
				AppRouter.periodicBackupSettingsIntent(applicationContext),
				0,
				false,
			),
		)
		NotificationManagerCompat.from(applicationContext).notify(TAG, startId, notification.build())
	}

	private companion object {

		const val CHANNEL_ID = BaseBackupRestoreService.CHANNEL_ID
		const val TAG = "periodical_backup"
	}
}
