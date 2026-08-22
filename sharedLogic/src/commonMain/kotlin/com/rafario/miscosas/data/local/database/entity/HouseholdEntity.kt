package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "households",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["created_by"]),
    ],
)
internal data class HouseholdEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "created_by")
    val createdBy: String,
    @ColumnInfo(name = "created_at_epoch_seconds")
    val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds")
    val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds")
    val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds")
    val updatedAtNanoseconds: Int,
)