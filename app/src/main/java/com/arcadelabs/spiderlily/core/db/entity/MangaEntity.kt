package com.arcadelabs.spiderlily.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arcadelabs.spiderlily.core.db.TABLE_MANGA

@Entity(tableName = TABLE_MANGA)
data class MangaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "manga_id") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "public_url") val publicUrl: String = "",
    @ColumnInfo(name = "cover_url") val coverUrl: String = "",
    @ColumnInfo(name = "large_cover_url") val largeCoverUrl: String? = null,
    @ColumnInfo(name = "author") val author: String? = null,
    @ColumnInfo(name = "artist") val artist: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "genre") val genre: String? = null,
    @ColumnInfo(name = "status") val status: Int = 0,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "nsfw") val isNsfw: Boolean = false,
    @ColumnInfo(name = "initialized") val initialized: Boolean = false,
)
