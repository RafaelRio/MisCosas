package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.local.database.entity.WarrantyEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class WarrantyDaoTest {

    @Test
    fun upsertsAndFindsWarrantyById() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val warranty = createWarrantyEntity(
                itemId = parents.item.id,
                documentId = parents.document.id,
            )

            database.warrantyDao().upsert(warranty)

            val storedWarranty = database.warrantyDao()
                .findById(warranty.id)

            assertEquals(warranty, storedWarranty)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingWarranty() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val warranty = createWarrantyEntity(
                itemId = parents.item.id,
                documentId = parents.document.id,
            )
            val updatedWarranty = warranty.copy(
                providerName = "Fabricante actualizado",
                notes = "Cobertura ampliada",
                reminderDaysBeforeEnd = 15,
                updatedAtNanoseconds = 900,
            )

            database.warrantyDao().upsert(warranty)
            database.warrantyDao().upsert(updatedWarranty)

            val storedWarranty = database.warrantyDao()
                .findById(warranty.id)

            assertEquals(updatedWarranty, storedWarranty)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenWarrantyDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedWarranty = database.warrantyDao()
                .findById("550e8400-e29b-41d4-a716-446655440030")

            assertNull(storedWarranty)
        } finally {
            database.close()
        }
    }

    @Test
    fun allowsWarrantyWithoutDocument() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(
                database = database,
                includeDocument = false,
            )
            val warranty = createWarrantyEntity(
                itemId = parents.item.id,
                documentId = null,
            )

            database.warrantyDao().upsert(warranty)

            val storedWarranty = database.warrantyDao()
                .findById(warranty.id)

            assertEquals(warranty, storedWarranty)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsWarrantyWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val warranty = createWarrantyEntity(
                itemId = "missing-item",
                documentId = null,
            )

            assertFailsWith<SQLiteException> {
                database.warrantyDao().upsert(warranty)
            }

            assertNull(database.warrantyDao().findById(warranty.id))
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsWarrantyWhenDocumentDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(
                database = database,
                includeDocument = false,
            )
            val warranty = createWarrantyEntity(
                itemId = parents.item.id,
                documentId = "missing-document",
            )

            assertFailsWith<SQLiteException> {
                database.warrantyDao().upsert(warranty)
            }

            assertNull(database.warrantyDao().findById(warranty.id))
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsWarrantyWhenDocumentBelongsToAnotherItem() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(
                database = database,
                includeDocument = true,
            )
            val anotherItem = parents.item.copy(
                id = "550e8400-e29b-41d4-a716-446655440011",
                name = "Bicicleta",
                serialNumber = "BIKE-123",
            )
            database.itemDao().upsert(anotherItem)

            val warranty = createWarrantyEntity(
                itemId = anotherItem.id,
                documentId = parents.document.id,
            )

            assertFailsWith<SQLiteException> {
                database.warrantyDao().upsert(warranty)
            }

            assertNull(database.warrantyDao().findById(warranty.id))
        } finally {
            database.close()
        }
    }

    private suspend fun insertParents(
        database: MisCosasDatabase,
        includeDocument: Boolean = true,
    ): WarrantyParents {
        val user = UserEntity(
            id = "firebase-user_A1b2C3",
            displayName = "Rafael",
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )
        val household = HouseholdEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            name = "Casa Río",
            createdBy = user.id,
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )
        val item = ItemEntity(
            id = "550e8400-e29b-41d4-a716-446655440010",
            householdId = household.id,
            name = "Televisor",
            categoryId = "builtin:image-and-sound",
            brand = "LG",
            model = "OLED",
            serialNumber = "TV-123",
            purchaseDateEpochDay = null,
            purchasePriceMinorUnits = null,
            purchaseCurrencyCode = null,
            purchaseSeller = null,
            statusCode = "active",
            isFavorite = false,
            isArchived = false,
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )
        val document = DocumentEntity(
            id = "550e8400-e29b-41d4-a716-446655440020",
            itemId = item.id,
            typeCode = "warranty",
            fileName = "garantia.pdf",
            mimeType = "application/pdf",
            sizeBytes = 2_048L,
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )

        database.userDao().upsert(user)
        database.householdDao().upsert(household)
        database.itemDao().upsert(item)
        if (includeDocument) {
            database.documentDao().upsert(document)
        }

        return WarrantyParents(
            item = item,
            document = document,
        )
    }

    private fun createWarrantyEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440030",
        itemId: String,
        documentId: String?,
        startDateEpochDay: Long? = 20_000L,
        endDateEpochDay: Long? = 20_365L,
        typeCode: String? = "extended",
        providerName: String? = "Fabricante",
        notes: String? = "Cobertura completa",
        reminderDaysBeforeEnd: Int? = 30,
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 500,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 800,
    ): WarrantyEntity {
        return WarrantyEntity(
            id = id,
            itemId = itemId,
            documentId = documentId,
            startDateEpochDay = startDateEpochDay,
            endDateEpochDay = endDateEpochDay,
            typeCode = typeCode,
            providerName = providerName,
            notes = notes,
            reminderDaysBeforeEnd = reminderDaysBeforeEnd,
            createdAtEpochSeconds = createdAtEpochSeconds,
            createdAtNanoseconds = createdAtNanoseconds,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
            updatedAtNanoseconds = updatedAtNanoseconds,
        )
    }

    private data class WarrantyParents(
        val item: ItemEntity,
        val document: DocumentEntity,
    )
}
