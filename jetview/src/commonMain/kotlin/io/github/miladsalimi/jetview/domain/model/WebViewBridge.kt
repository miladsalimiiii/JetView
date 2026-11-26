package io.github.miladsalimi.jetview.domain.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebViewBridge(
    val bridgeName: String = "JetViewBridge"
) {
    private val _messages = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 64)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun postMessage(message: String) {
        _messages.tryEmit(message)
    }
}
