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
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

class ItemEntityMapperTest {

    @Test
    fun mapsItemToEntityPreservingFullPurchaseAndNanoseconds() {
        val item = Item(
            id = ItemId( "550e8400-e30b-41d4-a716-446655440000"),
            householdId = HouseholdId("550e8400-e29b-41d4-a716-446655440000"),
            name = "Producto",
            categoryId = CategoryId("custom:photography"),
            brand = "Marca",
            model = "Modelo",
            serialNumber = "Número de serie",
            isFavorite = true,
            isArchived = true,
            createdAt = Instant.parse("2026-08-21T09:00:00.000000500Z"),
            updatedAt = Instant.parse("2026-08-21T09:00:00.000000800Z"),
            purchase = Purchase(
                date = LocalDate(2026, 9, 15),
                price = Money(
                    minorUnits = 1000,
                    currency = CurrencyCode(
                        value = "EUR"
                    )
                ),
                seller = "Tienda"
            ),
            status = ItemStatus.SOLD
        )

        val entity = item.toEntity()

        assertEquals(item.id.value, entity.id)
        assertEquals(item.householdId.value, entity.householdId)
        assertEquals(item.name, entity.name)
        assertEquals(item.categoryId.value, entity.categoryId)
        assertEquals(item.brand, entity.brand)
        assertEquals(item.model, entity.model)
        assertEquals(item.serialNumber, entity.serialNumber)
        assertEquals(item.isFavorite, entity.isFavorite)
        assertEquals(item.isArchived, entity.isArchived)
        assertEquals(item.createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(500, entity.createdAtNanoseconds)
        assertEquals(item.updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(800, entity.updatedAtNanoseconds)
        assertEquals(item.purchase?.date?.toEpochDays(), entity.purchaseDateEpochDay)
        assertEquals(item.purchase?.price?.minorUnits, entity.purchasePriceMinorUnits)
        assertEquals(item.purchase?.price?.currency?.value, entity.purchaseCurrencyCode)
        assertEquals(item.purchase?.seller, entity.purchaseSeller)
        assertEquals(item.status.code, entity.statusCode)
    }

    @Test
    fun mapsItemEntityToDomainPreservingFullPurchaseAndNanoseconds() {
        val itemEntity = createItemEntity(
            purchaseDateEpochDay = 1_000,
            purchasePriceMinorUnits = 1_000,
            purchaseCurrencyCode = "EUR",
            purchaseSeller = "Tienda",
        )

        val item = itemEntity.toDomain()

        assertEquals(itemEntity.id, item.id.value)
        assertEquals(itemEntity.householdId, item.householdId.value)
        assertEquals(itemEntity.name, item.name)
        assertEquals(itemEntity.categoryId, item.categoryId.value)
        assertEquals(itemEntity.brand, item.brand)
        assertEquals(itemEntity.model, item.model)
        assertEquals(itemEntity.serialNumber, item.serialNumber)
        assertEquals(itemEntity.isFavorite, item.isFavorite)
        assertEquals(itemEntity.isArchived, item.isArchived)
        assertEquals(itemEntity.createdAtEpochSeconds, item.createdAt.epochSeconds)
        assertEquals(itemEntity.createdAtNanoseconds, item.createdAt.nanosecondsOfSecond)
        assertEquals(itemEntity.updatedAtEpochSeconds, item.updatedAt.epochSeconds)
        assertEquals(itemEntity.updatedAtNanoseconds, item.updatedAt.nanosecondsOfSecond)
        assertEquals(itemEntity.purchaseDateEpochDay, item.purchase?.date?.toEpochDays())
        assertEquals(itemEntity.purchasePriceMinorUnits, item.purchase?.price?.minorUnits)
        assertEquals(itemEntity.purchaseCurrencyCode, item.purchase?.price?.currency?.value)
        assertEquals(itemEntity.purchaseSeller, item.purchase?.seller)
        assertEquals(itemEntity.statusCode, item.status.code)
    }

    @Test
    fun mapsNullPurchaseWhenAllPurchaseColumnsAreNull() {
        val itemEntity = createItemEntity()

        val item = itemEntity.toDomain()

        assertNull(item.purchase)
    }

    @Test
    fun mapsPurchaseContainingOnlySeller() {
        val itemEntity = createItemEntity(
            purchaseSeller = "PcComponentes",
        )

        val item = itemEntity.toDomain()

        val purchase = assertNotNull(item.purchase)

        assertNull(purchase.date)
        assertNull(purchase.price)
        assertEquals("PcComponentes", purchase.seller)
    }

    @Test
    fun rejectsIncompleteStoredMoneyPair() {
        val itemEntity = createItemEntity(
            purchasePriceMinorUnits = 1_000,
            purchaseSeller = "PcComponentes",
        )

        assertFailsWith<IllegalStateException> {
            itemEntity.toDomain()
        }
        val currencyWithoutMinorUnits = itemEntity.copy(
            purchasePriceMinorUnits = null,
            purchaseCurrencyCode = "EUR",
        )
        assertFailsWith<IllegalStateException> {
            currencyWithoutMinorUnits.toDomain()
        }
    }

    @Test
    fun rejectsUnknownStoredItemStatusCode() {
        val itemEntity = createItemEntity(
            statusCode = "unknown-status",
        )

        assertFailsWith<IllegalStateException> {
            itemEntity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val itemEntity = createItemEntity()
        val copy1 = itemEntity.copy(
            createdAtNanoseconds = -1,
        )
        assertFailsWith<IllegalStateException> {
            copy1.toDomain()
        }
        val copy2 = itemEntity.copy(
            updatedAtNanoseconds = 1_000_000_000,
        )
        assertFailsWith<IllegalStateException> {
            copy2.toDomain()
        }
    }

    @Test
    fun rejectsStoredPurchaseDateOutsideValidRange() {
        val itemEntity = createItemEntity(
            purchaseDateEpochDay = Long.MAX_VALUE
        )

        assertFailsWith<IllegalStateException> {
            itemEntity.toDomain()
        }
    }

    private fun createItemEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        householdId: String = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
        name: String = "Producto",
        categoryId: String = "custom:photography",
        brand: String? = "Marca",
        model: String? = "Modelo",
        serialNumber: String? = "Número de serie",
        purchaseDateEpochDay: Long? = null,
        purchasePriceMinorUnits: Long? = null,
        purchaseCurrencyCode: String? = null,
        purchaseSeller: String? = null,
        statusCode: String = ItemStatus.SOLD.code,
        isFavorite: Boolean = true,
        isArchived: Boolean = true,
        createdAtEpochSeconds: Long = 1_000,
        createdAtNanoseconds: Int = 500,
        updatedAtEpochSeconds: Long = 1_000,
        updatedAtNanoseconds: Int = 800,
    ): ItemEntity {
        return ItemEntity(
            id = id,
            householdId = householdId,
            name = name,
            categoryId = categoryId,
            brand = brand,
            model = model,
            serialNumber = serialNumber,
            purchaseDateEpochDay = purchaseDateEpochDay,
            purchasePriceMinorUnits = purchasePriceMinorUnits,
            purchaseCurrencyCode = purchaseCurrencyCode,
            purchaseSeller = purchaseSeller,
            statusCode = statusCode,
            isFavorite = isFavorite,
            isArchived = isArchived,
            createdAtEpochSeconds = createdAtEpochSeconds,
            createdAtNanoseconds = createdAtNanoseconds,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
            updatedAtNanoseconds = updatedAtNanoseconds,
        )
    }
}
