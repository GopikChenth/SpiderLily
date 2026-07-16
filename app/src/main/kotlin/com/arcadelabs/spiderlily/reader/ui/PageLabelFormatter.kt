package com.arcadelabs.spiderlily.reader.ui

import com.google.android.material.slider.LabelFormatter
import com.arcadelabs.spiderlily_parser.util.format

class PageLabelFormatter : LabelFormatter {

	override fun getFormattedValue(value: Float): String {
		return (value + 1).format(0)
	}
}
