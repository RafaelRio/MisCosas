package com.rafario.miscosas.domain.model

enum class DocumentType(val code: String) {
    RECEIPT("receipt"),
    INVOICE("invoice"),
    WARRANTY("warranty"),
    MANUAL("manual"),
    OTHER("other");

    companion object {
        fun fromCodeOrNull(code: String): DocumentType? {
            return entries.firstOrNull { it.code == code }
        }
    }
}