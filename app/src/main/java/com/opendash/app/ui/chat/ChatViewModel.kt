package com.opendash.app.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opendash.app.assistant.agent.AgentToolDispatcher
import com.opendash.app.assistant.agent.StreamingToolCallAggregator
import com.opendash.app.assistant.model.AssistantMessage
import com.opendash.app.assistant.model.AssistantSession
import com.opendash.app.assistant.model.ConversationState
import com.opendash.app.assistant.router.ConversationRouter
import com.opendash.app.tool.ToolExecutor
import com.opendash.app.tool.system.CameraProviderHolder
import com.opendash.app.tool.system.CaptureRequest
import com.opendash.app.tool.system.CaptureResult
import com.opendash.app.tool.system.OnDeviceOcr
import com.opendash.app.voice.pipeline.VoicePipeline
import com.opendash.app.voice.pipeline.VoicePipelineState
import com.opendash.app.voice.stt.SpeechToText
import com.opendash.app.voice.stt.SttResult
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val router: ConversationRouter,
    private val toolExecutor: ToolExecutor,
    private val moshi: Moshi,
    private val voicePipeline: VoicePipeline,
    private val stt: SpeechToText,
    private val cameraProviderHolder: CameraProviderHolder,
    private val onDeviceOcr: OnDeviceOcr,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AssistantMessage>>(emptyList())
    val messages: StateFlow<List<AssistantMessage>> = _messages.asStateFlow()

    private val _conversationState = MutableStateFlow<ConversationState>(ConversationState.Idle)
    val conversationState: StateFlow<ConversationState> = _conversationState.asStateFlow()

    private val _streamingContent = MutableStateFlow("")
    val streamingContent: StateFlow<String> = _streamingContent.asStateFlow()

    private val _partialSpeech = MutableStateFlow("")
    val partialSpeech: StateFlow<String> = _partialSpeech.asStateFlow()

    private val toolDispatcher = AgentToolDispatcher(toolExecutor, moshi)

    private var session: AssistantSession? = null
    private var sessionProviderId: String? = null

    val voiceState: StateFlow<VoicePipelineState> = voicePipeline.state

    companion object {
        private const val MAX_TOOL_ROUNDS = 10
    }

    fun startVoiceInput() {
        viewModelScope.launch {
            _conversationState.value = ConversationState.Listening
            _partialSpeech.value = ""

            var recognizedText = ""
            var recognitionError: String? = null
            stt.startListening().collect { result ->
                when (result) {
                    is SttResult.Final -> recognizedText = result.text
                    is SttResult.Partial -> _partialSpeech.value = result.text
                    is SttResult.Error -> {
                        recognitionError = result.message
                        return@collect
                    }
                }
            }

            val error = recognitionError
            if (error != null) {
                _conversationState.value = ConversationState.Error(error)
            } else if (recognizedText.isNotBlank()) {
                _partialSpeech.value = ""
                sendMessage(recognizedText)
            } else {
                _partialSpeech.value = ""
                _conversationState.value = ConversationState.Idle
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            val userMessage = AssistantMessage.User(content = text)
            _messages.value = _messages.value + userMessage
            _conversationState.value = ConversationState.Thinking

            try {
                val provider = router.resolveProvider(userInput = text)
                val activeSession = session?.takeIf { sessionProviderId == provider.id }
                    ?: provider.startSession().also {
                        session = it
                        sessionProviderId = provider.id
                    }

                val tools = com.opendash.app.tool.ToolFilter.filterByIntent(
                    allTools = toolExecutor.availableTools(),
                    userInput = text
                )
                val conversationMessages = _messages.value.toMutableList()
                var toolRounds = 0

                while (toolRounds < MAX_TOOL_ROUNDS) {
                    _streamingContent.value = ""
                    val responseBuilder = StringBuilder()
                    val toolCallAggregator = StreamingToolCallAggregator()

                    provider.sendStreaming(activeSession, conversationMessages, tools)
                        .collect { delta ->
                            responseBuilder.append(delta.contentDelta)
                            _streamingContent.value = responseBuilder.toString()
                            delta.toolCallDelta?.let { toolCallAggregator.accept(it) }
                        }
                    val toolCalls = toolCallAggregator.complete()

                    val assistantResponse = AssistantMessage.Assistant(
                        content = responseBuilder.toString(),
                        toolCalls = toolCalls
                    )
                    conversationMessages.add(assistantResponse)

                    if (toolCalls.isNotEmpty()) {
                        val results = toolDispatcher.dispatch(toolCalls)
                        conversationMessages.addAll(results)
                        toolRounds++
                        continue
                    }

                    _messages.value = _messages.value + assistantResponse
                    _streamingContent.value = ""
                    _conversationState.value = ConversationState.Idle
                    return@launch
                }

                Timber.w("Max tool rounds ($MAX_TOOL_ROUNDS) reached")
                _streamingContent.value = ""
                _conversationState.value = ConversationState.Idle
            } catch (e: Exception) {
                Timber.e(e, "Failed to send message")
                _streamingContent.value = ""
                _conversationState.value = ConversationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun scanTextWithCamera() {
        if (_conversationState.value is ConversationState.Thinking ||
            _conversationState.value is ConversationState.Listening
        ) return

        viewModelScope.launch {
            _conversationState.value = ConversationState.Thinking
            try {
                when (val capture = cameraProviderHolder.current().capture(CaptureRequest())) {
                    is CaptureResult.Success -> {
                        val text = onDeviceOcr.extractText(capture.imageBytes).trim()
                        if (text.isBlank()) {
                            _conversationState.value = ConversationState.Error(
                                context.getString(com.opendash.app.R.string.chat_scan_no_text)
                            )
                        } else {
                            sendMessage(context.getString(com.opendash.app.R.string.chat_scan_prompt, text))
                        }
                    }
                    is CaptureResult.Failed -> _conversationState.value = ConversationState.Error(capture.reason)
                    CaptureResult.NotReady -> _conversationState.value = ConversationState.Error(
                        context.getString(com.opendash.app.R.string.chat_scan_camera_not_ready)
                    )
                }
            } catch (error: Exception) {
                Timber.e(error, "On-device camera OCR failed")
                _conversationState.value = ConversationState.Error(
                    error.message ?: context.getString(com.opendash.app.R.string.chat_scan_failed)
                )
            }
        }
    }

}
