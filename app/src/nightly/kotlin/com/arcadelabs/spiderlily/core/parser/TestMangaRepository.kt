package com.arcadelabs.spiderlily.core.parser

import com.arcadelabs.spiderlily.core.cache.MemoryContentCache
import com.arcadelabs.spiderlily.core.model.TestMangaSource
import com.arcadelabs.spiderlily_parser.MangaLoaderContext

@Suppress("unused")
class TestMangaRepository(
	private val loaderContext: MangaLoaderContext,
	cache: MemoryContentCache
) : EmptyMangaRepository(TestMangaSource)
