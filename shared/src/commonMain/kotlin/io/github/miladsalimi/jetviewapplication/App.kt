package io.github.miladsalimi.jetviewapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.miladsalimi.jetview.domain.model.LoadingState
import io.github.miladsalimi.jetview.presentation.component.WebView
import io.github.miladsalimi.jetview.presentation.state.rememberWebViewNavigator
import io.github.miladsalimi.jetview.presentation.state.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val webViewState = rememberWebViewState("https://google.com")
        val navigator = rememberWebViewNavigator()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = webViewState.pageTitle ?: "JetView Browser",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                            Text(
                                text = webViewState.lastLoadedUrl ?: "https://google.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    },
                    actions = {
                        Row {
                            TextButton(
                                onClick = { navigator.goBack() },
                                enabled = webViewState.canGoBack
                            ) {
                                Text("Back")
                            }
                            TextButton(
                                onClick = { navigator.goForward() },
                                enabled = webViewState.canGoForward
                            ) {
                                Text("Forward")
                            }
                            TextButton(
                                onClick = { navigator.reload() }
                            ) {
                                Text("Reload")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                WebView(
                    state = webViewState,
                    navigator = navigator,
                    modifier = Modifier.fillMaxSize()
                )

                val loadingState = webViewState.loadingState
                if (loadingState is LoadingState.Loading) {
                    LinearProgressIndicator(
                        progress = { loadingState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
