package com.arcadelabs.spiderlily.settings.sources.catalog

import androidx.annotation.WorkerThread
import androidx.lifecycle.viewModelScope
import androidx.room.invalidationTrackerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.db.TABLE_SOURCES
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.ui.util.ReversibleAction
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.explore.data.MangaSourcesRepository
import com.arcadelabs.spiderlily.explore.data.SourcesSortOrder
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import com.arcadelabs.spiderlily.mihon.MihonExtensionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily_parser.model.ContentType
import com.arcadelabs.spiderlily_parser.model.MangaSource
import java.util.EnumSet
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SourcesCatalogViewModel @Inject constructor(
	private val repository: MangaSourcesRepository,
	private val mihonExtensionManager: MihonExtensionManager,
	db: MangaDatabase,
	settings: AppSettings,
) : BaseViewModel() {

	val onActionDone = MutableEventFlow<ReversibleAction>()
	val locales: Set<String?> = buildSet {
		repository.allMangaSources.forEach { add(it.locale) }
		mihonExtensionManager.getMihonMangaSources().forEach { add(it.locale) }
		add(null)
	}

	private val searchQuery = MutableStateFlow<String?>(null)
	val appliedFilter = MutableStateFlow(
		SourcesCatalogFilter(
			types = emptySet(),
			locale = Locale.getDefault().language.takeIf { it in locales },
			isNewOnly = false,
		),
	)

	val hasNewSources = repository.observeHasNewSources()
		.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, false)

	val contentTypes = MutableStateFlow<List<ContentType>>(emptyList())

	val content: StateFlow<List<ListModel>> = combine(
		searchQuery,
		appliedFilter,
		db.invalidationTrackerFlow(TABLE_SOURCES),
		mihonExtensionManager.installedExtensions,
	) { q, f, _, _ ->
		buildSourcesList(f, q)
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	init {
		repository.clearNewSourcesBadge()
		launchJob(Dispatchers.IO) {
			contentTypes.value = getContentTypes(settings.isNsfwContentDisabled)
		}
	}

	fun performSearch(query: String?) {
		searchQuery.value = query?.trim()
	}

	fun setLocale(value: String?) {
		appliedFilter.value = appliedFilter.value.copy(locale = value)
	}

	fun addSource(source: MangaSource) {
		launchJob(Dispatchers.IO) {
			val rollback = repository.setSourcesEnabled(setOf(source), true)
			onActionDone.call(ReversibleAction(R.string.source_enabled, rollback))
		}
	}

	fun setContentType(value: ContentType, isAdd: Boolean) {
		val filter = appliedFilter.value
		val types = EnumSet.noneOf(ContentType::class.java)
		types.addAll(filter.types)
		if (isAdd) {
			types.add(value)
		} else {
			types.remove(value)
		}
		appliedFilter.value = filter.copy(types = types)
	}

	fun setNewOnly(value: Boolean) {
		appliedFilter.value = appliedFilter.value.copy(isNewOnly = value)
	}

	private suspend fun buildSourcesList(filter: SourcesCatalogFilter, query: String?): List<SourceCatalogItem> {
		val sources = repository.queryParserSources(
			isDisabledOnly = true,
			isNewOnly = filter.isNewOnly,
			excludeBroken = false,
			types = filter.types,
			query = query,
			locale = filter.locale,
			sortOrder = SourcesSortOrder.ALPHABETIC,
		)
		return if (sources.isEmpty()) {
			listOf(
				if (query == null) {
					SourceCatalogItem.Hint(
						icon = R.drawable.ic_empty_feed,
						title = R.string.no_manga_sources,
						text = R.string.no_manga_sources_catalog_text,
					)
				} else {
					SourceCatalogItem.Hint(
						icon = R.drawable.ic_empty_feed,
						title = R.string.nothing_found,
						text = R.string.no_manga_sources_found,
					)
				},
			)
		} else {
			sources.map {
				SourceCatalogItem.Source(source = it)
			}
		}
	}

	@WorkerThread
	private fun getContentTypes(isNsfwDisabled: Boolean): List<ContentType> {
		val result = buildSet {
			repository.allMangaSources.forEach { add(it.contentType) }
			mihonExtensionManager.getMihonMangaSources().forEach { 
				when(it.contentType) {
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANGA -> add(ContentType.MANGA)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.HENTAI_MANGA -> add(ContentType.HENTAI)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.COMICS -> add(ContentType.COMICS)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANHWA -> add(ContentType.MANHWA)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANHUA -> add(ContentType.MANHUA)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.NOVEL -> add(ContentType.NOVEL)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.ONE_SHOT -> add(ContentType.ONE_SHOT)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.DOUJINSHI -> add(ContentType.DOUJINSHI)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.IMAGE_SET -> add(ContentType.IMAGE_SET)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.ARTIST_CG -> add(ContentType.ARTIST_CG)
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.GAME_CG -> add(ContentType.GAME_CG)
					else -> {}
				}
			}
		}.toList().sortedByDescending { type ->
			val kotatsuCount = repository.allMangaSources.count { it.contentType == type }
			val mihonCount = mihonExtensionManager.getMihonMangaSources().count { 
				when(it.contentType) {
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANGA -> type == ContentType.MANGA
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.HENTAI_MANGA -> type == ContentType.HENTAI
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.COMICS -> type == ContentType.COMICS
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANHWA -> type == ContentType.MANHWA
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.MANHUA -> type == ContentType.MANHUA
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.NOVEL -> type == ContentType.NOVEL
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.ONE_SHOT -> type == ContentType.ONE_SHOT
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.DOUJINSHI -> type == ContentType.DOUJINSHI
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.IMAGE_SET -> type == ContentType.IMAGE_SET
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.ARTIST_CG -> type == ContentType.ARTIST_CG
					com.arcadelabs.spiderlily.mihon.parsers.model.ContentType.GAME_CG -> type == ContentType.GAME_CG
					else -> false
				}
			}
			kotatsuCount + mihonCount
		}
		return if (isNsfwDisabled) {
			result.filterNot { it == ContentType.HENTAI }
		} else {
			result
		}
	}
}
