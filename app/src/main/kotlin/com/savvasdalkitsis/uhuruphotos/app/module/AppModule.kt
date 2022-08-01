/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.app.module

import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shreyaspatil.permissionFlow.PermissionFlow

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun preferences(@ApplicationContext context: Context): FlowSharedPreferences =
        FlowSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context))

    @Provides
    fun clipboardManager(@ApplicationContext context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun notificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    @Provides
    fun contentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    fun permissionFlow(): PermissionFlow = PermissionFlow.getInstance()
}