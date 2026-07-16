package com.arcadelabs.spiderlily.core.nav

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arcadelabs.spiderlily.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppRouterEntryPoint {

	val settings: AppSettings
}
