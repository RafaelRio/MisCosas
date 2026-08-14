package com.rafario.miscosas.domain.model

data class CurrencyCode(
    val value: String
) {
    init {
        require(value.length == 3 && value.all { it in 'A'..'Z' }) {
            "Currency code must contain exactly three uppercase ASCII letters"
        }
    }

    override fun toString() = value

    companion object {
        val EUR = CurrencyCode("EUR")
    }
}
