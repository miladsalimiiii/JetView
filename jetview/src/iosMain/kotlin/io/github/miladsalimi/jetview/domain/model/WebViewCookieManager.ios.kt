package io.github.miladsalimi.jetview.domain.model

import platform.Foundation.NSHTTPCookie
import platform.Foundation.NSHTTPCookieStorage
import platform.Foundation.NSURL

actual class WebViewCookieManager actual constructor() {
    private val cookieStorage = NSHTTPCookieStorage.sharedHTTPCookieStorage()

    actual fun setCookie(url: String, cookieValue: String) {
        // iOS cookie setting
    }

    actual fun getCookie(url: String): String? {
        val nsUrl = NSURL.URLWithString(url) ?: return null
        @Suppress("UNCHECKED_CAST")
        val cookies = cookieStorage.cookiesForURL(nsUrl) as? List<NSHTTPCookie> ?: return null
        return cookies.joinToString("; ") { cookie ->
            "${cookie.name}=${cookie.value}"
        }
    }

    actual fun removeAllCookies(callback: ((Boolean) -> Unit)?) {
        @Suppress("UNCHECKED_CAST")
        val cookies = cookieStorage.cookies as? List<NSHTTPCookie> ?: emptyList()
        for (cookie in cookies) {
            cookieStorage.deleteCookie(cookie)
        }
        callback?.invoke(true)
    }
}
