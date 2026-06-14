package com.arcadelabs.spiderlily.mihon.compat

import android.app.Application
import android.content.Context
import android.util.Log
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory

class MihonInjektBridge(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val cookieJar: CookieJar,
) {
    private val application: Application
        get() = context.applicationContext as Application

    @Volatile
    private var initialized = false

    @Synchronized
    fun initialize() {
        if (initialized) return
        try {
            val networkHelper = MihonNetworkHelper(httpClient, cookieJar)
            Injekt.importModule(object : InjektModule {
                override fun InjektRegistrar.registerInjectables() {
                    addSingleton(application)
                    addSingletonFactory<Context> { context.applicationContext }
                    addSingletonFactory<NetworkHelper> { networkHelper }
                    addSingletonFactory<OkHttpClient> { httpClient }
                    addSingletonFactory<CookieJar> { cookieJar }
                    val json = Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                    addSingletonFactory<Json> { json }
                    addSingletonFactory<StringFormat> { json }
                    addSingletonFactory<SerialFormat> { json }
                }
            })
            initialized = true
            Log.d("MihonInjektBridge", "Injekt initialized with App dependencies")
        } catch (e: Throwable) {
            Log.e("MihonInjektBridge", "CRITICAL: Failed to initialize Injekt bridge", e)
        }
    }

    fun isInitialized(): Boolean = initialized
}
