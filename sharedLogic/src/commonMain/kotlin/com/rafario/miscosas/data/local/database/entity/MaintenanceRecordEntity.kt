package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "maintenance_records",
    foreignKeys = [
        ForeignKey(
            entity = MaintenanceTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["task_id", "completed_on_epoch_day"]),
    ],
)
internal data class MaintenanceRecordEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "completed_on_epoch_day") val completedOnEpochDay: Long,
    val notes: String?,
    @ColumnInfo(name = "created_at_epoch_seconds") val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds") val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds") val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds") val updatedAtNanoseconds: Int,
)
