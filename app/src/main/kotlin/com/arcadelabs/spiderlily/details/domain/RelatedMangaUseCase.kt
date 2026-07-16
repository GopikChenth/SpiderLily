package com.arcadelabs.spiderlily.details.domain

import com.arcadelabs.spiderlily.core.parser.MangaRepository
import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily_parser.model.Manga
import com.arcadelabs.spiderlily_parser.util.runCatchingCancellable
import javax.inject.Inject

class RelatedMangaUseCase @Inject constructor(
	private val mangaRepositoryFactory: MangaRepository.Factory,
) {

	suspend operator fun invoke(seed: Manga) = runCatchingCancellable {
		mangaRepositoryFactory.create(seed.source).getRelated(seed)
	}.onFailure {
		it.printStackTraceDebug("RelatedMangaUseCase::invoke")
	}.getOrNull()
}
