package com.ravikantsharma.cmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform