package com.arcadelabs.spiderlily.mihon.parsers.model

/**
 * Simplified ContentSource interface for SpiderLily.
 * This is a minimal version - the full implementation from the parsers layer
 * can be restored when the content parser system is integrated.
 */
interface ContentSource {
    val name: String
    val locale: String
    val contentType: ContentType
}

/**
 * Factory function to create an anonymous ContentSource by name.
 */
fun contentSource(name: String): ContentSource = object : ContentSource {
    override val name: String = name
    override val locale: String = ""
    override val contentType: ContentType = ContentType.MANGA
}
