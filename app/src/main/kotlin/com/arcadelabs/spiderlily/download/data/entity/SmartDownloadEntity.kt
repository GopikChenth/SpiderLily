package com.arcadelabs.spiderlily.download.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.arcadelabs.spiderlily.core.db.TABLE_SMART_DOWNLOADS
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity

@Entity(
    tableName = TABLE_SMART_DOWNLOADS,
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["manga_id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SmartDownloadEntity(
    @PrimaryKey
    @ColumnInfo(name = "manga_id")
    val mangaId: Long,
    @ColumnInfo(name = "downloaded_indices")
    val downloadedIndices: IntArray,
    @ColumnInfo(name = "current_index")
    val currentIndex: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmartDownloadEntity

        if (mangaId != other.mangaId) return false
        if (!downloadedIndices.contentEquals(other.downloadedIndices)) return false
        if (currentIndex != other.currentIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mangaId.hashCode()
        result = 31 * result + downloadedIndices.contentHashCode()
        result = 31 * result + currentIndex
        return result
    }
}
