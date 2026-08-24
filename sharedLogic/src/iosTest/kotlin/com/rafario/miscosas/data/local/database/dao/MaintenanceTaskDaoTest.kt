package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MaintenanceTaskDaoTest {

    @Test
    fun upsertsAndFindsMaintenanceTaskById() = runTest {
        val database = createTestDatabase()

        try {
            val item = insertItemParent(database)
            val maintenanceTask = createMaintenanceTaskEntity(itemId = item.id)
            database.maintenanceTaskDao().upsert(maintenanceTask)
            val storedMaintenanceTask = database.maintenanceTaskDao().findById(maintenanceTask.id)

            assertEquals(maintenanceTask, storedMaintenanceTask)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingMaintenanceTask() = runTest {
        val database = createTestDatabase()

        try {
            val item = insertItemParent(database)
            val maintenanceTask = createMaintenanceTaskEntity(itemId = item.id)
            val updatedMaintenanceTask = maintenanceTask.copy(
                name = "Actualizar nombre",
                intervalAmount = 14,
                updatedAtNanoseconds = 2_000,
            )
            database.maintenanceTaskDao().upsert(maintenanceTask)
            database.maintenanceTaskDao().upsert(updatedMaintenanceTask)
            val storedMaintenanceTask = database.maintenanceTaskDao().findById(maintenanceTask.id)

            assertEquals(updatedMaintenanceTask, storedMaintenanceTask)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenMaintenanceTaskDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedMaintenanceTask = database.maintenanceTaskDao().findById("missing-id")

            assertNull(storedMaintenanceTask)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsMaintenanceTaskWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val maintenanceTask = createMaintenanceTaskEntity(itemId = "missing-item")
            assertFailsWith<SQLiteException> {
                database.maintenanceTaskDao().upsert(maintenanceTask)
            }
            assertNull(database.maintenanceTaskDao().findById(maintenanceTask.id))
        } finally {
            database.close()
        }
    }

    private suspend fun insertItemParent(database: MisCosasDatabase): ItemEntity {
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
            id = "550e8200-e29b-41d4-a716-446655440010",
            householdId = household.id,
            name = "Aire acondicionado",
            categoryId = "builtin:appliances",
            brand = "Daikin",
            model = "Perfera",
            serialNumber = "AC-123",
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

        database.userDao().upsert(user)
        database.householdDao().upsert(household)
        database.itemDao().upsert(item)

        return item
    }

    private fun createMaintenanceTaskEntity(
        itemId: String,
        id: String = "550e8400-e29b-41d4-a716-446655440020",
        name: String = "Limpiar filtros",
        details: String? = "Retirar y limpiar los filtros interiores",
        intervalAmount: Int = 3,
        intervalUnitCode: String = "month",
        firstDueDateEpochDay: Long = 20_365L,
        reminderDaysBeforeDue: Int? = 7,
        notes: String? = "Comprobar que estén completamente secos",
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
    ): MaintenanceTaskEntity = MaintenanceTaskEntity(
        id = id,
        itemId = itemId,
        name = name,
        details = details,
        intervalAmount = intervalAmount,
        intervalUnitCode = intervalUnitCode,
        firstDueDateEpochDay = firstDueDateEpochDay,
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        notes = notes,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
    )
}
