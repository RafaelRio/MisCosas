package com.rafario.miscosas.data.local.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.rafario.miscosas.domain.model.UserId
import java.security.MessageDigest

internal fun createAndroidDatabaseBuilder(
    context: Context,
    userId: UserId
): RoomDatabase.Builder<MisCosasDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(
        returnDeterministicName(userId),
    )

    return Room.databaseBuilder<MisCosasDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
        factory = MisCosasDatabaseConstructor::initialize,
    )
}

internal fun returnDeterministicName(id: UserId): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(id.value.encodeToByteArray())
    return "miscosas-${digest.toHexString()}.db"
}

internal fun createAndroidUserDatabaseProvider(
    context: Context
): UserDatabaseProvider {
    val appContext = context.applicationContext
    return UserDatabaseProvider { userId ->
        buildMisCosasDatabase(
            builder = createAndroidDatabaseBuilder(appContext, userId),
        )
    }
}