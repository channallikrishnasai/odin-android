package com.opendash.app.voice.stt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Fallback for optional offline STT implementations that cannot initialize.
 * Emits an actionable error instead of leaving the voice pipeline hanging.
 *
 * Vosk has a real implementation; this remains for Whisper when its native
 * library is unavailable.
 */
class OfflineSttStub(private val backendName: String) : SpeechToText {
    private val _listening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _listening.asStateFlow()

    override fun startListening(): Flow<SttResult> = flowOf(
        SttResult.Error("$backendName offline speech recognition is unavailable on this device. Select Vosk or Android speech in Settings.")
    )

    override fun stopListening() {
        _listening.value = false
    }
}
