package com.arcadelabs.spiderlily.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arcadelabs.spiderlily.core.db.dao.ChaptersDao
import com.arcadelabs.spiderlily.core.db.dao.FavouritesDao
import com.arcadelabs.spiderlily.core.db.dao.HistoryDao
import com.arcadelabs.spiderlily.core.db.dao.MangaDao
import com.arcadelabs.spiderlily.core.db.entity.ChapterEntity
import com.arcadelabs.spiderlily.core.db.entity.FavouriteCategoryEntity
import com.arcadelabs.spiderlily.core.db.entity.FavouriteEntity
import com.arcadelabs.spiderlily.core.db.entity.HistoryEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity

@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        HistoryEntity::class,
        FavouriteEntity::class,
        FavouriteCategoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class SpiderLilyDatabase : RoomDatabase() {

    abstract fun getMangaDao(): MangaDao

    abstract fun getChaptersDao(): ChaptersDao

    abstract fun getHistoryDao(): HistoryDao

    abstract fun getFavouritesDao(): FavouritesDao

    companion object {
        private const val DB_NAME = "spiderlily-db"

        @Volatile
        private var INSTANCE: SpiderLilyDatabase? = null

        fun getInstance(context: Context): SpiderLilyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): SpiderLilyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SpiderLilyDatabase::class.java,
                DB_NAME,
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
