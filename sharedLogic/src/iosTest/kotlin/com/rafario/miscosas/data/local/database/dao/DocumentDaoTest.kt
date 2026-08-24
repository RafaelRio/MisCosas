package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DocumentDaoTest {

    @Test
    fun upsertsAndFindsDocumentById() = runTest {
        val database = createTestDatabase()
        try {
            val userEntity = UserEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                displayName = "John Doe",
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500
            )

            val householdEntity = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500
            )

            val itemEntity = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440002",
                householdId = householdEntity.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val documentEntity = DocumentEntity(
                id = "550e8400-e29b-41d4-a716-446655440004",
                itemId = itemEntity.id,
                typeCode = "invoice",
                fileName = "invoice.pdf",
                mimeType = "application/pdf",
                sizeBytes = 102,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(userEntity)
            database.householdDao().upsert(householdEntity)
            database.itemDao().upsert(itemEntity)
            database.documentDao().upsert(documentEntity)

            val storedDocument = database.documentDao().findById(documentEntity.id)

            assertEquals(documentEntity, storedDocument)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingDocument() = runTest {
        val database = createTestDatabase()
        try {
            val userEntity = UserEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                displayName = "John Doe",
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500
            )

            val householdEntity = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500
            )

            val itemEntity = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440002",
                householdId = householdEntity.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val documentEntity = DocumentEntity(
                id = "550e8400-e29b-41d4-a716-446655440004",
                itemId = itemEntity.id,
                typeCode = "invoice",
                fileName = "invoice.pdf",
                mimeType = "application/pdf",
                sizeBytes = 102,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )
            val updatedDocument = documentEntity.copy(
                fileName = "invoice-updated.pdf",
                sizeBytes = 2_048L,
                updatedAtNanoseconds = 600,
            )
            database.userDao().upsert(userEntity)
            database.householdDao().upsert(householdEntity)
            database.itemDao().upsert(itemEntity)
            database.documentDao().upsert(documentEntity)
            database.documentDao().upsert(updatedDocument)

            val storedDocument = database.documentDao().findById(documentEntity.id)

            assertEquals(updatedDocument, storedDocument)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenDocumentDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedDocument =
                database.documentDao().findById("550e8400-e29b-41d4-a716-446655440004")

            assertNull(storedDocument)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsDocumentWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val document = DocumentEntity(
                id = "550e8400-e29b-41d4-a716-446655440004",
                itemId = "missing-item",
                typeCode = "invoice",
                fileName = "invoice.pdf",
                mimeType = "application/pdf",
                sizeBytes = 102L,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            assertFailsWith<SQLiteException> {
                database.documentDao().upsert(document)
            }

            assertNull(database.documentDao().findById(document.id))
        } finally {
            database.close()
        }
    }
}
