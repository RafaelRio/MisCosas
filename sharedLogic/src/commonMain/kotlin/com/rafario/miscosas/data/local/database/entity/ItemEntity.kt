package com.rafario.miscosas.data.local.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = HouseholdEntity::class,
            parentColumns = ["id"],
            childColumns = ["household_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["household_id"]),
    ],
)
internal data class ItemEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "household_id")
    val householdId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "brand")
    val brand: String?,
    @ColumnInfo(name = "model")
    val model: String?,
    @ColumnInfo(name = "serial_number")
    val serialNumber: String?,
    @ColumnInfo(name = "purchase_date_epoch_day")
    val purchaseDateEpochDay: Long?,
    @ColumnInfo(name = "purchase_price_minor_units")
    val purchasePriceMinorUnits: Long?,
    @ColumnInfo(name = "purchase_currency_code")
    val purchaseCurrencyCode: String?,
    @ColumnInfo(name = "purchase_seller")
    val purchaseSeller: String?,
    @ColumnInfo(name = "status_code")
    val statusCode: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean,
    @ColumnInfo(name = "created_at_epoch_seconds")
    val createdAtEpochSeconds: Long,
    @ColumnInfo(name = "created_at_nanoseconds")
    val createdAtNanoseconds: Int,
    @ColumnInfo(name = "updated_at_epoch_seconds")
    val updatedAtEpochSeconds: Long,
    @ColumnInfo(name = "updated_at_nanoseconds")
    val updatedAtNanoseconds: Int,
)
