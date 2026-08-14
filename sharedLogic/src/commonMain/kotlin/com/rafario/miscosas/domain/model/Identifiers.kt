package com.rafario.miscosas.domain.model

import kotlin.uuid.Uuid

data class HouseholdId(
    val value: String
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "HouseholdId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): HouseholdId {
            return HouseholdId(value = Uuid.random().toString())
        }
    }
}

data class ItemId(
    val value: String,
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "ItemId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): ItemId {
            return ItemId(value = Uuid.random().toString())
        }
    }
}

private fun requireCanonicalNonNilUuid(
    value: String,
    identifierName: String,
) {
    val uuid = Uuid.parseHexDashOrNull(value)

    require(
        uuid != null &&
                uuid != Uuid.NIL &&
                uuid.toString() == value
    ) {
        "$identifierName must contain a canonical, non-NIL UUID"
    }
}