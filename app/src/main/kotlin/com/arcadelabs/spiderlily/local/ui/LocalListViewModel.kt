package com.arcadelabs.spiderlily.local.ui

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.model.toChipModel
import com.arcadelabs.spiderlily.core.nav.AppRouter
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.ListMode
import com.arcadelabs.spiderlily.core.ui.widgets.ChipsView
import com.arcadelabs.spiderlily.core.util.ext.MutableEventFlow
import com.arcadelabs.spiderlily.core.util.ext.call
import com.arcadelabs.spiderlily.core.util.ext.toFileOrNull
import com.arcadelabs.spiderlily.core.util.ext.toUriOrNull
import com.arcadelabs.spiderlily.explore.data.MangaSourcesRepository
import com.arcadelabs.spiderlily.explore.domain.ExploreRepository
import com.arcadelabs.spiderlily.filter.ui.FilterCoordinator
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.domain.QuickFilterListener
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.MangaListModel
import com.arcadelabs.spiderlily.list.ui.model.QuickFilter
import com.arcadelabs.spiderlily.list.ui.model.TipModel
import com.arcadelabs.spiderlily.local.data.LocalStorageChanges
import com.arcadelabs.spiderlily.local.data.LocalStorageManager
import com.arcadelabs.spiderlily.local.domain.DeleteLocalMangaUseCase
import com.arcadelabs.spiderlily.local.domain.model.LocalManga
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily.remotelist.ui.RemoteListViewModel
import javax.inject.Inject

@HiltViewModel
class LocalListViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	mangaRepositoryFactory: MangaRepository.Factory,
	filterCoordinator: FilterCoordinator,
	private val settings: AppSettings,
	mangaListMapper: MangaListMapper,
	private val deleteLocalMangaUseCase: DeleteLocalMangaUseCase,
	exploreRepository: ExploreRepository,
	@param:LocalStorageChanges private val localStorageChanges: SharedFlow<LocalManga?>,
	private val localStorageManager: LocalStorageManager,
	sourcesRepository: MangaSourcesRepository,
	mangaDataRepository: MangaDataRepository,
) : RemoteListViewModel(
	savedStateHandle = savedStateHandle,
	mangaRepositoryFactory = mangaRepositoryFactory,
	filterCoordinator = filterCoordinator,
	settings = settings,
	mangaListMapper = mangaListMapper,
	exploreRepository = exploreRepository,
	sourcesRepository = sourcesRepository,
	mangaDataRepository = mangaDataRepository,
	localStorageChanges = localStorageChanges,
), SharedPreferences.OnSharedPreferenceChangeListener, QuickFilterListener {

	val onMangaRemoved = MutableEventFlow<Unit>()
	private val showInlineFilter: Boolean = savedStateHandle[AppRouter.KEY_IS_BOTTOMTAB] ?: false

	init {
		launchJob(Dispatchers.IO) {
			localStorageChanges
				.collect {
					loadList(filterCoordinator.snapshot(), append = false).join()
				}
		}
		settings.subscribe(this)
	}

	override suspend fun onBuildList(list: MutableList<ListModel>) {
		super.onBuildList(list)
		if (showInlineFilter) {
			createFilterHeader(maxCount = 16)?.let {
				list.add(0, it)
			}
		}
		if (!localStorageManager.hasExternalStoragePermission(isReadOnly = true)) {
			for (item in list) {
				if (item !is MangaListModel) {
					continue
				}
				val file = item.manga.url.toUriOrNull()?.toFileOrNull() ?: continue
				if (localStorageManager.isOnExternalStorage(file)) {
					val tip = TipModel(
						key = "permission",
						title = R.string.external_storage,
						text = R.string.missing_storage_permission,
						icon = R.drawable.ic_storage,
						primaryButtonText = R.string.fix,
						secondaryButtonText = R.string.settings,
					)
					list.add(0, tip)
					return
				}
			}
		}
	}

	override fun setFilterOption(option: ListFilterOption, isApplied: Boolean) {
		if (option is ListFilterOption.Tag) {
			filterCoordinator.toggleTag(option.tag, isApplied)
		}
	}

	override fun toggleFilterOption(option: ListFilterOption) {
		if (option is ListFilterOption.Tag) {
			val tag = option.tag
			val isSelected = tag in filterCoordinator.snapshot().listFilter.tags
			filterCoordinator.toggleTag(option.tag, !isSelected)
		}
	}

	override fun clearFilter() = filterCoordinator.reset()

	override fun onCleared() {
		settings.unsubscribe(this)
		super.onCleared()
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
		if (key == AppSettings.KEY_LOCAL_MANGA_DIRS) {
			onRefresh()
		}
	}

	fun delete(ids: Set<Long>) {
		launchLoadingJob(Dispatchers.IO) {
			deleteLocalMangaUseCase(ids)
			onMangaRemoved.call(Unit)
		}
	}

	override suspend fun mapMangaList(
		destination: MutableCollection<in ListModel>,
		manga: Collection<Manga>,
		mode: ListMode
	) = mangaListMapper.toListModelList(destination, manga, mode, MangaListMapper.NO_SAVED)

	override fun createEmptyState(canResetFilter: Boolean): EmptyState = if (canResetFilter) {
		super.createEmptyState(true)
	} else {
		EmptyState(
			icon = R.drawable.ic_empty_local,
			textPrimary = R.string.text_local_holder_primary,
			textSecondary = R.string.text_local_holder_secondary,
			actionStringRes = R.string._import,
		)
	}

	private suspend fun createFilterHeader(maxCount: Int): QuickFilter? {
		val appliedTags = filterCoordinator.snapshot().listFilter.tags
		val availableTags = repository.getFilterOptions().availableTags
		if (appliedTags.isEmpty() && availableTags.size < 3) {
			return null
		}
		val result = ArrayList<ChipsView.ChipModel>(minOf(availableTags.size, maxCount))
		appliedTags.mapTo(result) { tag ->
			ListFilterOption.Tag(tag).toChipModel(isChecked = true)
		}
		for (tag in availableTags) {
			if (result.size >= maxCount) {
				break
			}
			if (tag in appliedTags) {
				continue
			}
			result.add(ListFilterOption.Tag(tag).toChipModel(isChecked = false))
		}
		return QuickFilter(result)
	}
}
