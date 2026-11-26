package io.github.miladsalimi.jetview.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed interface LoadingState {
    data object Initializing : LoadingState
    data class Loading(val progress: Float) : LoadingState
    data object Finished : LoadingState
}

data class WebViewError(
    val errorCode: Int,
    val description: String,
    val failingUrl: String? = null
)

class WebViewState(
    content: WebContent,
    pageTitle: String? = null,
    lastLoadedUrl: String? = null,
    loadingState: LoadingState = LoadingState.Initializing,
    errors: List<WebViewError> = emptyList(),
    canGoBack: Boolean = false,
    canGoForward: Boolean = false,
    settings: WebViewSettings = WebViewSettings(),
    val bridge: WebViewBridge = WebViewBridge()
) {
    var content: WebContent by mutableStateOf(content)
    var pageTitle: String? by mutableStateOf(pageTitle)
    var lastLoadedUrl: String? by mutableStateOf(lastLoadedUrl)
    var loadingState: LoadingState by mutableStateOf(loadingState)
    var errors: List<WebViewError> by mutableStateOf(errors)
    var canGoBack: Boolean by mutableStateOf(canGoBack)
    var canGoForward: Boolean by mutableStateOf(canGoForward)
    var settings: WebViewSettings by mutableStateOf(settings)
}
