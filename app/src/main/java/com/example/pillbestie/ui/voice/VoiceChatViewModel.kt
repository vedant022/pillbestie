package com.example.pillbestie.ui.voice

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.MedicineRepository
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.SettingsRepository
import com.example.pillbestie.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class VoiceChatViewModel(
    application: Application,
    private val medicineRepository: MedicineRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(VoiceChatState())
    val uiState: StateFlow<VoiceChatState> = _uiState.asStateFlow()

    private val textToSpeech: TextToSpeech = TextToSpeech(application, this)
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())

    init {
        setupSpeechRecognizer()
        setupTtsListener()
        greetUser()
    }

    private fun setupTtsListener() {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = true)
            }

            override fun onDone(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = false)
            }

            override fun onError(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = false)
            }
        })
    }

    private fun greetUser() {
        viewModelScope.launch {
            val personality = settingsRepository.personality.first()
            val greeting = getGreeting(personality)
            addMessageToConversation("Bestie: $greeting")
            speak(greeting)
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.value = _uiState.value.copy(isListening = true)
            }

            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                if (spokenText.isNotBlank()) {
                    addMessageToConversation("You: $spokenText")
                    handleCommand(spokenText)
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

    private fun handleCommand(command: String) {
        viewModelScope.launch {
            when {
                command.contains("snooze") -> snoozeNextDose()
                command.contains("skip") -> skipNextDose()
                else -> generateBestieResponse(command)
            }
        }
    }

    private suspend fun snoozeNextDose() {
        val nextMedicine = medicineRepository.getNextUpcomingMedicine()
        if (nextMedicine != null) {
            notificationScheduler.cancel(nextMedicine)
            val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000 // 15 minutes
            val snoozedMedicine = nextMedicine.copy(times = listOf(snoozeTime))
            notificationScheduler.schedule(snoozedMedicine)

            val response = "I've snoozed your next dose of ${nextMedicine.name} for 15 minutes."
            addMessageToConversation("Bestie: $response")
            speak(response)
        } else {
            val response = "You don't have any upcoming doses to snooze."
            addMessageToConversation("Bestie: $response")
            speak(response)
        }
    }

    private suspend fun skipNextDose() {
        val nextMedicine = medicineRepository.getNextUpcomingMedicine()
        if (nextMedicine != null) {
            val doseLog = DoseLog(
                medicineId = nextMedicine.id,
                scheduledTime = nextMedicine.times.first(),
                status = "SKIPPED",
                wasMissed = true,
                takenTime = 0L
            )
            medicineRepository.insert(doseLog)
            notificationScheduler.cancel(nextMedicine)

            val response = "I've marked your next dose of ${nextMedicine.name} as skipped."
            addMessageToConversation("Bestie: $response")
            speak(response)
        } else {
            val response = "You don't have any upcoming doses to skip."
            addMessageToConversation("Bestie: $response")
            speak(response)
        }
    }


    fun startListening() {
        if (textToSpeech.isSpeaking) return
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
            addMessageToConversation("Bestie: $response")
            speak(response)
        }
    }

    private fun speak(text: String) {
        val utteranceId = UUID.randomUUID().toString()
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    private fun addMessageToConversation(message: String) {
        val currentConversation = _uiState.value.conversation.toMutableList()
        currentConversation.add(message)
        _uiState.value = _uiState.value.copy(conversation = currentConversation)
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
    private fun getGreeting(personality: Personality): String {
        return when (personality) {
            Personality.CARING -> "Hey there, bestie! I'm here to help you with anything you need."
            Personality.SARCASTIC -> "Oh, it's you again. What do you want now?"
            Personality.CHAOTIC -> "LETS GOOO! What kind of chaos are we causing today?!"
        }
    }

    private fun getCaringResponse(text: String): String {
        return when {
            text.contains("what can you do") -> "I can help you manage your medications. You can ask me to snooze or skip your next dose, and I can answer questions about the app."
            text.contains("how do I add a medicine") -> "You can add a new medicine by tapping the 'Add Meds' button on the home screen."
            text.contains("hello") -> "Hey there, bestie! How are you feeling today?"
            text.contains("took my medicine") -> "That's wonderful! I'm so proud of you for staying on top of it."
            text.contains("missed my medicine") -> "It's okay, don't worry. Just take it as soon as you remember. I'm here for you."
            else -> "I'm here to listen. Tell me anything that's on your mind."
        }
    }

    private fun getSarcasticResponse(text: String): String {
        return when {
            text.contains("what can you do") -> "I'm not your personal assistant, but I can help you with your meds. Don't get used to it."
            text.contains("how do I add a medicine") -> "Figure it out. Or, you know, tap the giant 'Add Meds' button."
            text.contains("hello") -> "Oh, look who decided to show up. What do you want?"
            text.contains("took my medicine") -> "Wow, you managed to do the bare minimum. Should I throw you a party?"
            text.contains("missed my medicine") -> "Shocking. Absolutely shocking. Try to remember next time, unless you enjoy feeling awful."
            else -> "I'm listening, but I'm also judging you. So, make it good."
        }
    }

    private fun getChaoticResponse(text: String): String {
        return when {
            text.contains("what can you do") -> "I AM THE MASTER OF MEDS! I CAN SNOOZE! I CAN SKIP! I AM A CHAOTIC GOD OF PILLS!"
            text.contains("how do I add a medicine") -> "SMASH THAT 'ADD MEDS' BUTTON! GO! GO! GO!"
            text.contains("hello") -> "HEY! What's the plan?! Are we taking over the world today?!"
            text.contains("took my medicine") -> "YES! POWER UP! You're basically a superhero now!"
            text.contains("missed my medicine") -> "A minor setback! A plot twist! The hero's journey is never easy! Let's get back on track!"
            else -> "Tell me your secrets! Or your grocery list! I'm not picky!"
        }
    }
}
