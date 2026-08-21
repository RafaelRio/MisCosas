package com.rafario.miscosas.domain.model

enum class ReturnTrackingState(val code: String) {
    TRACKING("tracking"),
    RETURNED("returned"),
    KEPT("kept");

    companion object {
        fun fromCodeOrNull(code: String): ReturnTrackingState? {
            return entries.find { it.code == code }
        }
    }
}