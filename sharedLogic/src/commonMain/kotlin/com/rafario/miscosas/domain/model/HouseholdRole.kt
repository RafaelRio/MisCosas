package com.rafario.miscosas.domain.model

enum class HouseholdRole(val code: String) {
    OWNER("owner"),
    MEMBER("member");

    companion object {
        fun fromCodeOrNull(value: String): HouseholdRole? {
            return entries.find { it.code == value }
        }
    }
}