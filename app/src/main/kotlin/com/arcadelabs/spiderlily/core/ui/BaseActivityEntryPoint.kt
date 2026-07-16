package com.arcadelabs.spiderlily.core.ui

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arcadelabs.spiderlily.core.exceptions.resolve.ExceptionResolver
import com.arcadelabs.spiderlily.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BaseActivityEntryPoint {

	val settings: AppSettings

	val exceptionResolverFactory: ExceptionResolver.Factory
}
