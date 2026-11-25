package io.github.miladsalimi.jetview.domain.model

sealed interface WebContent {
    data class Url(
        val url: String,
        val httpHeaders: Map<String, String> = emptyMap()
    ) : WebContent

    data class Data(
        val data: String,
        val baseUrl: String? = null,
        val mimeType: String = "text/html",
        val encoding: String = "utf-8"
    ) : WebContent

    data class Html(
        val html: String,
        val baseUrl: String? = null,
        val mimeType: String = "text/html",
        val encoding: String = "utf-8"
    ) : WebContent
}
