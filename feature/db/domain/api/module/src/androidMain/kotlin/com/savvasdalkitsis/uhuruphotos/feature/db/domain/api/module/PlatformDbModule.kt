package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

actual object PlatformDbModule {

    internal actual val driver: SqlDriver by singleInstance {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = AndroidModule.applicationContext,
            name = "uhuruPhotos.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    setPragma(db, "JOURNAL_MODE = WAL")
                    setPragma(db, "SYNCHRONOUS = NORMAL")
                }

                private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
                    db.query("PRAGMA $pragma").close()
                }
            }
        )
    }
}