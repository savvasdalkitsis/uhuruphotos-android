package com.savvasdalkitsis.uhuruphotos.settings.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    private val nothing = Unit

    @Provides
    fun settingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = preferencesDataStore("settings").getValue(context, ::nothing)

}