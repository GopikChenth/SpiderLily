package com.arcadelabs.spiderlily.reader.ui

import com.arcadelabs.spiderlily.reader.ui.pager.ReaderPage

data class ReaderContent(
	val pages: List<ReaderPage>,
	val state: ReaderState?
)
