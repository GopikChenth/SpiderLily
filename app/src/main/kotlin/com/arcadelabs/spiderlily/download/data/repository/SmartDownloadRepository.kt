package com.arcadelabs.spiderlily.download.data.repository

import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.download.data.entity.SmartDownloadEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartDownloadRepository @Inject constructor(
    private val db: MangaDatabase,
) {
    private val dao = db.getSmartDownloadDao()

    suspend fun get(mangaId: Long): SmartDownloadEntity? {
        return dao.get(mangaId)
    }

    suspend fun updateCurrentIndex(mangaId: Long, currentIndex: Int) {
        val entity = dao.get(mangaId)
        if (entity != null) {
            dao.update(entity.copy(currentIndex = currentIndex))
        } else {
            // If it doesn't exist, we might need to initialize it.
            // But usually it should be initialized when smart download starts for a manga.
            dao.insert(SmartDownloadEntity(mangaId, intArrayOf(), currentIndex))
        }
    }

    suspend fun updateDownloadedIndices(mangaId: Long, indices: IntArray) {
        val entity = dao.get(mangaId)
        if (entity != null) {
            dao.update(entity.copy(downloadedIndices = indices))
        } else {
            dao.insert(SmartDownloadEntity(mangaId, indices, -1))
        }
    }

    suspend fun delete(mangaId: Long) {
        dao.delete(mangaId)
    }
}
