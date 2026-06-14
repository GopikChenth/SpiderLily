package com.arcadelabs.spiderlily.di

import android.content.Context
import com.arcadelabs.spiderlily.core.db.SpiderLilyDatabase
import com.arcadelabs.spiderlily.core.db.dao.ChaptersDao
import com.arcadelabs.spiderlily.core.db.dao.FavouritesDao
import com.arcadelabs.spiderlily.core.db.dao.HistoryDao
import com.arcadelabs.spiderlily.core.db.dao.MangaDao
import com.arcadelabs.spiderlily.mihon.MihonExtensionLoader
import com.arcadelabs.spiderlily.mihon.MihonExtensionManager
import com.arcadelabs.spiderlily.mihon.compat.MihonInjektBridge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpiderLilyDatabase {
        return SpiderLilyDatabase.getInstance(context)
    }

    @Provides
    fun provideMangaDao(database: SpiderLilyDatabase): MangaDao = database.getMangaDao()

    @Provides
    fun provideChaptersDao(database: SpiderLilyDatabase): ChaptersDao = database.getChaptersDao()

    @Provides
    fun provideHistoryDao(database: SpiderLilyDatabase): HistoryDao = database.getHistoryDao()

    @Provides
    fun provideFavouritesDao(database: SpiderLilyDatabase): FavouritesDao = database.getFavouritesDao()

    @Provides
    @Singleton
    fun provideMihonInjektBridge(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): MihonInjektBridge {
        return MihonInjektBridge(
            context = context,
            httpClient = okHttpClient,
            cookieJar = CookieJar.NO_COOKIES,
        )
    }

    @Provides
    @Singleton
    fun provideMihonExtensionLoader(
        @ApplicationContext context: Context,
        injektBridge: MihonInjektBridge,
    ): MihonExtensionLoader {
        return MihonExtensionLoader(
            applicationContext = context,
            injektBridge = injektBridge,
        )
    }

    @Provides
    @Singleton
    fun provideMihonExtensionManager(
        @ApplicationContext context: Context,
        loader: MihonExtensionLoader,
    ): MihonExtensionManager {
        return MihonExtensionManager(
            context = context,
            loader = loader,
        )
    }
}
