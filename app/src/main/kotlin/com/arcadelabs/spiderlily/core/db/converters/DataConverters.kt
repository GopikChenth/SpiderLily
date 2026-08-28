package com.arcadelabs.spiderlily.core.db.converters

import androidx.room.TypeConverter

class DataConverters {
    @TypeConverter
    fun fromLongArray(value: LongArray?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toLongArray(value: String?): LongArray? {
        return value?.split(",")?.filter { it.isNotEmpty() }?.map { it.toLong() }?.toLongArray()
    }

    @TypeConverter
    fun fromIntArray(value: IntArray?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toIntArray(value: String?): IntArray? {
        return value?.split(",")?.filter { it.isNotEmpty() }?.map { it.toInt() }?.toIntArray()
    }
}
