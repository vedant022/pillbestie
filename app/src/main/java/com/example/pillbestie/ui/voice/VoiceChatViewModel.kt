package com.example.pillbestie.ui.voice

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceChatViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val settingsRepository = SettingsRepository(application)
    private val _uiState = MutableStateFlow(VoiceChatState())
    val uiState: StateFlow<VoiceChatState> = _uiState

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer

    init {
        textToSpeech = TextToSpeech(application, this)
        setupSpeechRecognizer()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.value = _uiState.value.copy(isListening = true)
            }

            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                if (spokenText.isNotBlank()) {
                    val currentConversation = _uiState.value.conversation.toMutableList()
                    currentConversation.add("You: $spokenText")
                    _uiState.value = _uiState.value.copy(conversation = currentConversation)
                    generateBestieResponse(spokenText)
                }
                _uiState.value = _uiState.value.copy(isListening = false)
            }

            override fun onError(error: Int) {
                _uiState.value = _uiState.value.copy(isListening = false, error = "Error listening. Please try again.")
            }
            // Other overrides ...
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        _uiState.value = _uiState.value.copy(isListening = false)
    }

    private fun generateBestieResponse(spokenText: String) {
        viewModelScope.launch {
            val personality = settingsRepository.personality.first()
            val response = when (personality) {
                Personality.CARING -> getCaringResponse(spokenText)
                Personality.SARCASTIC -> getSarcasticResponse(spokenText)
                Personality.CHAOTIC -> getChaoticResponse(spokenText)
            }
            speak(response)
            val currentConversation = _uiState.value.conversation.toMutableList()
            currentConversation.add("Bestie: $response")
            _uiState.value = _uiState.value.copy(conversation = currentConversation)
        }
    }

    private fun speak(text: String) {
        _uiState.value = _uiState.value.copy(isSpeaking = true)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.language = locale
            }
        } else {
            _uiState.value = _uiState.value.copy(error = "Text-to-speech initialization failed.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech.stop()
        textToSpeech.shutdown()
        speechRecognizer.destroy()
    }

    private fun getCaringResponse(text: String): String {
        return when {
            text.contains("hello") -> "Hey there, bestie! How are you feeling today?"
            text.contains("took my medicine") -> "That's wonderful! I'm so proud of you for staying on top of it."
            text.contains("missed my medicine") -> "It's okay, don't worry. Just take it as soon as you remember. I'm here for you."
            else -> "I'm here to listen. Tell me anything that's on your mind."
        }
    }

    private fun getSarcasticResponse(text: String): String {
        return when {
            text.contains("hello") -> "Oh, look who decided to show up. What do you want?"
            text.contains("took my medicine") -> "Wow, you managed to do the bare minimum. Should I throw you a party?"
            text.contains("missed my medicine") -> "Shocking. Absolutely shocking. Try to remember next time, unless you enjoy feeling awful."
            else -> "I'm listening, but I'm also judging you. So, make it good."
        }
    }

    private fun getChaoticResponse(text: String): String {
        return when {
            text.contains("hello") -> "HEY! What's the plan?! Are we taking over the world today?!"
            text.contains("took my medicine") -> "YES! POWER UP! You're basically a superhero now!"
            text.contains("missed my medicine") -> "A minor setback! A plot twist! The hero's journey is never easy! Let's get back on track!"
            else -> "Tell me your secrets! Or your grocery list! I'm not picky!"
        }
    }
}
