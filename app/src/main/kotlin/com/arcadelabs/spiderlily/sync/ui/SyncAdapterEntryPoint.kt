package com.arcadelabs.spiderlily.sync.ui

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arcadelabs.spiderlily.sync.domain.SyncHelper

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncAdapterEntryPoint {
	val syncHelperFactory: SyncHelper.Factory
}
