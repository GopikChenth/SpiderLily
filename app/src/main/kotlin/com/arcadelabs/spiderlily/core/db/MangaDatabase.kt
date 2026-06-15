package com.arcadelabs.spiderlily.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.arcadelabs.spiderlily.bookmarks.data.BookmarkEntity
import com.arcadelabs.spiderlily.bookmarks.data.BookmarksDao
import com.arcadelabs.spiderlily.core.db.dao.ChaptersDao
import com.arcadelabs.spiderlily.core.db.dao.ExternalExtensionRepoDao
import com.arcadelabs.spiderlily.core.db.dao.MangaDao
import com.arcadelabs.spiderlily.core.db.dao.MangaSourcesDao
import com.arcadelabs.spiderlily.core.db.dao.PreferencesDao
import com.arcadelabs.spiderlily.core.db.dao.TagsDao
import com.arcadelabs.spiderlily.core.db.dao.TrackLogsDao
import com.arcadelabs.spiderlily.core.db.entity.ChapterEntity
import com.arcadelabs.spiderlily.core.db.entity.ExternalExtensionRepoEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaPrefsEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaSourceEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaTagsEntity
import com.arcadelabs.spiderlily.core.db.entity.TagEntity
import com.arcadelabs.spiderlily.core.db.migrations.Migration10To11
import com.arcadelabs.spiderlily.core.db.migrations.Migration11To12
import com.arcadelabs.spiderlily.core.db.migrations.Migration12To13
import com.arcadelabs.spiderlily.core.db.migrations.Migration13To14
import com.arcadelabs.spiderlily.core.db.migrations.Migration14To15
import com.arcadelabs.spiderlily.core.db.migrations.Migration15To16
import com.arcadelabs.spiderlily.core.db.migrations.Migration16To17
import com.arcadelabs.spiderlily.core.db.migrations.Migration17To18
import com.arcadelabs.spiderlily.core.db.migrations.Migration18To19
import com.arcadelabs.spiderlily.core.db.migrations.Migration19To20
import com.arcadelabs.spiderlily.core.db.migrations.Migration1To2
import com.arcadelabs.spiderlily.core.db.migrations.Migration20To21
import com.arcadelabs.spiderlily.core.db.migrations.Migration21To22
import com.arcadelabs.spiderlily.core.db.migrations.Migration22To23
import com.arcadelabs.spiderlily.core.db.migrations.Migration23To24
import com.arcadelabs.spiderlily.core.db.migrations.Migration24To23
import com.arcadelabs.spiderlily.core.db.migrations.Migration24To25
import com.arcadelabs.spiderlily.core.db.migrations.Migration25To26
import com.arcadelabs.spiderlily.core.db.migrations.Migration26To27
import com.arcadelabs.spiderlily.core.db.migrations.Migration27To28
import com.arcadelabs.spiderlily.core.db.migrations.Migration2To3
import com.arcadelabs.spiderlily.core.db.migrations.Migration3To4
import com.arcadelabs.spiderlily.core.db.migrations.Migration4To5
import com.arcadelabs.spiderlily.core.db.migrations.Migration5To6
import com.arcadelabs.spiderlily.core.db.migrations.Migration6To7
import com.arcadelabs.spiderlily.core.db.migrations.Migration7To8
import com.arcadelabs.spiderlily.core.db.migrations.Migration8To9
import com.arcadelabs.spiderlily.core.db.migrations.Migration9To10
import com.arcadelabs.spiderlily.core.util.ext.processLifecycleScope
import com.arcadelabs.spiderlily.favourites.data.FavouriteCategoriesDao
import com.arcadelabs.spiderlily.favourites.data.FavouriteCategoryEntity
import com.arcadelabs.spiderlily.favourites.data.FavouriteEntity
import com.arcadelabs.spiderlily.favourites.data.FavouritesDao
import com.arcadelabs.spiderlily.history.data.HistoryDao
import com.arcadelabs.spiderlily.history.data.HistoryEntity
import com.arcadelabs.spiderlily.local.data.index.LocalMangaIndexDao
import com.arcadelabs.spiderlily.local.data.index.LocalMangaIndexEntity
import com.arcadelabs.spiderlily.scrobbling.common.data.ScrobblingDao
import com.arcadelabs.spiderlily.scrobbling.common.data.ScrobblingEntity
import com.arcadelabs.spiderlily.stats.data.StatsDao
import com.arcadelabs.spiderlily.stats.data.StatsEntity
import com.arcadelabs.spiderlily.suggestions.data.SuggestionDao
import com.arcadelabs.spiderlily.suggestions.data.SuggestionEntity
import com.arcadelabs.spiderlily.tracker.data.TrackEntity
import com.arcadelabs.spiderlily.tracker.data.TrackLogEntity
import com.arcadelabs.spiderlily.tracker.data.TracksDao
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

