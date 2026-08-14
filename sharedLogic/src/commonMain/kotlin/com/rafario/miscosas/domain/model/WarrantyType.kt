package com.rafario.miscosas.domain.model

enum class WarrantyType(val code: String) {
    LEGAL("legal"),
    COMMERCIAL("commercial"),
    EXTENDED("extended"),
    OTHER("other");

    companion object {
        fun fromCodeOrNull(code: String): WarrantyType? {
            return entries.find { it.code == code }
        }
    }
}
