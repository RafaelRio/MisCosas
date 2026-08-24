package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "maintenance_tasks",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["item_id"]),
    ],
)
internal data class MaintenanceTaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "item_id") val itemId: String,
    val name: String,
    val details: String?,
    @ColumnInfo(name = "interval_amount") val intervalAmount: Int,
    @ColumnInfo(name = "interval_unit_code") val intervalUnitCode: String,
    @ColumnInfo(name = "first_due_date_epoch_day") val firstDueDateEpochDay: Long,
    @ColumnInfo(name = "reminder_days_before_due") val reminderDaysBeforeDue: Int?,
    val notes: String?,
    @ColumnInfo(name = "created_at_epoch_seconds") val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds") val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds") val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds") val updatedAtNanoseconds: Int,
)