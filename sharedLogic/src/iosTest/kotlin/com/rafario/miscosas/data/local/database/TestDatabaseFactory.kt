package com.rafario.miscosas.data.local.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

internal fun createTestDatabase(): MisCosasDatabase {
    return Room
        .inMemoryDatabaseBuilder<MisCosasDatabase>(
            factory = MisCosasDatabaseConstructor::initialize,
        )
        .setDriver(BundledSQLiteDriver())
        .build()
}