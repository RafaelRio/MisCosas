package com.rafario.miscosas.data.local.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun createIosDatabaseBuilder(): RoomDatabase.Builder<MisCosasDatabase> {
    val databaseFilePath =
        "${applicationSupportDirectoryPath()}/$MISCOSAS_DATABASE_FILE_NAME"

    return Room.databaseBuilder<MisCosasDatabase>(
        name = databaseFilePath,
        factory = MisCosasDatabaseConstructor::initialize,
    )
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
