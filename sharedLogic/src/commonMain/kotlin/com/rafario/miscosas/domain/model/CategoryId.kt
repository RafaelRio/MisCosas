package com.rafario.miscosas.domain.model

data class CategoryId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            "CategoryId must not be blank"
        }

        require(value == value.trim()) {
            "CategoryId must not contain leading or trailing whitespace"
        }
    }
}
