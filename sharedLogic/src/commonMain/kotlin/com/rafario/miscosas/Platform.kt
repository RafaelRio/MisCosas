package com.rafario.miscosas

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform