package com.arcadelabs.spiderlily.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcadelabs.spiderlily.core.db.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavouritesDao {

    @Query("SELECT * FROM favourites WHERE deleted_at = 0")
    abstract fun observeAll(): Flow<List<FavouriteEntity>>

    @Query("SELECT * FROM favourites WHERE manga_id = :mangaId AND deleted_at = 0")
    abstract suspend fun find(mangaId: Long): List<FavouriteEntity>

    @Query("SELECT EXISTS(SELECT * FROM favourites WHERE manga_id = :mangaId AND deleted_at = 0)")
    abstract suspend fun isFavourite(mangaId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: FavouriteEntity)

    @Query("UPDATE favourites SET deleted_at = :deletedAt WHERE manga_id = :mangaId")
    abstract suspend fun delete(mangaId: Long, deletedAt: Long)

    @Query("DELETE FROM favourites WHERE deleted_at != 0")
    abstract suspend fun gc()
}
