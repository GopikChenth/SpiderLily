package com.arcadelabs.spiderlily.scrobbling.common.domain.model

import javax.inject.Qualifier

@Qualifier
annotation class ScrobblerType(
	val service: ScrobblerService
)
