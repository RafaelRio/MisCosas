package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.MaintenanceRecordEntity
import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MaintenanceRecordDaoTest {

    @Test
    fun upsertsAndFindsMaintenanceRecordById() = runTest {
        val database = createTestDatabase()

        try {
            val task = insertTaskParent(database)
            val maintenanceRecordEntity = createMaintenanceRecordEntity(task.id)

            database.maintenanceRecordDao().upsert(maintenanceRecordEntity)

            val storedMaintenanceRecord = database.maintenanceRecordDao()
                .findById(maintenanceRecordEntity.id)

            assertEquals(maintenanceRecordEntity, storedMaintenanceRecord)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingMaintenanceRecord() = runTest {
        val database = createTestDatabase()

        try {
            val task = insertTaskParent(database)
            val originalRecord = createMaintenanceRecordEntity(task.id)
            val updatedRecord = originalRecord.copy(
                completedOnEpochDay = 20_430L,
                notes = "Filtros limpiados y revisados",
                updatedAtNanoseconds = 600,
            )

            database.maintenanceRecordDao().upsert(originalRecord)
            database.maintenanceRecordDao().upsert(updatedRecord)

            val storedRecord = database.maintenanceRecordDao()
                .findById(originalRecord.id)

            assertEquals(updatedRecord, storedRecord)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenMaintenanceRecordDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            assertNull(
                database.maintenanceRecordDao()
                    .findById("missing-record"),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun findsRecordsByTaskIdOrderedByMostRecentCompletion() = runTest {
        val database = createTestDatabase()

        try {
            val mainTask = insertTaskParent(database)
            val otherTask = mainTask.copy(
                id = "550e8400-e29b-41d4-a716-446655440021",
                name = "Revisión anual",
            )
            val olderRecord = createMaintenanceRecordEntity(
                taskId = mainTask.id,
                id = "550e8400-e29b-41d4-a716-446655440039",
                completedOnEpochDay = 20_400L,
            )
            val newerRecord = createMaintenanceRecordEntity(
                taskId = mainTask.id,
                id = "550e8400-e29b-41d4-a716-446655440030",
                completedOnEpochDay = 20_430L,
            )
            val otherTaskRecord = createMaintenanceRecordEntity(
                taskId = otherTask.id,
                id = "550e8400-e29b-41d4-a716-446655440032",
                completedOnEpochDay = 20_500L,
            )

            database.maintenanceTaskDao().upsert(otherTask)
            database.maintenanceRecordDao().upsert(olderRecord)
            database.maintenanceRecordDao().upsert(newerRecord)
            database.maintenanceRecordDao().upsert(otherTaskRecord)

            val storedRecords = database.maintenanceRecordDao()
                .findAllByTaskId(mainTask.id)

            assertEquals(
                listOf(newerRecord, olderRecord),
                storedRecords,
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun findsLatestRecordByTaskId() = runTest {
        val database = createTestDatabase()

        try {
            val mainTask = insertTaskParent(database)
            val otherTask = mainTask.copy(
                id = "550e8400-e29b-41d4-a716-446655440021",
                name = "Revisión anual",
            )
            val olderRecord = createMaintenanceRecordEntity(
                taskId = mainTask.id,
                id = "550e8400-e29b-41d4-a716-446655440039",
                completedOnEpochDay = 20_400L,
            )
            val newerRecord = createMaintenanceRecordEntity(
                taskId = mainTask.id,
                id = "550e8400-e29b-41d4-a716-446655440030",
                completedOnEpochDay = 20_430L,
            )
            val otherTaskRecord = createMaintenanceRecordEntity(
                taskId = otherTask.id,
                id = "550e8400-e29b-41d4-a716-446655440032",
                completedOnEpochDay = 20_500L,
            )

            database.maintenanceTaskDao().upsert(otherTask)
            database.maintenanceRecordDao().upsert(olderRecord)
            database.maintenanceRecordDao().upsert(newerRecord)
            database.maintenanceRecordDao().upsert(otherTaskRecord)

            val latestRecord = database.maintenanceRecordDao()
                .findLatestRecordByTaskId(mainTask.id)

            assertEquals(newerRecord, latestRecord)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsEmptyListWhenTaskHasNoMaintenanceRecords() = runTest {
        val database = createTestDatabase()

        try {
            val task = insertTaskParent(database)

            val storedRecords = database.maintenanceRecordDao()
                .findAllByTaskId(task.id)

            assertEquals(emptyList(), storedRecords)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenTaskHasNoLatestMaintenanceRecord() = runTest {
        val database = createTestDatabase()

        try {
            val task = insertTaskParent(database)

            val latestRecord = database.maintenanceRecordDao()
                .findLatestRecordByTaskId(task.id)

            assertNull(latestRecord)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsMaintenanceRecordWhenTaskDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val record = createMaintenanceRecordEntity(taskId = "missing-task")

            assertFailsWith<SQLiteException> {
                database.maintenanceRecordDao().upsert(record)
            }

            assertNull(database.maintenanceRecordDao().findById(record.id))
        } finally {
            database.close()
        }
    }

    private suspend fun insertTaskParent(database: MisCosasDatabase): MaintenanceTaskEntity {
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
        val task = MaintenanceTaskEntity(
            id = "550e8400-e29b-41d4-a716-446655440020",
            itemId = item.id,
            name = "Limpiar filtros",
            details = "Retirar y limpiar los filtros interiores",
            intervalAmount = 3,
            intervalUnitCode = "month",
            firstDueDateEpochDay = 20_365L,
            reminderDaysBeforeDue = 7,
            notes = "Comprobar que estén completamente secos",
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )

        database.userDao().upsert(user)
        database.householdDao().upsert(household)
        database.itemDao().upsert(item)
        database.maintenanceTaskDao().upsert(task)

        return task
    }

    private fun createMaintenanceRecordEntity(
        taskId: String,
        id: String = "550e8400-e29b-41d4-a716-446655440030",
        completedOnEpochDay: Long = 20_400L,
        notes: String? = "Filtros limpiados",
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
    ): MaintenanceRecordEntity = MaintenanceRecordEntity(
        id = id,
        taskId = taskId,
        completedOnEpochDay = completedOnEpochDay,
        notes = notes,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
    )
}
