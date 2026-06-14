package com.arcadelabs.spiderlily.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.arcadelabs.spiderlily.core.db.TABLE_FAVOURITES

@Entity(
    tableName = TABLE_FAVOURITES,
    primaryKeys = ["manga_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["manga_id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class FavouriteEntity(
    @ColumnInfo(name = "manga_id") val mangaId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "sort_key") val sortKey: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long = 0,
)
