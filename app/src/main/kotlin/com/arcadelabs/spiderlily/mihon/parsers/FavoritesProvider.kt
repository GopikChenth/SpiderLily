package com.arcadelabs.spiderlily.mihon.parsers

import com.arcadelabs.spiderlily.mihon.parsers.model.Content

interface FavoritesProvider {

    suspend fun fetchFavorites(): List<Content>
}
