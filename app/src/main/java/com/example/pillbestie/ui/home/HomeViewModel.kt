package com.example.pillbestie.ui.home

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeViewModel(
    private val medicineRepository: MedicineRepository,
    private val settingsRepository: SettingsRepository,
    application: Application
) : ViewModel(), TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(application, this)

    val medicines: StateFlow<List<MedicineUIData>> = combine(
        medicineRepository.allMedicines,
        medicineRepository.allDoseLogs
    ) { medicines, doseLogs ->
        val today = Calendar.getInstance()
        val doseLogMap = doseLogs
            .filter { log ->
                val calLog = Calendar.getInstance().apply { timeInMillis = log.takenTime ?: 0 }
                calLog.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calLog.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            }
            .associateBy { it.medicineId }

        medicines.map { medicine ->
            MedicineUIData(medicine, doseLogMap.containsKey(medicine.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _affirmation = MutableStateFlow<String?>(null)
    val affirmation: StateFlow<String?> = _affirmation

    val takenAction: StateFlow<TakenAction> = settingsRepository.takenAction.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TakenAction.QUICK_TAP
    )

    val personality: StateFlow<Personality> = settingsRepository.personality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Personality.CARING
    )

    val profileName: StateFlow<String> = settingsRepository.profileName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )
    
    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting

    init {
        viewModelScope.launch {
            val currentPersonality = personality.first()
            _greeting.value = generateGreeting(currentPersonality, profileName.first())
        }
    }

    fun markDoseAsTaken(medicine: Medicine) {
        viewModelScope.launch {
            val doseLog = DoseLog(
                medicineId = medicine.id,
                scheduledTime = medicine.timeInMillis,
                takenTime = System.currentTimeMillis(),
                wasMissed = false
            )
            medicineRepository.insert(doseLog)
            triggerAffirmation()
        }
    }

    private fun triggerAffirmation() {
        viewModelScope.launch {
            val currentPersonality = personality.first()
            val affirmationText = generateAffirmation(currentPersonality)
            _affirmation.value = affirmationText
            speak(affirmationText)
        }
    }

    fun clearAffirmation() {
        _affirmation.value = null
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
        tts.shutdown()
    }

    private fun generateGreeting(personality: Personality, name: String): String {
        val greetings = when (personality) {
            Personality.CARING -> listOf(
                "Hey, $name! ✨",
                "Glad to see you, $name!",
                "Hope you're having a great day, $name!",
                "Hi, bestie! Let's get this bread!",
                "Welcome back, $name!",
                "Hey you! Let's do this!"
            )
            Personality.SARCASTIC -> listOf(
                "Oh, it's $name. Try not to forget your pills...",
                "Look who decided to show up.",
                "$name. Don't mess it up.",
                "Just a friendly reminder that I'm not your mom.",
                "Your pills won't take themselves, you know.",
                "Let's get this over with."
            )
            Personality.CHAOTIC -> listOf(
                "Alright, $name, let's do this! PILL TIME!",
                "LETS GOOO, $name! Time to adult!",
                "$name, assemble! Your pills need you.",
                "It's pill-o-clock, bestie!",
                "Time to get this party started!",
                "Chaotic energy, assemble!"
            )
        }
        return greetings.random()
    }

    private fun generateAffirmation(personality: Personality): String {
        val affirmations = when (personality) {
            Personality.CARING -> listOf(
                "Great job taking care of yourself! ✨",
                "You're doing amazing, sweetie!",
                "Proud of you!",
                "You've got this!",
                "One step at a time. You're doing great!",
                "Killing it!",
                "Slay!",
                "You're a star!",
                "Pop off, bestie!",
                "You're the best!"
            )
            Personality.SARCASTIC -> listOf(
                "Wow, you actually remembered. I'm shocked.",
                "Don't get used to this praise.",
                "You did the bare minimum. Congrats.",
                "I'm not impressed, but good job.",
                "You're not a total failure, I guess.",
                "I'm still judging you, but you did it.",
                "You're lucky I'm here to remind you.",
                "Don't expect a medal.",
                "You're not as useless as I thought.",
                "I'm still not your friend."
            )
            Personality.CHAOTIC -> listOf(
                "PILL TIME! LET'S GOOOO! 🎉",
                "CHAOS REIGNS!",
                "UNLEASH THE BEAST!",
                "YOU'RE A SUPERSTAR!",
                "LET'S GET WEIRD!",
                "POWER UP!",
                "YOU'RE A LEGEND!",
                "THIS IS YOUR MOMENT!",
                "YOU'RE A CHAOTIC ICON!",
                "GO, GO, GO!"
            )
        }
        return affirmations.random()
    }
}
