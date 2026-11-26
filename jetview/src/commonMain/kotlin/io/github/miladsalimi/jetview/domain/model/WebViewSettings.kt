package io.github.miladsalimi.jetview.domain.model

data class WebViewSettings(
    val javaScriptEnabled: Boolean = true,
    val domStorageEnabled: Boolean = true,
    val loadsImagesAutomatically: Boolean = true,
    val mediaPlaybackRequiresUserGesture: Boolean = false,
    val supportZoom: Boolean = true,
    val userAgentString: String? = null
)
