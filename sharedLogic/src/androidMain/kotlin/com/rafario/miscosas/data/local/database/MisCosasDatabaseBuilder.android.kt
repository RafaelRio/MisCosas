package com.rafario.miscosas.data.local.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

internal fun createAndroidDatabaseBuilder(
    context: Context,
): RoomDatabase.Builder<MisCosasDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(
        MISCOSAS_DATABASE_FILE_NAME,
    )

    return Room.databaseBuilder<MisCosasDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
        factory = MisCosasDatabaseConstructor::initialize,
    )
}