package com.opendash.app.voice.stt

/** Small parser for the text fields returned by Vosk's JSON API. */
internal object VoskResultParser {
    private val finalText = Regex("\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"")
    private val partialText = Regex("\\\"partial\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"")

    fun finalText(json: String): String? = extract(finalText, json)

    fun partialText(json: String): String? = extract(partialText, json)

    private fun extract(pattern: Regex, json: String): String? = pattern.find(json)
        ?.groupValues
        ?.getOrNull(1)
        ?.replace("\\\"", "\"")
        ?.replace("\\\\", "\\")
        ?.trim()
        ?.takeIf(String::isNotEmpty)
}
