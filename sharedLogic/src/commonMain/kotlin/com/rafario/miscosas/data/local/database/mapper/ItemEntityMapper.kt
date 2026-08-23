package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.domain.model.CategoryId
import com.rafario.miscosas.domain.model.CurrencyCode
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.Item
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.ItemStatus
import com.rafario.miscosas.domain.model.Money
import com.rafario.miscosas.domain.model.Purchase

internal fun Item.toEntity(): ItemEntity {
    return ItemEntity(
        id = id.value,
        householdId = householdId.value,
        name = name,
        categoryId = categoryId.value,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        purchaseDateEpochDay = purchase?.date?.toEpochDays(),
        purchasePriceMinorUnits = purchase?.price?.minorUnits,
        purchaseCurrencyCode = purchase?.price?.currency?.value,
        purchaseSeller = purchase?.seller,
        statusCode = status.code,
        isFavorite = isFavorite,
        isArchived = isArchived,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun ItemEntity.toDomain(): Item {
    val status = checkNotNull(ItemStatus.fromCodeOrNull(statusCode)) {
        "Invalid status code: $statusCode"
    }
    val purchaseIsAbsent =
        purchaseDateEpochDay == null &&
                purchasePriceMinorUnits == null &&
                purchaseCurrencyCode == null &&
                purchaseSeller == null
    val purchaseDate = purchaseDateEpochDay?.let { epochDay ->
        localDateFromEpochDay(
            epochDay = epochDay,
            fieldName = "ItemEntity.purchaseDate",
        )
    }

    val storedMinorUnits = purchasePriceMinorUnits
    val storedCurrencyCode = purchaseCurrencyCode

    val purchasePrice =
        when {
            storedMinorUnits == null && storedCurrencyCode == null -> {
                null
            }

            storedMinorUnits != null && storedCurrencyCode != null -> {
                Money(
                    minorUnits = storedMinorUnits,
                    currency = CurrencyCode(storedCurrencyCode),
                )
            }

            else -> {
                error("ItemEntity purchase price requires both minor units and currency code")
            }
        }

    val purchase = if (purchaseIsAbsent) {
        null
    } else {
        Purchase(
            date = purchaseDate,
            price = purchasePrice,
            seller = purchaseSeller,
        )
    }
    return Item(
        id = ItemId(id),
        householdId = HouseholdId(householdId),
        name = name,
        categoryId = CategoryId(categoryId),
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        purchase = purchase,
        status = status,
        isFavorite = isFavorite,
        isArchived = isArchived,
        createdAt = instantFromEpochColumns(
            createdAtEpochSeconds,
            createdAtNanoseconds,
            fieldName = "ItemEntity.createdAt",
        ),
        updatedAt = instantFromEpochColumns(
            updatedAtEpochSeconds,
            updatedAtNanoseconds,
            fieldName = "ItemEntity.updatedAt",
        ),
    )
}
