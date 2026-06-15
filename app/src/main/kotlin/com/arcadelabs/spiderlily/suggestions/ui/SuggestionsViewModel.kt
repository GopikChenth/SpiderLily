package com.arcadelabs.spiderlily.suggestions.ui

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
import com.arcadelabs.spiderlily.core.prefs.observeAsFlow
import com.arcadelabs.spiderlily.core.util.ext.onFirst
import com.arcadelabs.spiderlily.list.domain.MangaListMapper
import com.arcadelabs.spiderlily.list.domain.QuickFilterListener
import com.arcadelabs.spiderlily.list.ui.MangaListViewModel
import com.arcadelabs.spiderlily.list.ui.model.EmptyState
import com.arcadelabs.spiderlily.list.ui.model.LoadingState
import com.arcadelabs.spiderlily.list.ui.model.toErrorState
import com.arcadelabs.spiderlily.suggestions.domain.SuggestionRepository
import com.arcadelabs.spiderlily.suggestions.domain.SuggestionsListQuickFilter
import javax.inject.Inject
import com.arcadelabs.spiderlily.local.data.LocalStorageChanges
import com.arcadelabs.spiderlily.local.domain.model.LocalManga
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
	repository: SuggestionRepository,
	settings: AppSettings,
	private val mangaListMapper: MangaListMapper,
	private val quickFilter: SuggestionsListQuickFilter,
	private val suggestionsScheduler: SuggestionsWorker.Scheduler,
	mangaDataRepository: MangaDataRepository,
	@LocalStorageChanges localStorageChanges: SharedFlow<LocalManga?>,
) : MangaListViewModel(settings, mangaDataRepository, localStorageChanges), QuickFilterListener by quickFilter {

	override val listMode = settings.observeAsFlow(AppSettings.KEY_LIST_MODE_SUGGESTIONS) { suggestionsListMode }
		.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, settings.suggestionsListMode)

	override val content = combine(
		quickFilter.appliedOptions.combineWithSettings().flatMapLatest { repository.observeAll(0, it) },
		quickFilter.appliedOptions,
		observeListModeWithTriggers(),
	) { list, filters, mode ->
		when {
			list.isEmpty() -> if (filters.isEmpty()) {
				listOf(
					EmptyState(
						icon = R.drawable.ic_empty_common,
						textPrimary = R.string.nothing_found,
						textSecondary = R.string.text_suggestion_holder,
						actionStringRes = 0,
					),
				)
			} else {
				listOfNotNull(
					quickFilter.filterItem(filters),
					EmptyState(
						icon = R.drawable.ic_empty_common,
						textPrimary = R.string.nothing_found,
						textSecondary = R.string.text_empty_holder_secondary_filtered,
						actionStringRes = 0,
					),
				)
			}

			else -> buildList(list.size + 1) {
				quickFilter.filterItem(filters)?.let(::add)
				mangaListMapper.toListModelList(this, list, mode)
			}
		}
	}.onStart {
		loadingCounter.increment()
	}.onFirst {
		loadingCounter.decrement()
	}.catch {
		emit(listOf(it.toErrorState(canRetry = false)))
	}.stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, listOf(LoadingState))

	override fun onRefresh() = Unit

	override fun onRetry() = Unit

	fun updateSuggestions() {
		launchJob(Dispatchers.IO) {
			suggestionsScheduler.startNow()
		}
	}
}
