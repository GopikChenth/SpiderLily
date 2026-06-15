package com.arcadelabs.spiderlily.core.exceptions

class IncompatiblePluginException(
	val name: String?,
	cause: Throwable?,
) : RuntimeException(cause)
