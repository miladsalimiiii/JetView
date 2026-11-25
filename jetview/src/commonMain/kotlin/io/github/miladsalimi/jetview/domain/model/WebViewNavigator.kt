package io.github.miladsalimi.jetview.domain.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface WebViewCommand {
    data object Back : WebViewCommand
    data object Forward : WebViewCommand
    data object Reload : WebViewCommand
    data object StopLoading : WebViewCommand
    data class LoadUrl(val url: String, val headers: Map<String, String> = emptyMap()) : WebViewCommand
    data class EvaluateJavaScript(val script: String, val callback: ((String?) -> Unit)? = null) : WebViewCommand
}

class WebViewNavigator {
    private val _navigationRequests = MutableSharedFlow<WebViewCommand>(replay = 0, extraBufferCapacity = 64)
    val navigationRequests: SharedFlow<WebViewCommand> = _navigationRequests.asSharedFlow()

    var canGoBack: Boolean = false
        internal set
    var canGoForward: Boolean = false
        internal set

    fun loadUrl(url: String, headers: Map<String, String> = emptyMap()) {
        _navigationRequests.tryEmit(WebViewCommand.LoadUrl(url, headers))
    }

    fun goBack() {
        _navigationRequests.tryEmit(WebViewCommand.Back)
    }

    fun goForward() {
        _navigationRequests.tryEmit(WebViewCommand.Forward)
    }

    fun reload() {
        _navigationRequests.tryEmit(WebViewCommand.Reload)
    }

    fun stopLoading() {
        _navigationRequests.tryEmit(WebViewCommand.StopLoading)
    }

    fun evaluateJavaScript(script: String, callback: ((String?) -> Unit)? = null) {
        _navigationRequests.tryEmit(WebViewCommand.EvaluateJavaScript(script, callback))
    }
}
