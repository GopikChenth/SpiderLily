package com.arcadelabs.spiderlily.alternatives.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.alternatives.domain.AlternativesUseCase
import com.arcadelabs.spiderlily.alternatives.domain.MigrateUseCase
import com.arcadelabs.spiderlily.core.model.chaptersCount
import com.arcadelabs.spiderlily.core.model.parcelable.ParcelableManga
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.prefs.ListMode
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.append
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.core.util.ext.require
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.ui.model.ButtonFooter
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingFooter
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import com.arcadelabs.spiderlily.list.ui.model.MangaGridModel
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.util.suspendlazy.getOrDefault
import com.arcadelabs.spiderlily_parser.util.suspendlazy.suspendLazy
import javax.inject.Inject

@HiltViewModel
class AlternativesViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val mangaRepositoryFactory: MangaRepository.Factory,
	private val alternativesUseCase: AlternativesUseCase,
	private val migrateUseCase: MigrateUseCase,
	private val mangaListMapper: MangaListMapper,
) : BaseViewModel() {

	val manga = savedStateHandle.require<ParcelableManga>(AppRouter.KEY_MANGA).manga

	private var includeDisabledSources = MutableStateFlow(false)
	private val results = MutableStateFlow<List<MangaAlternativeModel>>(emptyList())

	private var migrationJob: Job? = null
	private var searchJob: Job? = null

	private val mangaDetails = suspendLazy {
		mangaRepositoryFactory.create(manga.source).getDetails(manga)
	}

	val onMigrated = MutableEventFlow<Manga>()

	val list: StateFlow<List<ListModel>> = combine(
		results,
		isLoading,
		includeDisabledSources,
	) { list, loading, includeDisabled ->
		when {
			list.isEmpty() -> listOf(
				when {
					loading -> LoadingState
					else -> EmptyState(
						icon = R.drawable.ic_empty_common,
						textPrimary = R.string.nothing_found,
						textSecondary = R.string.text_search_holder_secondary,
						actionStringRes = 0,
					)
				},
			)

			loading -> list + LoadingFooter()
			includeDisabled -> list
			else -> list + ButtonFooter(R.string.search_disabled_sources)
		}
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	init {
		doSearch(throughDisabledSources = false)
	}

	fun retry() {
		searchJob?.cancel()
		results.value = emptyList()
		includeDisabledSources.value = false
		doSearch(throughDisabledSources = false)
	}

	fun continueSearch() {
		if (includeDisabledSources.value) {
			return
		}
		val prevJob = searchJob
		searchJob = launchLoadingJob(Dispatchers.IO) {
			includeDisabledSources.value = true
			prevJob?.join()
			doSearch(throughDisabledSources = true)
		}
	}

	fun migrate(target: Manga) {
		if (migrationJob?.isActive == true) {
			return
		}
		migrationJob = launchLoadingJob(Dispatchers.IO) {
			migrateUseCase(manga, target)
			onMigrated.call(target)
		}
	}

	private fun doSearch(throughDisabledSources: Boolean) {
		val prevJob = searchJob
		searchJob = launchLoadingJob(Dispatchers.IO) {
			prevJob?.cancelAndJoin()
			val ref = mangaDetails.getOrDefault(manga)
			val refCount = ref.chaptersCount()
			alternativesUseCase.invoke(ref, throughDisabledSources)
				.collect {
					val model = MangaAlternativeModel(
						mangaModel = mangaListMapper.toListModel(it, ListMode.GRID) as MangaGridModel,
						referenceChapters = refCount,
					)
					results.append(model)
				}
		}
	}
}
