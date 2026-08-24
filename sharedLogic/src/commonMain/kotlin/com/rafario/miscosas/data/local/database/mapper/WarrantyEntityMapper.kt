package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.WarrantyEntity
import com.rafario.miscosas.domain.model.DocumentId
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.Warranty
import com.rafario.miscosas.domain.model.WarrantyId
import com.rafario.miscosas.domain.model.WarrantyType

internal fun Warranty.toEntity(): WarrantyEntity {
    return WarrantyEntity(
        id = id.value,
        itemId = itemId.value,
        documentId = documentId?.value,
        startDateEpochDay = startDate?.toEpochDays(),
        endDateEpochDay = endDate?.toEpochDays(),
        typeCode = type?.code,
        providerName = providerName,
        notes = notes,
        reminderDaysBeforeEnd = reminderDaysBeforeEnd,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun WarrantyEntity.toDomain(): Warranty {
    val warrantyType = typeCode?.let { code ->
        checkNotNull(WarrantyType.fromCodeOrNull(code)) {
            "WarrantyType with code $code not found"
        }
    }
    val startDate = startDateEpochDay?.let { epochDay ->
        localDateFromEpochDay(
            epochDay = epochDay,
            fieldName = "WarrantyEntity.startDate",
        )
    }
    val endDate = endDateEpochDay?.let { epochDay ->
        localDateFromEpochDay(
            epochDay = epochDay,
            fieldName = "WarrantyEntity.endDate",
        )
    }
    return Warranty(
        id = WarrantyId(id),
        itemId = ItemId(itemId),
        documentId = documentId?.let { DocumentId(it) },
        startDate = startDate,
        endDate = endDate,
        type = warrantyType,
        providerName = providerName,
        notes = notes,
        reminderDaysBeforeEnd = reminderDaysBeforeEnd,
        createdAt = instantFromEpochColumns(
            epochSeconds = createdAtEpochSeconds,
            nanosecondsOfSecond = createdAtNanoseconds,
            fieldName = "WarrantyEntity.createdAt",
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "WarrantyEntity.updatedAt",
        ),
    )
}
