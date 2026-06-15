package com.arcadelabs.spiderlily.mihon.parsers

import com.arcadelabs.spiderlily.mihon.parsers.model.Content

interface FavoritesSyncProvider {

    suspend fun addFavorite(manga: Content): Boolean

    suspend fun removeFavorite(manga: Content): Boolean
}
