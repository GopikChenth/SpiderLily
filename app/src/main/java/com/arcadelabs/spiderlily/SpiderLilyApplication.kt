package com.arcadelabs.spiderlily

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * SpiderLily Application class.
 *
 * Annotated with @HiltAndroidApp to trigger Hilt code generation.
 * All dependency injection is handled by the AppModule.
 * The Mihon Injekt bridge is initialized lazily by HiltExtensionManager
 * when extensions are first loaded.
 */
@HiltAndroidApp
class SpiderLilyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "SpiderLily initializing (Hilt)...")
    }

    companion object {
        private const val TAG = "SpiderLilyApp"

        @Volatile
        private var instance: SpiderLilyApplication? = null

        fun getInstance(): SpiderLilyApplication {
            return instance ?: throw IllegalStateException(
                "SpiderLilyApplication has not been initialized yet"
            )
        }
    }
}
