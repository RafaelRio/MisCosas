package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "item_history_events",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["recorded_by_user_id"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(
            value = [
                "item_id",
                "occurred_at_epoch_seconds",
                "occurred_at_nanoseconds",
                "id",
            ],
        ),
        Index(value = ["recorded_by_user_id"]),
    ],
)
internal data class ItemHistoryEventEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "item_id") val itemId: String,
    @ColumnInfo(name = "type_code") val typeCode: String,
    @ColumnInfo(name = "occurred_at_epoch_seconds") val occurredAtEpochSeconds: Long,
    @ColumnInfo(name = "occurred_at_nanoseconds") val occurredAtNanoseconds: Int,
    @ColumnInfo(name = "recorded_by_user_id") val recordedByUserId: String,
    @ColumnInfo(name = "previous_status_code") val previousStatusCode: String?,
    @ColumnInfo(name = "new_status_code") val newStatusCode: String?,
    val details: String?,
)
