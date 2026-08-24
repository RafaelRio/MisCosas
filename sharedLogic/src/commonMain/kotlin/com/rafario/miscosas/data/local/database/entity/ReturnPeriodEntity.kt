package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "return_periods",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal data class ReturnPeriodEntity(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: String,
    val seller: String?,
    @ColumnInfo(name = "deadline_epoch_day")
    val deadlineEpochDay: Long,
    @ColumnInfo(name = "created_at_epoch_seconds")
    val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds")
    val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds")
    val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds")
    val updatedAtNanoseconds: Int,
    @ColumnInfo(name = "tracking_state_code")
    val trackingStateCode: String,
)