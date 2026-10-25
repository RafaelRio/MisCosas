package com.rafario.miscosas.data.local.database

import kotlin.test.Test
import kotlin.test.assertEquals

class UserDatabaseProviderTest {

    @Test
    fun providerDoesNotOpenDatabaseOnCreation() {
        var openCalls = 0

        val provider = UserDatabaseProvider(
            openDatabase = { _ ->
                openCalls++
                error("No debería abrirse al crear el proveedor")
            }
        )

        assertEquals(0, openCalls)
    }
}