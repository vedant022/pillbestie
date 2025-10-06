package com.example.pillbestie.ui.voice

data class VoiceChatState(
    val conversation: List<String> = emptyList(),
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val error: String? = null
)
