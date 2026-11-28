package io.github.miladsalimi.jetviewapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.miladsalimi.jetview.domain.model.LoadingState
import io.github.miladsalimi.jetview.domain.model.WebContent
import io.github.miladsalimi.jetview.presentation.component.WebView
import io.github.miladsalimi.jetview.presentation.state.rememberWebViewNavigator
import io.github.miladsalimi.jetview.presentation.state.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }

        // State & Navigator for Browser tab
        var urlInput by remember { mutableStateOf("https://google.com") }
        val webViewState = rememberWebViewState(urlInput)
        val navigator = rememberWebViewNavigator()

        if (selectedTab == 0) {
            PlatformBackHandler(enabled = webViewState.canGoBack) {
                navigator.goBack()
            }
        }

        var jsResult by remember { mutableStateOf<String?>(null) }

        val bridgeMessages = remember { mutableStateListOf<String>() }

        // Custom HTML content state with JS Bridge integration & visual feedback
        var customHtml by remember {
            mutableStateOf(
                """
                <html>
                <body style="font-family: sans-serif; padding: 24px; background-color: #f7f9fc; text-align: center;">
                    <div style="max-width: 400px; margin: 0 auto; background: white; padding: 24px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">
                        <h2 style="color: #6200EE; margin-top: 0;">JetView JS Bridge</h2>
                        <p style="color: #555; font-size: 14px; margin-bottom: 24px;">Click the button below to send a message from JavaScript to Kotlin via <b>JetViewBridge</b>.</p>
                        <button onclick="
                            var msg = 'Hello from JS! Time: ' + new Date().toLocaleTimeString();
                            window.JetViewBridge.postMessage(msg);
                            document.getElementById('response').innerText = '✅ Sent to Kotlin: ' + msg;
                            document.getElementById('response').style.color = '#4CAF50';
                        " style="width: 100%; padding: 16px; background: #6200EE; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; box-shadow: 0 2px 6px rgba(98,0,238,0.3);">Send Bridge Message</button>
                        <p id="response" style="margin-top: 20px; font-size: 14px; font-weight: bold; color: #888;"></p>
                    </div>
                </body>
                </html>
                """.trimIndent()
            )
        }
        val htmlWebViewState = rememberWebViewState(WebContent.Html(customHtml))

        // Collect bridge messages
        LaunchedEffect(htmlWebViewState.bridge) {
            htmlWebViewState.bridge.messages.collect { message ->
                bridgeMessages.add(0, message)
            }
        }

        // Custom Data content state with well-formatted data string
        var customData by remember {
            mutableStateOf(
                """
                <html>
                <body style="font-family: sans-serif; padding: 20px; background-color: #fff3e0;">
                    <h2 style="color: #e65100;">WebContent.Data Demo</h2>
                    <p>This content was loaded using <b>WebContent.Data</b> with custom MIME type and encoding.</p>
                    <div style="background: white; padding: 12px; border-left: 4px solid #e65100; margin-top: 12px;">
                        <p style="margin: 0; color: #333;">Raw data string successfully rendered into WebView!</p>
                    </div>
                </body>
                </html>
                """.trimIndent()
            )
        }
        val dataWebViewState = rememberWebViewState(WebContent.Data(customData))

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = when (selectedTab) {
                                    0 -> webViewState.pageTitle ?: "JetView Browser"
                                    1 -> "JetView JS Bridge & HTML"
                                    2 -> "JetView Data Content"
                                    3 -> "JetView JS Evaluation"
                                    else -> "WebView Error Logs"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                            Text(
                                text = when (selectedTab) {
                                    0 -> webViewState.lastLoadedUrl ?: urlInput
                                    1 -> "${bridgeMessages.size} messages received from JS"
                                    2 -> "WebContent.Data mode"
                                    3 -> "JavaScript Evaluation mode"
                                    else -> "${webViewState.errors.size} errors recorded"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    },
                    actions = {
                        if (selectedTab == 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { navigator.goBack() }, enabled = webViewState.canGoBack) {
                                    Text("Back")
                                }
                                TextButton(onClick = { navigator.goForward() }, enabled = webViewState.canGoForward) {
                                    Text("Forward")
                                }
                                TextButton(onClick = { navigator.reload() }) {
                                    Text("Reload")
                                }
                                TextButton(onClick = { navigator.stopLoading() }) {
                                    Text("Stop")
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {},
                        label = { Text("Browser") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {},
                        label = { Text("JS Bridge") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {},
                        label = { Text("Data") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {},
                        label = { Text("JS Eval") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = {},
                        label = { Text("Errors") }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // URL input bar and quick buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = urlInput,
                                    onValueChange = { urlInput = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    label = { Text("URL") },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                    keyboardActions = KeyboardActions(
                                        onGo = {
                                            val targetUrl = if (urlInput.startsWith("http://") || urlInput.startsWith("https://")) {
                                                urlInput
                                            } else {
                                                "https://$urlInput"
                                            }
                                            navigator.loadUrl(targetUrl)
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val targetUrl = if (urlInput.startsWith("http://") || urlInput.startsWith("https://")) {
                                            urlInput
                                        } else {
                                            "https://$urlInput"
                                        }
                                        navigator.loadUrl(targetUrl)
                                    }
                                ) {
                                    Text("Go")
                                }
                            }

                            // Quick presets & settings toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(onClick = { urlInput = "https://google.com"; navigator.loadUrl(urlInput) }) {
                                    Text("Google")
                                }
                                OutlinedButton(onClick = { urlInput = "https://github.com"; navigator.loadUrl(urlInput) }) {
                                    Text("GitHub")
                                }
                                OutlinedButton(
                                    onClick = {
                                        webViewState.settings = webViewState.settings.copy(
                                            javaScriptEnabled = !webViewState.settings.javaScriptEnabled
                                        )
                                    }
                                ) {
                                    Text("JS: ${if (webViewState.settings.javaScriptEnabled) "ON" else "OFF"}")
                                }
                            }

                            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
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
                    1 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("JS Bridge Messages Received from Web:", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (bridgeMessages.isEmpty()) {
                                        Text("No messages yet. Click 'Send Bridge Message' in the web view below.", style = MaterialTheme.typography.bodySmall)
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth().height(100.dp)
                                        ) {
                                            items(bridgeMessages) { msg ->
                                                Text("• $msg", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }

                            WebView(
                                state = htmlWebViewState,
                                modifier = Modifier.fillMaxSize().weight(1f)
                            )
                        }
                    }
                    2 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("WebContent.Data Demo", style = MaterialTheme.typography.titleSmall)
                                    OutlinedTextField(
                                        value = customData,
                                        onValueChange = {
                                            customData = it
                                            dataWebViewState.content = WebContent.Data(it)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(140.dp),
                                        label = { Text("Data HTML String") }
                                    )
                                }
                            }

                            WebView(
                                state = dataWebViewState,
                                modifier = Modifier.fillMaxSize().weight(1f)
                            )
                        }
                    }
                    3 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Evaluate JavaScript Demo", style = MaterialTheme.typography.titleSmall)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                navigator.evaluateJavaScript("document.title") { result ->
                                                    jsResult = "Title: $result"
                                                }
                                            }
                                        ) {
                                            Text("Get Title")
                                        }
                                        Button(
                                            onClick = {
                                                navigator.evaluateJavaScript("document.body.style.backgroundColor = '#e0f7fa'; 'Background changed!';") { result ->
                                                    jsResult = "Result: $result"
                                                }
                                            }
                                        ) {
                                            Text("Change BG")
                                        }
                                    }
                                    if (jsResult != null) {
                                       Spacer(modifier = Modifier.height(4.dp))
                                       Text(text = jsResult!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            WebView(
                                state = webViewState,
                                navigator = navigator,
                                modifier = Modifier.fillMaxSize().weight(1f)
                            )
                        }
                    }
                    4 -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("WebView Error Logs", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (webViewState.errors.isEmpty()) {
                                Text("No errors recorded.")
                            } else {
                                webViewState.errors.forEach { err ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Code: ${err.errorCode}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Description: ${err.description}", style = MaterialTheme.typography.bodySmall)
                                            err.failingUrl?.let { Text("Url: $it", style = MaterialTheme.typography.bodySmall) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
