package com.arcadelabs.spiderlily.mihon.parsers.model

import com.arcadelabs.spiderlily_parser.model.MangaSource

interface ContentSource : MangaSource {

    override val name: String
    val locale: String
    val contentType: ContentType
}