const val DATABASE_VERSION = 28

@Database(
	entities = [
		MangaEntity::class, TagEntity::class, HistoryEntity::class, MangaTagsEntity::class, ChapterEntity::class,
		FavouriteCategoryEntity::class, FavouriteEntity::class, MangaPrefsEntity::class, TrackEntity::class,
		TrackLogEntity::class, SuggestionEntity::class, BookmarkEntity::class, ScrobblingEntity::class,
		MangaSourceEntity::class, StatsEntity::class, LocalMangaIndexEntity::class, ExternalExtensionRepoEntity::class,
	],
	version = DATABASE_VERSION,
)
abstract class MangaDatabase : RoomDatabase() {

	abstract fun getHistoryDao(): HistoryDao

	abstract fun getTagsDao(): TagsDao

	abstract fun getMangaDao(): MangaDao

	abstract fun getFavouritesDao(): FavouritesDao

	abstract fun getPreferencesDao(): PreferencesDao

	abstract fun getFavouriteCategoriesDao(): FavouriteCategoriesDao

	abstract fun getTracksDao(): TracksDao

	abstract fun getTrackLogsDao(): TrackLogsDao

	abstract fun getSuggestionDao(): SuggestionDao

	abstract fun getBookmarksDao(): BookmarksDao

	abstract fun getScrobblingDao(): ScrobblingDao

	abstract fun getSourcesDao(): MangaSourcesDao

	abstract fun getStatsDao(): StatsDao

	abstract fun getLocalMangaIndexDao(): LocalMangaIndexDao

	abstract fun getChaptersDao(): ChaptersDao

	abstract fun getExternalExtensionRepoDao(): ExternalExtensionRepoDao
}

fun getDatabaseMigrations(context: Context): Array<Migration> = arrayOf(
	Migration1To2(),
	Migration2To3(),
	Migration3To4(),
	Migration4To5(),
	Migration5To6(),
	Migration6To7(),
	Migration7To8(),
	Migration8To9(),
	Migration9To10(),
	Migration10To11(),
	Migration11To12(),
	Migration12To13(),
	Migration13To14(),
	Migration14To15(),
	Migration15To16(),
	Migration16To17(context),
	Migration17To18(),
	Migration18To19(),
	Migration19To20(),
	Migration20To21(),
	Migration21To22(),
	Migration22To23(),
	Migration23To24(),
	Migration24To23(),
	Migration24To25(),
	Migration25To26(),
	Migration26To27(),
	Migration27To28(),
)

fun MangaDatabase(context: Context): MangaDatabase = Room
	.databaseBuilder(context, MangaDatabase::class.java, "futon-db")
	.addMigrations(*getDatabaseMigrations(context))
	.addCallback(DatabasePrePopulateCallback(context.resources))
	.build()

fun InvalidationTracker.removeObserverAsync(observer: InvalidationTracker.Observer) {
	val scope = processLifecycleScope
	if (scope.isActive) {
		processLifecycleScope.launch(Dispatchers.IO, CoroutineStart.ATOMIC) {
			removeObserver(observer)
		}
	}
}
