package com.arcadelabs.spiderlily.tracker.ui.updates

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.parser.MangaDataRepository
import com.arcadelabs.spiderlily.core.prefs.AppSettings
import com.arcadelabs.spiderlily.core.prefs.ListMode
import com.arcadelabs.spiderlily.core.prefs.observeAsFlow
import com.arcadelabs.spiderlily.core.ui.model.DateTimeAgo
import com.arcadelabs.spiderlily.core.util.ext.calculateTimeAgo
import com.arcadelabs.spiderlily.core.util.ext.onFirst
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.domain.QuickFilterListener
import com.arcadelabs.spiderlily.list.ui.MangaListViewModel
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.ListHeader
import com.arcadelabs.spiderlily.list.ui.model.ListModel
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import com.arcadelabs.spiderlily.list.ui.model.toErrorState
import com.arcadelabs.spiderlily.tracker.domain.TrackingRepository
import com.arcadelabs.spiderlily.tracker.domain.UpdatesListQuickFilter
import com.arcadelabs.spiderlily.tracker.domain.model.MangaTracking
import javax.inject.Inject
import com.arcadelabs.spiderlily.local.data.LocalStorageChanges
import com.arcadelabs.spiderlily.local.domain.model.LocalManga
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class UpdatesViewModel @Inject constructor(
	private val repository: TrackingRepository,
	settings: AppSettings,
	private val mangaListMapper: MangaListMapper,
	private val quickFilter: UpdatesListQuickFilter,
	mangaDataRepository: MangaDataRepository,
	@LocalStorageChanges localStorageChanges: SharedFlow<LocalManga?>,
) : MangaListViewModel(settings, mangaDataRepository, localStorageChanges), QuickFilterListener by quickFilter {

	override val content = combine(
		quickFilter.appliedOptions.flatMapLatest { filterOptions ->
			repository.observeUpdatedManga(
				limit = 0,
				filterOptions = filterOptions,
			)
		},
		quickFilter.appliedOptions,
		settings.observeAsFlow(AppSettings.KEY_UPDATED_GROUPING) { isUpdatedGroupingEnabled },
		observeListModeWithTriggers(),
	) { mangaList, filters, grouping, mode ->
		when {
			mangaList.isEmpty() -> listOfNotNull(
				quickFilter.filterItem(filters),
				EmptyState(
					icon = R.drawable.ic_empty_history,
					textPrimary = R.string.text_history_holder_primary,
					textSecondary = R.string.text_history_holder_secondary,
					actionStringRes = 0,
				),
			)

			else -> mangaList.toUi(mode, filters, grouping)
		}
	}.onStart {
		loadingCounter.increment()
	}.onFirst {
		loadingCounter.decrement()
	}.catch {
		emit(listOf(it.toErrorState(canRetry = false)))
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	init {
		launchJob(Dispatchers.IO) {
			repository.gc()
		}
	}

	override fun onRefresh() = Unit

	override fun onRetry() = Unit

	fun remove(ids: Set<Long>) {
		launchJob(Dispatchers.IO) {
			repository.clearUpdates(ids)
		}
	}

	private suspend fun List<MangaTracking>.toUi(
		mode: ListMode,
		filters: Set<ListFilterOption>,
		grouped: Boolean,
	): List<ListModel> {
		val result = ArrayList<ListModel>(if (grouped) (size * 1.4).toInt() else size + 1)
		quickFilter.filterItem(filters)?.let(result::add)
		var prevHeader: DateTimeAgo? = null
		for (item in this) {
			if (grouped) {
				val header = item.lastChapterDate?.let { calculateTimeAgo(it) }
				if (header != prevHeader) {
					if (header != null) {
						result += ListHeader(header)
					}
					prevHeader = header
				}
			}
			result += mangaListMapper.toListModel(item.manga, mode)
		}
		return result
	}
}
