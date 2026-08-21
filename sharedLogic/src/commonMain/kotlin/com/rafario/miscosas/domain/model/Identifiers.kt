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

data class WarrantyId(
    val value: String,
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "WarrantyId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): WarrantyId {
            return WarrantyId(value = Uuid.random().toString())
        }
    }
}

data class DocumentId(
    val value: String,
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "DocumentId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): DocumentId {
            return DocumentId(value = Uuid.random().toString())
        }
    }
}

data class MaintenanceTaskId(
    val value: String,
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "MaintenanceTaskId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): MaintenanceTaskId {
            return MaintenanceTaskId(value = Uuid.random().toString())
        }
    }
}

data class MaintenanceRecordId(
    val value: String,
) {
    init {
        requireCanonicalNonNilUuid(
            value = value,
            identifierName = "MaintenanceRecordId",
        )
    }

    override fun toString(): String = value

    companion object {
        fun generate(): MaintenanceRecordId {
            return MaintenanceRecordId(value = Uuid.random().toString())
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
