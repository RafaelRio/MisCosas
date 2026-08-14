package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate

data class Purchase(
    val date: LocalDate?,
    val price: Money?,
    val seller: String?,
) {
    init {
        require(seller == null || seller.isNotBlank()) {
            "Purchase seller must not be blank"
        }

        require(
            date != null ||
                price != null ||
                seller != null
        ) {
            "Purchase must contain at least one piece of information"
        }
    }
}
