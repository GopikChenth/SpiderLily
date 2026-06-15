package com.arcadelabs.spiderlily.settings.work

interface PeriodicWorkScheduler {

	suspend fun schedule()

	suspend fun unschedule()

	suspend fun isScheduled(): Boolean
}
