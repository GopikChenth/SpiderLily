package com.arcadelabs.spiderlily.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import dagger.hilt.android.AndroidEntryPoint
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.os.AppShortcutManager
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.ListMode
import com.arcadelabs.spiderlily.core.prefs.ProgressIndicatorMode
import com.arcadelabs.spiderlily.core.prefs.ScreenshotsPolicy
import com.arcadelabs.spiderlily.core.prefs.SearchSuggestionType
import com.arcadelabs.spiderlily.core.prefs.TriStateOption
import com.arcadelabs.spiderlily.core.ui.BasePreferenceFragment
import com.arcadelabs.spiderlily.core.ui.util.ActivityRecreationHandle
import com.arcadelabs.spiderlily.core.util.LocaleComparator
import com.arcadelabs.spiderlily.core.util.ext.getLocalesConfig
import com.arcadelabs.spiderlily.core.util.ext.postDelayed
import com.arcadelabs.spiderlily.core.util.ext.setDefaultValueCompat
import com.arcadelabs.spiderlily.core.util.ext.sortedWithSafe
import com.arcadelabs.spiderlily.core.util.ext.toList
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.koitharu.kotatsu.parsers.util.names
import org.koitharu.kotatsu.parsers.util.toTitleCase
import com.arcadelabs.spiderlily.settings.protect.ProtectSetupActivity
import com.arcadelabs.spiderlily.settings.utils.ActivityListPreference
import com.arcadelabs.spiderlily.settings.utils.MultiSummaryProvider
import com.arcadelabs.spiderlily.settings.utils.PercentSummaryProvider
import com.arcadelabs.spiderlily.settings.utils.SliderPreference
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceSettingsFragment :
    BasePreferenceFragment(R.string.appearance),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var activityRecreationHandle: ActivityRecreationHandle

    @Inject
    lateinit var appShortcutManager: AppShortcutManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_appearance)
        findPreference<SliderPreference>(AppSettings.KEY_GRID_SIZE)?.summaryProvider = PercentSummaryProvider()
        findPreference<ListPreference>(AppSettings.KEY_LIST_MODE)?.run {
            entryValues = ListMode.entries.names()
            setDefaultValueCompat(ListMode.GRID.name)
        }
        findPreference<ListPreference>(AppSettings.KEY_PROGRESS_INDICATORS)?.run {
            entryValues = ProgressIndicatorMode.entries.names()
            setDefaultValueCompat(ProgressIndicatorMode.PERCENT_READ.name)
        }
        findPreference<ActivityListPreference>(AppSettings.KEY_APP_LOCALE)?.run {
            initLocalePicker(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityIntent = Intent(
                    Settings.ACTION_APP_LOCALE_SETTINGS,
                    Uri.fromParts("package", context.packageName, null),
                )
            }
            summaryProvider = Preference.SummaryProvider<ActivityListPreference> {
                val locale = AppCompatDelegate.getApplicationLocales().get(0)
                locale?.getDisplayName(locale)?.toTitleCase(locale) ?: getString(R.string.follow_system)
            }
            setDefaultValueCompat("")
        }
        findPreference<MultiSelectListPreference>(AppSettings.KEY_MANGA_LIST_BADGES)?.run {
            summaryProvider = MultiSummaryProvider(R.string.none)
        }
        findPreference<Preference>(AppSettings.KEY_SHORTCUTS)?.isVisible =
            appShortcutManager.isDynamicShortcutsAvailable()
        findPreference<TwoStatePreference>(AppSettings.KEY_PROTECT_APP)
            ?.isChecked = !settings.appPassword.isNullOrEmpty()
        findPreference<ListPreference>(AppSettings.KEY_SCREENSHOTS_POLICY)?.run {
            entryValues = ScreenshotsPolicy.entries.names()
            setDefaultValueCompat(ScreenshotsPolicy.ALLOW.name)
        }
        findPreference<MultiSelectListPreference>(AppSettings.KEY_SEARCH_SUGGESTION_TYPES)?.let { pref ->
            pref.entryValues = SearchSuggestionType.entries.names()
            pref.entries = SearchSuggestionType.entries.map { pref.context.getString(it.titleResId) }.toTypedArray()
            pref.summaryProvider = MultiSummaryProvider(R.string.none)
            pref.values = settings.searchSuggestionTypes.mapToSet { it.name }
        }
        bindNavSummary()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings.subscribe(this)
    }

    override fun onDestroyView() {
        settings.unsubscribe(this)
        super.onDestroyView()
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        when (key) {
            AppSettings.KEY_THEME -> {
                AppCompatDelegate.setDefaultNightMode(settings.theme)
            }

            AppSettings.KEY_COLOR_THEME,
            AppSettings.KEY_THEME_AMOLED,
                -> {
                postRestart()
            }

            AppSettings.KEY_APP_LOCALE -> {
                AppCompatDelegate.setApplicationLocales(settings.appLocales)
            }

            AppSettings.KEY_NAV_MAIN -> {
                bindNavSummary()
            }

            AppSettings.KEY_APP_PASSWORD -> {
                findPreference<TwoStatePreference>(AppSettings.KEY_PROTECT_APP)
                    ?.isChecked = !settings.appPassword.isNullOrEmpty()
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            AppSettings.KEY_PROTECT_APP -> {
                val pref = (preference as? TwoStatePreference ?: return false)
                if (pref.isChecked) {
                    pref.isChecked = false
                    startActivity(Intent(preference.context, ProtectSetupActivity::class.java))
                } else {
                    settings.appPassword = null
                }
                true
            }

            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun postRestart() {
        viewLifecycleOwner.lifecycle.postDelayed(400) {
            activityRecreationHandle.recreateAll()
        }
    }

    private fun initLocalePicker(preference: ListPreference) {
        val locales = preference.context.getLocalesConfig()
            .toList()
            .sortedWithSafe(LocaleComparator())
        preference.entries = Array(locales.size + 1) { i ->
            if (i == 0) {
                getString(R.string.follow_system)
            } else {
                val lc = locales[i - 1]
                lc.getDisplayName(lc).toTitleCase(lc)
            }
        }
        preference.entryValues = Array(locales.size + 1) { i ->
            if (i == 0) {
                ""
            } else {
                locales[i - 1].toLanguageTag()
            }
        }
    }

    private fun bindNavSummary() {
        val pref = findPreference<Preference>(AppSettings.KEY_NAV_MAIN) ?: return
        pref.summary = settings.mainNavItems.joinToString {
            getString(it.title)
        }
    }
}
