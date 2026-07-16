package com.arcadelabs.spiderlily.scrobbling.common.domain

import com.arcadelabs.spiderlily.scrobbling.anilist.data.AniListRepository
import com.arcadelabs.spiderlily.scrobbling.common.data.ScrobblerRepository
import com.arcadelabs.spiderlily.scrobbling.common.domain.model.ScrobblerService
import com.arcadelabs.spiderlily.scrobbling.kitsu.data.KitsuRepository
import com.arcadelabs.spiderlily.scrobbling.mal.data.MALRepository
import com.arcadelabs.spiderlily.scrobbling.shikimori.data.ShikimoriRepository
import javax.inject.Inject
import javax.inject.Provider

class ScrobblerRepositoryMap @Inject constructor(
	private val shikimoriRepository: Provider<ShikimoriRepository>,
	private val aniListRepository: Provider<AniListRepository>,
	private val malRepository: Provider<MALRepository>,
	private val kitsuRepository: Provider<KitsuRepository>,
) {

	operator fun get(scrobblerService: ScrobblerService): ScrobblerRepository = when (scrobblerService) {
		ScrobblerService.SHIKIMORI -> shikimoriRepository
		ScrobblerService.ANILIST -> aniListRepository
		ScrobblerService.MAL -> malRepository
		ScrobblerService.KITSU -> kitsuRepository
	}.get()
}
