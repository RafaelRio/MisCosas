package com.rafario.miscosas.data.local.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.rafario.miscosas.domain.model.UserId
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256


internal fun createIosDatabaseBuilder(
    userId: UserId
): RoomDatabase.Builder<MisCosasDatabase> {
    val databaseFilePath =
        "${applicationSupportDirectoryPath()}/${createDatabaseName(userId)}"

    return Room.databaseBuilder<MisCosasDatabase>(
        name = databaseFilePath,
        factory = MisCosasDatabaseConstructor::initialize,
    )
}

private fun createDatabaseName(id: UserId): String {
    val digest = CryptographyProvider.Default
        .get(SHA256)
        .hasher()
        .hashBlocking(id.value.encodeToByteArray())

    val hexDigest = digest.joinToString(separator = "") { byte ->
        (byte.toInt() and 0xff)
            .toString(radix = 16)
            .padStart(length = 2, padChar = '0')
    }

    return "miscosas-$hexDigest.db"
}

internal fun createIosUserDatabaseProvider(): UserDatabaseProvider {
    return UserDatabaseProvider { userId ->
        buildMisCosasDatabase(
            builder = createIosDatabaseBuilder(userId),
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun applicationSupportDirectoryPath(): String {
    val directoryUrl = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )

    return requireNotNull(directoryUrl?.path) {
        "Could not locate the iOS Application Support directory"
    }
}
