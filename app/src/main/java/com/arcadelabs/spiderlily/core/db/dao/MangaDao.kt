package com.arcadelabs.spiderlily.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MangaDao {

    @Query("SELECT * FROM manga WHERE manga_id = :id")
    abstract suspend fun find(id: Long): MangaEntity?

    @Query("SELECT EXISTS(SELECT * FROM manga WHERE manga_id = :id)")
    abstract suspend operator fun contains(id: Long): Boolean

    @Query("SELECT * FROM manga WHERE source = :source")
    abstract suspend fun findAllBySource(source: String): List<MangaEntity>

    @Query("SELECT * FROM manga WHERE title LIKE :query LIMIT :limit")
    abstract suspend fun searchByTitle(query: String, limit: Int = 20): List<MangaEntity>

    @Query("SELECT * FROM manga WHERE manga_id IN (SELECT manga_id FROM favourites WHERE deleted_at = 0)")
    abstract fun observeFavourites(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE manga_id IN (SELECT manga_id FROM history WHERE deleted_at = 0) ORDER BY (SELECT updated_at FROM history WHERE history.manga_id = manga.manga_id) DESC")
    abstract fun observeHistory(): Flow<List<MangaEntity>>

    @Upsert
    abstract suspend fun upsert(manga: MangaEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(manga: MangaEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(manga: MangaEntity): Int

    @Query("DELETE FROM manga WHERE manga_id = :id")
    abstract suspend fun delete(id: Long)
}
