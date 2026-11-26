package io.github.miladsalimi.jetview.domain.model

expect class WebViewCookieManager() {
    fun setCookie(url: String, cookieValue: String)
    fun getCookie(url: String): String?
    fun removeAllCookies(callback: ((Boolean) -> Unit)? = null)
}
