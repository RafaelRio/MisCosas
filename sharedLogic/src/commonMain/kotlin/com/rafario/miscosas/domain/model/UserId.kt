package com.rafario.miscosas.domain.model

data class UserId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            "UserId must not be blank"
        }
    }

    override fun toString(): String = value
}