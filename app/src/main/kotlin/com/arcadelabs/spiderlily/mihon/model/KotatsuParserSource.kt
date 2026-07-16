package com.arcadelabs.spiderlily.mihon.model

import com.arcadelabs.spiderlily.mihon.parsers.model.ContentSource
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentType
import com.arcadelabs.spiderlily_parser.model.MangaParserSource

data class KotatsuParserSource(
    val mangaSource: MangaParserSource
) : ContentSource {
    override val name: String get() = mangaSource.name
    override val locale: String get() = mangaSource.locale
    override val contentType: ContentType get() = when (mangaSource.contentType) {
        com.arcadelabs.spiderlily_parser.model.ContentType.MANGA -> ContentType.MANGA
        com.arcadelabs.spiderlily_parser.model.ContentType.HENTAI -> ContentType.HENTAI_MANGA
        com.arcadelabs.spiderlily_parser.model.ContentType.COMICS -> ContentType.COMICS
        com.arcadelabs.spiderlily_parser.model.ContentType.OTHER -> ContentType.OTHER
        com.arcadelabs.spiderlily_parser.model.ContentType.MANHWA -> ContentType.MANHWA
        com.arcadelabs.spiderlily_parser.model.ContentType.MANHUA -> ContentType.MANHUA
        com.arcadelabs.spiderlily_parser.model.ContentType.NOVEL -> ContentType.NOVEL
        com.arcadelabs.spiderlily_parser.model.ContentType.ONE_SHOT -> ContentType.ONE_SHOT
        com.arcadelabs.spiderlily_parser.model.ContentType.DOUJINSHI -> ContentType.DOUJINSHI
        com.arcadelabs.spiderlily_parser.model.ContentType.IMAGE_SET -> ContentType.IMAGE_SET
        com.arcadelabs.spiderlily_parser.model.ContentType.ARTIST_CG -> ContentType.ARTIST_CG
        com.arcadelabs.spiderlily_parser.model.ContentType.GAME_CG -> ContentType.GAME_CG
    }
    val isBroken: Boolean get() = mangaSource.isBroken
}
