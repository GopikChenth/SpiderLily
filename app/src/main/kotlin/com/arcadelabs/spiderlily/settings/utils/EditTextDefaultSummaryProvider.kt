package com.arcadelabs.spiderlily.settings.utils

import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily_parser.util.ifNullOrEmpty

class EditTextDefaultSummaryProvider(
	private val defaultValue: String,
) : Preference.SummaryProvider<EditTextPreference> {

	override fun provideSummary(
		preference: EditTextPreference,
	): CharSequence = preference.text.ifNullOrEmpty {
		preference.context.getString(R.string.default_s, defaultValue)
	}
}
