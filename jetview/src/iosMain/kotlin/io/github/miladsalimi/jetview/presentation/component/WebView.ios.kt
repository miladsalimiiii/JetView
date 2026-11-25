package io.github.miladsalimi.jetview.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import io.github.miladsalimi.jetview.domain.model.LoadingState
import io.github.miladsalimi.jetview.domain.model.WebContent
import io.github.miladsalimi.jetview.domain.model.WebViewCommand
import io.github.miladsalimi.jetview.domain.model.WebViewNavigator
import io.github.miladsalimi.jetview.domain.model.WebViewState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.coroutines.flow.collectLatest
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebView(
    state: WebViewState,
    modifier: Modifier,
    navigator: WebViewNavigator,
    onCreated: () -> Unit,
    onDispose: () -> Unit
) {
    var webViewInstance: WKWebView? = null

    UIKitView(
        factory = {
            val config = WKWebViewConfiguration()
            val webView = WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = config).apply {
                webViewInstance = this
                navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                    @ObjCSignatureOverride
                    override fun webView(webView: WKWebView, didStartProvisionalNavigation: WKNavigation?) {
                        state.loadingState = LoadingState.Loading(0.1f)
                        webView.URL?.absoluteString?.let {
                            state.lastLoadedUrl = it
                        }
                        state.canGoBack = webView.canGoBack
                        state.canGoForward = webView.canGoForward
                        navigator.canGoBack = webView.canGoBack
                        navigator.canGoForward = webView.canGoForward
                    }

                    @ObjCSignatureOverride
                    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                        state.loadingState = LoadingState.Finished
                        webView.URL?.absoluteString?.let {
                            state.lastLoadedUrl = it
                        }
                        webView.title?.let {
                            state.pageTitle = it
                        }
                        state.canGoBack = webView.canGoBack
                        state.canGoForward = webView.canGoForward
                        navigator.canGoBack = webView.canGoBack
                        navigator.canGoForward = webView.canGoForward
                    }
                }
            }

            when (val content = state.content) {
                is WebContent.Url -> {
                    NSURL.URLWithString(content.url)?.let { nsUrl ->
                        webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                    }
                }
                is WebContent.Html -> {
                    webView.loadHTMLString(content.html, baseURL = content.baseUrl?.let { NSURL.URLWithString(it) })
                }
                is WebContent.Data -> {
                    webView.loadHTMLString(content.data, baseURL = content.baseUrl?.let { NSURL.URLWithString(it) })
                }
            }

            onCreated()
            webView
        },
        modifier = modifier,
        update = { webView ->
            webViewInstance = webView
            when (val content = state.content) {
                is WebContent.Url -> {
                    val currentUrl = webView.URL?.absoluteString
                    if (currentUrl != content.url) {
                        NSURL.URLWithString(content.url)?.let { nsUrl ->
                            webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                        }
                    }
                }
                is WebContent.Html -> {
                    webView.loadHTMLString(content.html, baseURL = content.baseUrl?.let { NSURL.URLWithString(it) })
                }
                is WebContent.Data -> {
                    webView.loadHTMLString(content.data, baseURL = content.baseUrl?.let { NSURL.URLWithString(it) })
                }
            }
        },
        onRelease = { webView ->
            webView.stopLoading()
            webViewInstance = null
            onDispose()
        }
    )

    LaunchedEffect(navigator) {
        navigator.navigationRequests.collectLatest { command ->
            val webView = webViewInstance ?: return@collectLatest
            when (command) {
                is WebViewCommand.Back -> if (webView.canGoBack) webView.goBack()
                is WebViewCommand.Forward -> if (webView.canGoForward) webView.goForward()
                is WebViewCommand.Reload -> webView.reload()
                is WebViewCommand.StopLoading -> webView.stopLoading()
                is WebViewCommand.LoadUrl -> {
                    NSURL.URLWithString(command.url)?.let { nsUrl ->
                        webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                    }
                }
                is WebViewCommand.EvaluateJavaScript -> {
                    webView.evaluateJavaScript(command.script) { result, _ ->
                        command.callback?.invoke(result?.toString())
                    }
                }
            }
        }
    }
}
