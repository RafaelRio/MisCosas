package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "household_members",
    primaryKeys = [
        "household_id",
        "user_id",
    ],
    foreignKeys = [
        ForeignKey(
            entity = HouseholdEntity::class,
            parentColumns = ["id"],
            childColumns = ["household_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["user_id"]),
    ],
)
internal data class HouseholdMemberEntity(
    @ColumnInfo(name = "household_id")
    val householdId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "role_code")
    val roleCode: String,
    @ColumnInfo(name = "joined_at_epoch_seconds")
    val joinedAtEpochSeconds: Long,
    @ColumnInfo(name = "joined_at_nanoseconds")
    val joinedAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds")
    val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds")
    val updatedAtNanoseconds: Int,
)