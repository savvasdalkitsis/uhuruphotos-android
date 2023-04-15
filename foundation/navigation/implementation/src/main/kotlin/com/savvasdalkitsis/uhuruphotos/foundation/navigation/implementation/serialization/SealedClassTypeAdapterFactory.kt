package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import kotlin.jvm.internal.Reflection

internal class SealedClassTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val kClass = Reflection.getOrCreateKotlinClass(type.rawType)
        return if (kClass.sealedSubclasses.any()) {
            SealedClassTypeAdapter(kClass, gson)
        } else
            gson.getDelegateAdapter(this, type)
    }
}