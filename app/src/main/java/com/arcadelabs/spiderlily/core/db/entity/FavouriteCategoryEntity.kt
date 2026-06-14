package com.arcadelabs.spiderlily.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arcadelabs.spiderlily.core.db.TABLE_FAVOURITE_CATEGORIES

@Entity(tableName = TABLE_FAVOURITE_CATEGORIES)
data class FavouriteCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id") val categoryId: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "sort_key") val sortKey: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = 0,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long = 0,
)
