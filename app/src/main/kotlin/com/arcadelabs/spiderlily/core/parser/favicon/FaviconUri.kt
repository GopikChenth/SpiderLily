package com.arcadelabs.spiderlily.core.parser.favicon

import android.net.Uri
import com.arcadelabs.spiderlily_parser.model.MangaSource

const val URI_SCHEME_FAVICON = "favicon"

fun MangaSource.faviconUri(): Uri = Uri.fromParts(URI_SCHEME_FAVICON, name, null)
