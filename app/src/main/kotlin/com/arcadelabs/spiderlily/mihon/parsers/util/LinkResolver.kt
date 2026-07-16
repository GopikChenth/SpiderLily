package com.arcadelabs.spiderlily.mihon.parsers.util

import okhttp3.HttpUrl
import com.arcadelabs.spiderlily.mihon.parsers.model.Content
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentSource

public interface LinkResolver {
    public val link: HttpUrl
    public suspend fun getSource(): ContentSource?
    public suspend fun getContent(): Content?
}

