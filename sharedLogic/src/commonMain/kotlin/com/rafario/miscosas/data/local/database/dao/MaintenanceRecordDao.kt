package com.rafario.miscosas.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafario.miscosas.data.local.database.entity.MaintenanceRecordEntity

@Dao
internal interface MaintenanceRecordDao {
    @Upsert
    suspend fun upsert(maintenanceRecord: MaintenanceRecordEntity)

    @Query("SELECT * FROM maintenance_records WHERE id = :id")
    suspend fun findById(id: String): MaintenanceRecordEntity?

    @Query(
        """
        SELECT * FROM maintenance_records
        WHERE task_id = :taskId
        ORDER BY
            completed_on_epoch_day DESC,
            created_at_epoch_seconds DESC,
            created_at_nanoseconds DESC,
            id DESC
        """,
    )
    suspend fun findAllByTaskId(taskId: String): List<MaintenanceRecordEntity>

    @Query(
        """
        SELECT * FROM maintenance_records
        WHERE task_id = :taskId
        ORDER BY
            completed_on_epoch_day DESC,
            created_at_epoch_seconds DESC,
            created_at_nanoseconds DESC,
            id DESC
        LIMIT 1
        """,
    )
    suspend fun findLatestRecordByTaskId(taskId: String): MaintenanceRecordEntity?
}
