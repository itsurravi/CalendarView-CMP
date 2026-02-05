package com.ravikantsharma.cmp

class Greeting {
    private val platform = getPlatform()

    fun platformName(): String {
        return platform.name
    }

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}