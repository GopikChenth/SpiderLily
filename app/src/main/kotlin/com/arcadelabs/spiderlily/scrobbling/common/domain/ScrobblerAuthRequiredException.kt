package com.arcadelabs.spiderlily.scrobbling.common.domain

import okio.IOException
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerService

class ScrobblerAuthRequiredException(
	val scrobbler: ScrobblerService,
) : IOException()
