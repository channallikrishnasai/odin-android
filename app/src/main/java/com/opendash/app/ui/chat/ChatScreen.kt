package com.opendash.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.opendash.app.assistant.model.ConversationState
import com.opendash.app.R

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val state by viewModel.conversationState.collectAsState()
    val streaming by viewModel.streamingContent.collectAsState()
    val partialSpeech by viewModel.partialSpeech.collectAsState()
    val listState = rememberLazyListState()
    val mathPrompt = stringResource(R.string.chat_demo_prompt_math)
    val privacyPrompt = stringResource(R.string.chat_demo_prompt_privacy)
    val bengaluruPrompt = stringResource(R.string.chat_demo_prompt_bengaluru)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
      Column(
          modifier = modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .systemBarsPadding()
      ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.tab_chat), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(R.string.providers_badge_on_device),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(top = 8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(message)
            }

            if (messages.isEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                        Text(
                            text = stringResource(R.string.chat_demo_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.chat_demo_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        AssistChip(
                            onClick = { viewModel.sendMessage(mathPrompt) },
                            label = { Text(stringResource(R.string.chat_demo_chip_math)) }
                        )
                        AssistChip(
                            onClick = { viewModel.sendMessage(privacyPrompt) },
                            label = { Text(stringResource(R.string.chat_demo_chip_privacy)) }
                        )
                        AssistChip(
                            onClick = { viewModel.sendMessage(bengaluruPrompt) },
                            label = { Text(stringResource(R.string.chat_demo_chip_bengaluru)) }
                        )
                        AssistChip(
                            onClick = { viewModel.scanTextWithCamera() },
                            label = { Text(stringResource(R.string.chat_demo_chip_scan)) }
                        )
                    }
                }
            }

            if (streaming.isNotBlank()) {
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = streaming,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (state is ConversationState.Thinking || state is ConversationState.Listening) {
            LinearProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
        }

        if (state is ConversationState.Listening && partialSpeech.isNotBlank()) {
            Text(
                text = "${stringResource(R.string.voice_listening)} $partialSpeech",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        if (state is ConversationState.Error) {
            val errorMsg = (state as ConversationState.Error).message
            val isNoProvider = errorMsg.contains("No available") || errorMsg.contains("not found")
            Text(
                text = if (isNoProvider) {
                    "AI provider not configured. Go to Settings to set up On-Device LLM, OpenClaw, or external LLM endpoint."
                } else {
                    errorMsg
                },
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        ChatInputBar(
            onSend = { viewModel.sendMessage(it) },
            onMicClick = { viewModel.startVoiceInput() },
            onScanClick = { viewModel.scanTextWithCamera() },
            enabled = state !is ConversationState.Thinking && state !is ConversationState.Listening
        )
      }
    }
}
