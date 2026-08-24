package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "warranties",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id", "item_id"],
            childColumns = ["document_id", "item_id"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["item_id"]),
        Index(value = ["document_id", "item_id"]),
    ],
)
internal data class WarrantyEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "item_id")
    val itemId: String,
    @ColumnInfo(name = "document_id")
    val documentId: String?,
    @ColumnInfo(name = "start_date_epoch_day")
    val startDateEpochDay: Long?,
    @ColumnInfo(name = "end_date_epoch_day")
    val endDateEpochDay: Long?,
    @ColumnInfo(name = "type_code")
    val typeCode: String?,
    @ColumnInfo(name = "provider_name")
    val providerName: String?,
    val notes: String?,
    @ColumnInfo(name = "reminder_days_before_end")
    val reminderDaysBeforeEnd: Int?,
    @ColumnInfo(name = "created_at_epoch_seconds")
    val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds")
    val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds")
    val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds")
    val updatedAtNanoseconds: Int,
)
