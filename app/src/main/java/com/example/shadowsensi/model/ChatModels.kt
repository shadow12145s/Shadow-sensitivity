package com.example.shadowsensi.model

enum class ChatSender {
    USER,
    SHADOW_AI
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val thinkingProcess: String? = null,
    val isThinking: Boolean = false,
    val isError: Boolean = false
)

data class QuickPrompt(
    val title: String,
    val prompt: String,
    val icon: String
)
