package com.arcadelabs.spiderlily.mihon.compat

import eu.kanade.tachiyomi.network.NetworkHelper
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MihonNetworkHelper(
    baseClient: OkHttpClient,
    val cookieJar: CookieJar,
) : NetworkHelper() {

    override val client: OkHttpClient = run {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(baseClient.connectTimeoutMillis.toLong(), TimeUnit.MILLISECONDS)
        builder.readTimeout(baseClient.readTimeoutMillis.toLong(), TimeUnit.MILLISECONDS)
        builder.writeTimeout(baseClient.writeTimeoutMillis.toLong(), TimeUnit.MILLISECONDS)
        builder.cookieJar(baseClient.cookieJar)
        builder.dns(baseClient.dns)
        builder.followRedirects(baseClient.followRedirects)
        builder.followSslRedirects(baseClient.followSslRedirects)
        builder.retryOnConnectionFailure(baseClient.retryOnConnectionFailure)
        builder.build()
    }

    override fun defaultUserAgentProvider(): String {
        return "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    }
}
