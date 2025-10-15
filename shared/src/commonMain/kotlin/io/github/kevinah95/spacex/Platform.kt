package io.github.kevinah95.spacex

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform