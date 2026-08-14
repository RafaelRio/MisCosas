package com.rafario.miscosas.domain.model

data class Money(
    val minorUnits: Long,
    val currency: CurrencyCode
) {
    init {
        require(minorUnits >= 0) {
            "Money cannot contain a negative amount"
        }
    }
}
