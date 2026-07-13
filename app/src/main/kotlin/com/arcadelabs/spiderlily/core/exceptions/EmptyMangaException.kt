package com.arcadelabs.spiderlily.core.exceptions

import com.arcadelabs.spiderlily.details.ui.pager.EmptyMangaReason
import com.arcadelabs.spiderlily_parser.model.Manga

class EmptyMangaException(
    val reason: EmptyMangaReason?,
    val manga: Manga,
    cause: Throwable?
) : IllegalStateException(cause)
