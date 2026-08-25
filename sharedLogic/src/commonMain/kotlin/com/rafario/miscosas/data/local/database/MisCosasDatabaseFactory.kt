package com.rafario.miscosas.data.local.database

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal const val MISCOSAS_DATABASE_FILE_NAME = "miscosas.db"

internal fun buildMisCosasDatabase(
    builder: RoomDatabase.Builder<MisCosasDatabase>,
): MisCosasDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}