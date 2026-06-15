package com.arcadelabs.spiderlily.core.exceptions

class SyncApiException(
	message: String,
	val code: Int,
) : RuntimeException(message)
