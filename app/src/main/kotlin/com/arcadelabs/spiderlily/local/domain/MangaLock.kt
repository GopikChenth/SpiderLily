package com.arcadelabs.spiderlily.local.domain

import com.arcadelabs.spiderlily.core.util.MultiMutex
import com.arcadelabs.spiderlily_parser.model.Manga
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaLock @Inject constructor() : MultiMutex<Manga>()
