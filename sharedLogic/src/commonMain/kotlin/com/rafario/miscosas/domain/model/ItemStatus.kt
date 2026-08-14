package com.rafario.miscosas.domain.model

enum class ItemStatus(
    val code: String
) {
    ACTIVE(code = "active"),
    SOLD(code = "sold"),
    GIFTED(code = "gifted"),
    BROKEN(code = "broken"),
    LOST(code = "lost"),
    RECYCLED(code = "recycled");

    companion object {
        fun fromCodeOrNull(code: String): ItemStatus? {
            return entries.find { it.code == code }
        }
    }
}