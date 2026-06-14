package com.arcadelabs.spiderlily.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.arcadelabs.spiderlily.core.db.TABLE_HISTORY

@Entity(
    tableName = TABLE_HISTORY,
    primaryKeys = ["manga_id"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["manga_id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class HistoryEntity(
    @ColumnInfo(name = "manga_id") val mangaId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long = 0,
    @ColumnInfo(name = "page") val page: Int = 0,
    @ColumnInfo(name = "scroll") val scroll: Float = 0f,
    @ColumnInfo(name = "percent") val percent: Float = 0f,
    @ColumnInfo(name = "chapters_total") val chaptersTotal: Int = 0,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long = 0,
)
