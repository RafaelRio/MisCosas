package com.rafario.miscosas.data.local.database

import androidx.room3.Room

internal fun createTestDatabase(): MisCosasDatabase {
    return buildMisCosasDatabase(
        builder = Room.inMemoryDatabaseBuilder<MisCosasDatabase>(
            factory = MisCosasDatabaseConstructor::initialize,
        ),
    )
}
