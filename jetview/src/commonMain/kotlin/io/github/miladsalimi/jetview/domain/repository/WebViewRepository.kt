package io.github.miladsalimi.jetview.domain.repository

import io.github.miladsalimi.jetview.domain.model.WebContent
import io.github.miladsalimi.jetview.domain.model.WebViewNavigator
import io.github.miladsalimi.jetview.domain.model.WebViewState

interface WebViewRepository {
    fun loadContent(content: WebContent)
    fun goBack()
    fun goForward()
    fun reload()
    fun stopLoading()
    fun evaluateJavaScript(script: String, callback: ((String?) -> Unit)? = null)
}
