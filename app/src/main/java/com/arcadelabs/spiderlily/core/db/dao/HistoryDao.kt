package com.arcadelabs.spiderlily.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arcadelabs.spiderlily.core.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao {

    @Query("SELECT * FROM history WHERE deleted_at = 0 ORDER BY updated_at DESC")
    abstract fun observeAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE manga_id = :mangaId AND deleted_at = 0")
    abstract suspend fun find(mangaId: Long): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(entity: HistoryEntity): Long

    @Update
    abstract suspend fun update(entity: HistoryEntity)

    @Query("UPDATE history SET deleted_at = :deletedAt WHERE manga_id = :mangaId")
    abstract suspend fun delete(mangaId: Long, deletedAt: Long)

    @Query("DELETE FROM history WHERE deleted_at != 0")
    abstract suspend fun gc()
}
