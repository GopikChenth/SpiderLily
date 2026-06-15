package com.arcadelabs.spiderlily.details.ui.related

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.parcelable.ParcelableManga
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.core.util.ext.require
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.ui.MangaListViewModel
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import com.arcadelabs.spiderlily.list.ui.model.toErrorState
import com.arcadelabs.spiderlily.local.data.LocalStorageChanges
import com.arcadelabs.spiderlily.local.domain.model.LocalManga
import org.koitharu.kotatsu.parsers.model.Manga
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class RelatedListViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	mangaRepositoryFactory: MangaRepository.Factory,
	settings: AppSettings,
	private val mangaListMapper: MangaListMapper,
	mangaDataRepository: MangaDataRepository,
	@LocalStorageChanges localStorageChanges: SharedFlow<LocalManga?>,
) : MangaListViewModel(settings, mangaDataRepository, localStorageChanges) {

	private val seed = savedStateHandle.require<ParcelableManga>(AppRouter.KEY_MANGA).manga
	private val repository = mangaRepositoryFactory.create(seed.source)
	private val mangaList = MutableStateFlow<List<Manga>?>(null)
	private val listError = MutableStateFlow<Throwable?>(null)
	private var loadingJob: Job? = null

	override val content = combine(
		mangaList,
		observeListModeWithTriggers(),
		listError,
	) { list, mode, error ->
		when {
			list.isNullOrEmpty() && error != null -> listOf(error.toErrorState(canRetry = true))
			list == null -> listOf(LoadingState)
			list.isEmpty() -> listOf(createEmptyState())
			else -> mangaListMapper.toListModelList(list, mode)
		}
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	init {
		loadList()
	}

	override fun onRefresh() {
		loadList()
	}

	override fun onRetry() {
		loadList()
	}

	private fun loadList(): Job {
		loadingJob?.let {
			if (it.isActive) return it
		}
		return launchLoadingJob(Dispatchers.IO) {
			try {
				listError.value = null
				mangaList.value = repository.getRelated(seed)
			} catch (e: CancellationException) {
				throw e
			} catch (e: Throwable) {
				e.printStackTraceDebug("RelatedListViewModel::loadList")
				listError.value = e
				if (!mangaList.value.isNullOrEmpty()) {
					errorEvent.call(e)
				}
			}
		}.also { loadingJob = it }
	}

	private fun createEmptyState() = EmptyState(
		icon = R.drawable.ic_empty_common,
		textPrimary = R.string.nothing_found,
		textSecondary = 0,
		actionStringRes = 0,
	)
}

