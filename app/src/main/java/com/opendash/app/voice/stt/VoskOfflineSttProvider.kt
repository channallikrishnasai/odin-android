package com.opendash.app.voice.stt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.opendash.app.voice.AudioEffects
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import org.vosk.Model
import org.vosk.Recognizer
import timber.log.Timber
import java.io.File

/** On-device English transcription using the Vosk model already used for wake-word detection. */
class VoskOfflineSttProvider(
    private val context: Context,
    private val modelDirProvider: () -> File = { File(context.filesDir, MODEL_DIR_NAME) }
) : SpeechToText {

    private val _isListening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    @Volatile
    private var activeRecorder: AudioRecord? = null

    override fun startListening(): Flow<SttResult> = flow {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            emit(SttResult.Error("Microphone permission is required for offline voice input."))
            return@flow
        }

        val modelDir = modelDirProvider()
        if (!modelDir.isDirectory || modelDir.listFiles().isNullOrEmpty()) {
            emit(SttResult.Error("Offline speech model is missing. Connect once to download the English voice model."))
            return@flow
        }

        var model: Model? = null
        var recognizer: Recognizer? = null
        var recorder: AudioRecord? = null
        var echoCanceler: AcousticEchoCanceler? = null
        var noiseSuppressor: NoiseSuppressor? = null
        try {
            val loadedModel = Model(modelDir.absolutePath)
            model = loadedModel
            val activeRecognizer = Recognizer(loadedModel, SAMPLE_RATE.toFloat())
            recognizer = activeRecognizer
            val minBufferBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
            if (minBufferBytes <= 0) {
                emit(SttResult.Error("This device could not open the microphone for offline transcription."))
                return@flow
            }

            val sampleCapacity = maxOf(minBufferBytes / 2, SAMPLE_RATE / 2)
            recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL,
                ENCODING,
                sampleCapacity * Short.SIZE_BYTES
            )
            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                emit(SttResult.Error("Microphone initialization failed."))
                return@flow
            }

            activeRecorder = recorder
            recorder.startRecording()
            echoCanceler = AudioEffects.applyAcousticEchoCanceler(recorder.audioSessionId)
            noiseSuppressor = AudioEffects.applyNoiseSuppressor(recorder.audioSessionId)
            _isListening.value = true
            Timber.d("Vosk offline transcription started")

            val audio = ShortArray(sampleCapacity)
            val startedAt = SystemClock.elapsedRealtime()
            var latestPartial = ""
            while (currentCoroutineContext().isActive && _isListening.value) {
                if (SystemClock.elapsedRealtime() - startedAt >= MAX_LISTENING_MS) {
                    val final = VoskResultParser.finalText(activeRecognizer.getFinalResult())
                    if (final != null) emit(SttResult.Final(final, 1.0f))
                    else emit(SttResult.Error("I didn't catch that. Please try again."))
                    return@flow
                }

                val read = recorder.read(audio, 0, audio.size)
                if (read < 0) {
                    emit(SttResult.Error("Microphone capture stopped unexpectedly ($read)."))
                    return@flow
                }
                if (read == 0) continue

                if (activeRecognizer.acceptWaveForm(audio, read)) {
                    val final = VoskResultParser.finalText(activeRecognizer.getResult())
                    if (final != null) {
                        emit(SttResult.Final(final, 1.0f))
                        return@flow
                    }
                    latestPartial = ""
                } else {
                    val partial = VoskResultParser.partialText(activeRecognizer.getPartialResult())
                    if (partial != null && partial != latestPartial) {
                        latestPartial = partial
                        emit(SttResult.Partial(partial))
                    }
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Timber.e(error, "Vosk offline transcription failed")
            emit(SttResult.Error(error.message ?: "Offline speech recognition failed."))
        } finally {
            _isListening.value = false
            if (activeRecorder === recorder) activeRecorder = null
            runCatching { recorder?.stop() }
            runCatching { recorder?.release() }
            AudioEffects.release(echoCanceler, noiseSuppressor)
            runCatching { recognizer?.close() }
            runCatching { model?.close() }
        }
    }.flowOn(Dispatchers.IO)

    override fun stopListening() {
        _isListening.value = false
        runCatching { activeRecorder?.stop() }
    }

    companion object {
        private const val SAMPLE_RATE = 16_000
        private const val MAX_LISTENING_MS = 15_000L
        private const val MODEL_DIR_NAME = "vosk-model"
        private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }
}
