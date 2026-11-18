package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.NainaLanguage
import com.example.data.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "NainaTtsController"

data class TtsVoiceOption(
    val name: String,
    val displayLabel: String,
    val localeTag: String,
    val isFemaleHint: Boolean,
    val requiresNetwork: Boolean
)

class NainaTtsController(context: Context) {

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<TtsVoiceOption>>(emptyList())
    val availableVoices: StateFlow<List<TtsVoiceOption>> = _availableVoices.asStateFlow()

    private val _voiceStatusMessage = MutableStateFlow<String?>(null)
    val voiceStatusMessage: StateFlow<String?> = _voiceStatusMessage.asStateFlow()

    private var tts: TextToSpeech? = null
    private var installedVoiceObjects: List<Voice> = emptyList()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _isReady.value = true
                refreshInstalledVoices()
                applyDefaultHindiVoice()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                Log.w(TAG, "TextToSpeech initialization failed with status=$status")
                _voiceStatusMessage.value =
                    "Text-to-Speech engine could not initialize on this device."
            }
        }
    }

    private fun refreshInstalledVoices() {
        val engine = tts ?: return
        val rawVoices = try {
            engine.voices?.toList().orEmpty()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query TTS voices", e)
            emptyList()
        }

        installedVoiceObjects = rawVoices
        val relevant = rawVoices
            .filter { voice ->
                val lang = voice.locale.language.lowercase(Locale.ROOT)
                lang in setOf("hi", "ur", "en")
            }
            .sortedWith(
                compareByDescending<Voice> { isLikelyFemaleVoice(it) }
                    .thenBy { it.isNetworkConnectionRequired }
                    .thenBy { it.locale.language }
                    .thenBy { it.name }
            )

        _availableVoices.value = relevant.map { voice ->
            val female = isLikelyFemaleVoice(voice)
            val langLabel = when (voice.locale.language.lowercase(Locale.ROOT)) {
                "hi" -> "Hindi"
                "ur" -> "Urdu"
                "en" -> "English (${voice.locale.country.ifBlank { "Global" }})"
                else -> voice.locale.displayLanguage
            }
            val tag = if (female) "Female" else "Voice"
            val mode = if (voice.isNetworkConnectionRequired) "Online" else "Offline"
            val shortCode = voice.name.substringAfterLast("-").take(8)
            TtsVoiceOption(
                name = voice.name,
                displayLabel = "$langLabel • $tag ($shortCode, $mode)",
                localeTag = voice.locale.toLanguageTag(),
                isFemaleHint = female,
                requiresNetwork = voice.isNetworkConnectionRequired
            )
        }
    }

    private fun applyDefaultHindiVoice() {
        val engine = tts ?: return
        val hindiBest = pickBestFemaleVoiceForLanguage(NainaLanguage.HINDI)
        if (hindiBest != null) {
            engine.voice = hindiBest
        } else {
            engine.language = Locale.forLanguageTag("hi-IN")
        }
    }

    fun speak(text: String, settings: VoiceSettings) {
        val engine = tts ?: return
        if (!_isReady.value) {
            _voiceStatusMessage.value = "Voice engine is still starting up. Please try again."
            return
        }
        val cleanText = text
            .replace(Regex("```[\\s\\S]*?```"), "Code block generated.")
            .replace(Regex("[*_`#>]"), "")
            .trim()
        if (cleanText.isBlank()) return

        _voiceStatusMessage.value = null

        val explicitVoice = settings.selectedVoiceName?.let { name ->
            installedVoiceObjects.firstOrNull { it.name == name }
        }

        val chosenVoice = explicitVoice ?: pickBestFemaleVoiceForLanguage(settings.language)
        if (chosenVoice != null) {
            val voiceResult = engine.setVoice(chosenVoice)
            if (voiceResult == TextToSpeech.ERROR) {
                fallbackToLocale(engine, settings.language)
            }
        } else {
            fallbackToLocale(engine, settings.language)
        }

        engine.setSpeechRate(settings.speed.coerceIn(0.5f, 2.0f))
        val effectivePitch = (settings.pitch * settings.voiceStyle.pitchMultiplier)
            .coerceIn(0.5f, 2.0f)
        engine.setPitch(effectivePitch)

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume.coerceIn(0f, 1.0f))
        }
        engine.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            params,
            "naina_${System.currentTimeMillis()}"
        )
    }

    private fun fallbackToLocale(engine: TextToSpeech, language: NainaLanguage) {
        val locale = Locale.forLanguageTag(language.ttsLanguageTag)
        val result = engine.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            _voiceStatusMessage.value =
                "Installed ${language.shortLabel} voice pack is unavailable on this device. Falling back to default voice — install ${language.shortLabel} voice data in Android TTS Settings."
            engine.language = Locale.getDefault()
        }
    }

    private fun pickBestFemaleVoiceForLanguage(language: NainaLanguage): Voice? {
        val targetLang = when (language) {
            NainaLanguage.HINDI, NainaLanguage.HINGLISH -> "hi"
            NainaLanguage.URDU -> "ur"
            NainaLanguage.ENGLISH -> "en"
        }
        val matching = installedVoiceObjects.filter {
            it.locale.language.equals(targetLang, ignoreCase = true)
        }
        if (matching.isEmpty()) return null

        val femaleMatches = matching.filter { isLikelyFemaleVoice(it) }
        val pool = femaleMatches.ifEmpty { matching }

        // Prefer offline high quality, matching country (IN / PK), then higher quality score
        return pool.sortedWith(
            compareByDescending<Voice> {
                val c = it.locale.country.uppercase(Locale.ROOT)
                (targetLang == "hi" && c == "IN") ||
                    (targetLang == "en" && c == "IN") ||
                    (targetLang == "ur" && (c == "PK" || c == "IN"))
            }
                .thenBy { it.isNetworkConnectionRequired }
                .thenByDescending { it.quality }
        ).firstOrNull()
    }

    private fun isLikelyFemaleVoice(voice: Voice): Boolean {
        val lowerName = voice.name.lowercase(Locale.ROOT)
        val featuresText = voice.features?.joinToString(" ")?.lowercase(Locale.ROOT).orEmpty()
        if (lowerName.contains("female") || featuresText.contains("female")) return true
        // Common Google TTS female voice IDs for hi-IN, ur-PK, en-IN, en-US
        val femaleTokens = listOf("hie", "hia", "hid", "ene", "ena", "enc", "sfg", "tpf", "iob", "iol", "ur-pk-x-urb", "hi-in-x-hia", "hi-in-x-hic", "en-in-x-ena", "en-in-x-enc")
        return femaleTokens.any { lowerName.contains(it) }
    }

    fun clearStatusMessage() {
        _voiceStatusMessage.value = null
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isSpeaking.value = false
    }
}
