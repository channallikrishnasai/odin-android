package com.opendash.app.voice.stt

/**
 * Which backend handles speech-to-text. The system default uses Android's
 * [android.speech.SpeechRecognizer], which relies on Google Play Services or
 * an OEM STT engine and usually requires network — fine for most devices
 * but not acceptable for fully-offline tablets.
 *
 * Vosk is the offline default. Whisper remains an optional native backend;
 * Android recognition is available when the user explicitly chooses it.
 */
enum class SttProviderType(val prefValue: String) {
    /** android.speech.SpeechRecognizer (online / OEM service). */
    ANDROID("android"),

    /** Vosk offline English transcription. */
    VOSK_OFFLINE("vosk"),

    /** whisper.cpp offline STT via JNI. */
    WHISPER_OFFLINE("whisper");

    companion object {
        fun fromPref(raw: String?): SttProviderType =
            values().firstOrNull { it.prefValue == raw?.lowercase() } ?: VOSK_OFFLINE
    }
}
