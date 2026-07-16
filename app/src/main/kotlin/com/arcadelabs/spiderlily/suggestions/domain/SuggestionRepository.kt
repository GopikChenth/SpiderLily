package com.arcadelabs.spiderlily.suggestions.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.db.entity.toEntities
import com.arcadelabs.spiderlily.core.db.entity.toEntity
import com.arcadelabs.spiderlily.core.db.entity.toManga
import com.arcadelabs.spiderlily.core.db.entity.toMangaTagsList
import com.arcadelabs.spiderlily.core.model.toMangaSources
import com.arcadelabs.spiderlily.core.util.ext.mapItems
import com.arcadelabs.spiderlily.list.domain.ListFilterOption
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.model.MangaSource
import com.arcadelabs.spiderlily_parser.model.MangaTag
import com.arcadelabs.spiderlily.suggestions.data.SuggestionEntity
import com.arcadelabs.spiderlily.suggestions.data.SuggestionWithManga
import javax.inject.Inject

class SuggestionRepository @Inject constructor(
	private val db: MangaDatabase,
) {

	fun observeAll(): Flow<List<Manga>> {
		return db.getSuggestionDao().observeAll().mapItems {
			it.toManga()
		}
	}

	fun observeAll(limit: Int, filterOptions: Set<ListFilterOption>): Flow<List<Manga>> {
		return db.getSuggestionDao().observeAll(limit, filterOptions).mapItems {
			it.toManga()
		}
	}

	suspend fun getRandomList(limit: Int): List<Manga> {
		return db.getSuggestionDao().getRandom(limit).map {
			it.toManga()
		}
	}

	suspend fun clear() {
		db.getSuggestionDao().deleteAll()
	}

	suspend fun isEmpty(): Boolean {
		return db.getSuggestionDao().count() == 0
	}

	suspend fun getTopTags(limit: Int): List<MangaTag> {
		return db.getSuggestionDao().getTopTags(limit)
			.toMangaTagsList()
	}

	suspend fun getTopSources(limit: Int): List<MangaSource> {
		return db.getSuggestionDao().getTopSources(limit)
			.toMangaSources()
	}

	suspend fun replace(suggestions: Iterable<MangaSuggestion>) {
		db.withTransaction {
			db.getSuggestionDao().deleteAll()
			suggestions.forEach { (manga, relevance) ->
				val tags = manga.tags.toEntities()
				db.getTagsDao().upsert(tags)
				db.getMangaDao().upsert(manga.toEntity(), tags)
				db.getSuggestionDao().upsert(
					SuggestionEntity(
						mangaId = manga.id,
						relevance = relevance,
						createdAt = System.currentTimeMillis(),
					),
				)
			}
		}
	}

	private fun SuggestionWithManga.toManga() = manga.toManga(emptySet(), null)
}
