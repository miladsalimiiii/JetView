package io.github.miladsalimi.jetview.presentation.component

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView as AndroidWebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.miladsalimi.jetview.domain.model.LoadingState
import io.github.miladsalimi.jetview.domain.model.WebContent
import io.github.miladsalimi.jetview.domain.model.WebViewBridge
import io.github.miladsalimi.jetview.domain.model.WebViewCommand
import io.github.miladsalimi.jetview.domain.model.WebViewError
import io.github.miladsalimi.jetview.domain.model.WebViewNavigator
import io.github.miladsalimi.jetview.domain.model.WebViewState
import kotlinx.coroutines.flow.collectLatest

private class WebAppInterface(private val bridge: WebViewBridge) {
    @JavascriptInterface
    fun postMessage(message: String) {
        bridge.postMessage(message)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebView(
    state: WebViewState,
    modifier: Modifier,
    navigator: WebViewNavigator,
    onCreated: () -> Unit,
    onDispose: () -> Unit
) {
    var webViewInstance: AndroidWebView? = null

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AndroidWebView(context).apply {
                webViewInstance = this

                // Apply settings
                val settings = state.settings
                this.settings.javaScriptEnabled = settings.javaScriptEnabled
                this.settings.domStorageEnabled = settings.domStorageEnabled
                this.settings.loadsImagesAutomatically = settings.loadsImagesAutomatically
                this.settings.mediaPlaybackRequiresUserGesture = settings.mediaPlaybackRequiresUserGesture
                this.settings.setSupportZoom(settings.supportZoom)
                this.settings.builtInZoomControls = settings.supportZoom
                if (settings.userAgentString != null) {
                    this.settings.userAgentString = settings.userAgentString
                }

                // Add JS Bridge interface
                val bridge = state.bridge
                addJavascriptInterface(WebAppInterface(bridge), bridge.bridgeName)

                // Download listener
                setDownloadListener { url, _, _, _, _ ->
                    navigator.loadUrl(url)
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: AndroidWebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        url?.let { state.lastLoadedUrl = it }
                        state.loadingState = LoadingState.Loading(0.1f)
                        state.canGoBack = canGoBack()
                        state.canGoForward = canGoForward()
                        navigator.canGoBack = canGoBack()
                        navigator.canGoForward = canGoForward()
                    }

                    override fun onPageFinished(view: AndroidWebView?, url: String?) {
                        super.onPageFinished(view, url)
                        url?.let { state.lastLoadedUrl = it }
                        state.loadingState = LoadingState.Finished
                        state.pageTitle = title
                        state.canGoBack = canGoBack()
                        state.canGoForward = canGoForward()
                        navigator.canGoBack = canGoBack()
                        navigator.canGoForward = canGoForward()
                    }

                    override fun onReceivedError(
                        view: AndroidWebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        if (error != null) {
                            val err = WebViewError(
                                errorCode = error.errorCode,
                                description = error.description.toString(),
                                failingUrl = request?.url?.toString()
                            )
                            state.errors = state.errors + err
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onReceivedTitle(view: AndroidWebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        state.pageTitle = title
                    }

                    override fun onProgressChanged(view: AndroidWebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        state.loadingState = if (newProgress == 100) {
                            LoadingState.Finished
                        } else {
                            LoadingState.Loading(newProgress / 100f)
                        }
                    }
                }

                when (val content = state.content) {
                    is WebContent.Url -> loadUrl(content.url, content.httpHeaders)
                    is WebContent.Html -> loadDataWithBaseURL(content.baseUrl, content.html, content.mimeType, content.encoding, null)
                    is WebContent.Data -> loadDataWithBaseURL(content.baseUrl, content.data, content.mimeType, content.encoding, null)
                }

                onCreated()
            }
        },
        update = { webView ->
            webViewInstance = webView
            // Update settings if changed
            val settings = state.settings
            webView.settings.javaScriptEnabled = settings.javaScriptEnabled
            webView.settings.domStorageEnabled = settings.domStorageEnabled

            when (val content = state.content) {
                is WebContent.Url -> {
                    if (webView.url != content.url) {
                        webView.loadUserAgentAndUrl(content) // or loadUrl
                    }
                }
                is WebContent.Html -> {
                    webView.loadDataWithBaseURL(content.baseUrl, content.html, content.mimeType, content.encoding, null)
                }
                is WebContent.Data -> {
                    webView.loadDataWithBaseURL(content.baseUrl, content.data, content.mimeType, content.encoding, null)
                }
            }
        },
        onRelease = { webView ->
            webView.stopLoading()
            webView.destroy()
            webViewInstance = null
            onDispose()
        }
    )

    LaunchedEffect(navigator) {
        navigator.navigationRequests.collectLatest { command ->
            val webView = webViewInstance ?: return@collectLatest
            when (command) {
                is WebViewCommand.Back -> if (webView.canGoBack()) webView.goBack()
                is WebViewCommand.Forward -> if (webView.canGoForward()) webView.goForward()
                is WebViewCommand.Reload -> webView.reload()
                is WebViewCommand.StopLoading -> webView.stopLoading()
                is WebViewCommand.LoadUrl -> webView.loadUrl(command.url, command.headers)
                is WebViewCommand.EvaluateJavaScript -> {
                    webView.evaluateJavascript(command.script) { result ->
                        command.callback?.invoke(result)
                    }
                }
            }
        }
    }
}

private fun AndroidWebView.loadUserAgentAndUrl(content: WebContent.Url) {
    loadUrl(content.url, content.httpHeaders)
}
