package com.arcadelabs.spiderlily.scrobbling.kitsu.domain

import com.arcadelabs.spiderlily.core.db.MangaDatabase
import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.scrobbling.common.domain.Scrobbler
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerService
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblingStatus
import com.arcadelabs.spiderlily.scrobbling.kitsu.data.KitsuRepository
import javax.inject.Inject

class KitsuScrobbler @Inject constructor(
	private val repository: KitsuRepository,
	db: MangaDatabase,
	mangaRepositoryFactory: MangaRepository.Factory,
) : Scrobbler(db, ScrobblerService.KITSU, repository, mangaRepositoryFactory) {

	init {
		statuses[ScrobblingStatus.PLANNED] = "planned"
		statuses[ScrobblingStatus.READING] = "current"
		statuses[ScrobblingStatus.COMPLETED] = "completed"
		statuses[ScrobblingStatus.ON_HOLD] = "on_hold"
		statuses[ScrobblingStatus.DROPPED] = "dropped"
	}

	override suspend fun updateScrobblingInfo(
		mangaId: Long,
		rating: Float,
		status: ScrobblingStatus?,
		comment: String?
	) {
		val entity = db.getScrobblingDao().find(scrobblerService.id, mangaId)
		requireNotNull(entity) { "Scrobbling info for manga $mangaId not found" }
		repository.updateRate(
			rateId = entity.id,
			mangaId = entity.mangaId,
			rating = rating,
			status = statuses[status],
			comment = comment,
		)
	}

}
