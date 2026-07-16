package com.arcadelabs.spiderlily.mihon.parsers.exception

import com.arcadelabs.spiderlily.mihon.parsers.InternalParsersApi
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentSource
import okio.IOException

/**
 * Authorization is required for access to the requested content
 */
public class AuthRequiredException @InternalParsersApi @JvmOverloads constructor(
	public val source: ContentSource,
	cause: Throwable? = null,
) : IOException("Authorization required", cause)

