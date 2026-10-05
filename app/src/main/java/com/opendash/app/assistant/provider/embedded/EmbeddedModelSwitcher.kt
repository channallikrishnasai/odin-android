package com.opendash.app.assistant.provider.embedded

import com.opendash.app.assistant.provider.AssistantProvider

/** Assistant provider that can replace its active local model without restarting the app. */
interface EmbeddedModelSwitcher : AssistantProvider {
    suspend fun switchModel(newModelPath: String): Boolean
}
