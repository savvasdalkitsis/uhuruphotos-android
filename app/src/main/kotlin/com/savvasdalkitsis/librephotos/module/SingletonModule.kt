package com.savvasdalkitsis.librephotos.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.savvasdalkitsis.librephotos.db.LibrePhotosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SingletonModule {

    @Provides
    fun authDao(db: LibrePhotosDatabase) = db.authDao()
    @Provides
    fun serverDao(db: LibrePhotosDatabase) = db.serverDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): LibrePhotosDatabase {
        return Room.databaseBuilder(
            appContext,
            LibrePhotosDatabase::class.java,
            "LibrePhotos"
        ).build()
    }

    private val Context.dataStore by preferencesDataStore(name = "settings")

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        appContext.dataStore
}