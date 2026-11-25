package io.github.miladsalimi.jetview.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.miladsalimi.jetview.domain.model.WebContent
import io.github.miladsalimi.jetview.domain.model.WebViewNavigator
import io.github.miladsalimi.jetview.domain.model.WebViewState

@Composable
fun rememberWebViewState(url: String, headers: Map<String, String> = emptyMap()): WebViewState =
    remember(url) {
        WebViewState(
            content = WebContent.Url(url, headers)
        )
    }

@Composable
fun rememberWebViewState(content: WebContent): WebViewState =
    remember(content) {
        WebViewState(
            content = content
        )
    }

@Composable
fun rememberWebViewNavigator(): WebViewNavigator =
    remember {
        WebViewNavigator()
    }
