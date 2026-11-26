package io.github.miladsalimi.jetview.domain.model

import android.webkit.CookieManager
import android.webkit.ValueCallback

actual class WebViewCookieManager actual constructor() {
    private val cookieManager: CookieManager = CookieManager.getInstance()

    actual fun setCookie(url: String, cookieValue: String) {
        cookieManager.setCookie(url, cookieValue)
        cookieManager.flush()
    }

    actual fun getCookie(url: String): String? {
        return cookieManager.getCookie(url)
    }

    actual fun removeAllCookies(callback: ((Boolean) -> Unit)?) {
        if (callback != null) {
            cookieManager.removeAllCookies(ValueCallback { result ->
                callback(result)
            })
        } else {
            cookieManager.removeAllCookies(null)
        }
        cookieManager.flush()
    }
}
