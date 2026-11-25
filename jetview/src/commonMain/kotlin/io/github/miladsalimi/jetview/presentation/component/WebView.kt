package io.github.miladsalimi.jetview.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.miladsalimi.jetview.domain.model.WebViewNavigator
import io.github.miladsalimi.jetview.domain.model.WebViewState
import io.github.miladsalimi.jetview.presentation.state.rememberWebViewNavigator

@Composable
expect fun WebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    onCreated: () -> Unit = {},
    onDispose: () -> Unit = {}
)
