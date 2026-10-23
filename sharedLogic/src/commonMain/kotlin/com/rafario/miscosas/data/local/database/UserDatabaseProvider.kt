package com.rafario.miscosas.data.local.database

import com.rafario.miscosas.domain.model.UserId

internal class UserDatabaseProvider(
    private val openDatabase: (UserId) -> MisCosasDatabase,
) {
    private var activeUserId: UserId? = null
    private var activeDatabase: MisCosasDatabase? = null

    //PASO 1
    fun databaseFor(id: UserId): MisCosasDatabase {
        val currentDatabase = activeDatabase
        if (activeUserId == id && currentDatabase != null) {
            return currentDatabase
        } else {
            currentDatabase?.close()
        }

        activeDatabase = null
        activeUserId = null

        val result = openDatabase(id)

        activeDatabase = result
        activeUserId = id

        return result
    }

    fun closeCurrentDatabase() {
        activeDatabase?.close()
        activeUserId = null
        activeDatabase = null
    }
}