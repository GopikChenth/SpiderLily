package com.arcadelabs.spiderlily.details.service

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arcadelabs.spiderlily.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefetchCompanionEntryPoint {
	val settings: AppSettings
}
