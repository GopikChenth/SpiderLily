package com.arcadelabs.spiderlily.mihon

import android.content.Context
import com.arcadelabs.spiderlily.mihon.compat.MihonInjektBridge
import okhttp3.CookieJar
import okhttp3.OkHttpClient

object MihonModule {
    fun provideMihonInjektBridge(
        context: Context,
        okHttpClient: OkHttpClient,
        cookieJar: CookieJar,
    ): MihonInjektBridge {
        return MihonInjektBridge(
            context = context,
            httpClient = okHttpClient,
            cookieJar = cookieJar,
        )
    }

    fun provideMihonExtensionLoader(
        context: Context,
        injektBridge: MihonInjektBridge,
    ): MihonExtensionLoader {
        return MihonExtensionLoader(context, injektBridge)
    }

    fun provideMihonExtensionManager(
        context: Context,
        loader: MihonExtensionLoader,
    ): MihonExtensionManager {
        return MihonExtensionManager(context, loader)
    }
}
