package io.github.miladsalimi.jetviewapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform