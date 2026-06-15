package com.arcadelabs.spiderlily.settings.tracker

import androidx.room.InvalidationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import okio.Closeable
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.db.TABLE_FAVOURITE_CATEGORIES
import com.arcadelabs.spiderlily.core.db.removeObserverAsync
import com.arcadelabs.spiderlily.core.ui.BaseViewModel
import com.arcadelabs.spiderlily.tracker.domain.TrackingRepository
import javax.inject.Inject

@HiltViewModel
class TrackerSettingsViewModel @Inject constructor(
	private val repository: TrackingRepository,
	private val database: MangaDatabase,
) : BaseViewModel() {

	val categoriesCount = MutableStateFlow<IntArray?>(null)

	init {
		updateCategoriesCount()
		val databaseObserver = DatabaseObserver(this)
		addCloseable(databaseObserver)
		launchJob(Dispatchers.IO) {
			database.invalidationTracker.addObserver(databaseObserver)
		}
	}

	private fun updateCategoriesCount() {
		launchJob(Dispatchers.IO) {
			categoriesCount.value = repository.getCategoriesCount()
		}
	}

	private class DatabaseObserver(private var vm: TrackerSettingsViewModel?) :
		InvalidationTracker.Observer(arrayOf(TABLE_FAVOURITE_CATEGORIES)),
		Closeable {

		override fun onInvalidated(tables: Set<String>) {
			vm?.updateCategoriesCount()
		}

		override fun close() {
			(vm ?: return).database.invalidationTracker.removeObserverAsync(this)
			vm = null
		}
	}
}
