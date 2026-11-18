package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.voiceSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "naina_voice_settings"
)

enum class NainaLanguage(
    val displayName: String,
    val shortLabel: String,
    val speechRecognizerLocale: String,
    val ttsLanguageTag: String,
    val promptInstruction: String
) {
    HINDI(
        displayName = "हिन्दी (Hindi)",
        shortLabel = "Hindi",
        speechRecognizerLocale = "hi-IN",
        ttsLanguageTag = "hi-IN",
        promptInstruction = "Reply naturally in warm, conversational Hindi (Devanagari script) unless the user explicitly writes in another language."
    ),
    URDU(
        displayName = "اردو (Urdu)",
        shortLabel = "Urdu",
        speechRecognizerLocale = "ur-PK",
        ttsLanguageTag = "ur-PK",
        promptInstruction = "Reply naturally in polite, conversational Urdu (Nastaliq/Arabic script) unless the user explicitly writes in another language."
    ),
    HINGLISH(
        displayName = "Hinglish",
        shortLabel = "Hinglish",
        speechRecognizerLocale = "hi-IN",
        ttsLanguageTag = "hi-IN",
        promptInstruction = "Reply in friendly conversational Hinglish (natural mix of Hindi and English in Latin script)."
    ),
    ENGLISH(
        displayName = "English (India)",
        shortLabel = "English",
        speechRecognizerLocale = "en-IN",
        ttsLanguageTag = "en-IN",
        promptInstruction = "Reply naturally in warm, concise, helpful English."
    )
}

enum class NainaVoiceStyle(
    val displayName: String,
    val shortLabel: String,
    val pitchMultiplier: Float
) {
    FEMALE_WARM("Female (Warm)", "Female", 1.0f),
    FEMALE_SWEET("Female (Bright)", "Bright", 1.12f),
    CALM_STUDIO("Female (Calm)", "Calm", 0.94f)
}

data class VoiceSettings(
    val language: NainaLanguage = NainaLanguage.HINDI,
    val voiceStyle: NainaVoiceStyle = NainaVoiceStyle.FEMALE_WARM,
    val selectedVoiceName: String? = null,
    val voiceEnabled: Boolean = true,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val volume: Float = 0.8f
)

class VoiceSettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val VOICE_STYLE = stringPreferencesKey("voice_style")
        val SELECTED_VOICE_NAME = stringPreferencesKey("selected_voice_name")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val SPEED = floatPreferencesKey("speed")
        val PITCH = floatPreferencesKey("pitch")
        val VOLUME = floatPreferencesKey("volume")
    }

    val settingsFlow: Flow<VoiceSettings> = context.voiceSettingsDataStore.data.map { prefs ->
        val language = prefs[Keys.LANGUAGE]?.let { name ->
            NainaLanguage.entries.firstOrNull { it.name == name }
        } ?: NainaLanguage.HINDI

        val voiceStyle = prefs[Keys.VOICE_STYLE]?.let { name ->
            NainaVoiceStyle.entries.firstOrNull { it.name == name }
        } ?: NainaVoiceStyle.FEMALE_WARM

        VoiceSettings(
            language = language,
            voiceStyle = voiceStyle,
            selectedVoiceName = prefs[Keys.SELECTED_VOICE_NAME],
            voiceEnabled = prefs[Keys.VOICE_ENABLED] ?: true,
            speed = (prefs[Keys.SPEED] ?: 1.0f).coerceIn(0.5f, 2.0f),
            pitch = (prefs[Keys.PITCH] ?: 1.0f).coerceIn(0.5f, 2.0f),
            volume = (prefs[Keys.VOLUME] ?: 0.8f).coerceIn(0f, 1.0f)
        )
    }

    suspend fun setLanguage(language: NainaLanguage) {
        context.voiceSettingsDataStore.edit {
            it[Keys.LANGUAGE] = language.name
            // Reset explicit voice override when switching languages so best female voice auto-selects
            it.remove(Keys.SELECTED_VOICE_NAME)
        }
    }

    suspend fun setVoiceStyle(style: NainaVoiceStyle) {
        context.voiceSettingsDataStore.edit { it[Keys.VOICE_STYLE] = style.name }
    }

    suspend fun setSelectedVoiceName(voiceName: String?) {
        context.voiceSettingsDataStore.edit {
            if (voiceName.isNullOrBlank()) {
                it.remove(Keys.SELECTED_VOICE_NAME)
            } else {
                it[Keys.SELECTED_VOICE_NAME] = voiceName
            }
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.voiceSettingsDataStore.edit { it[Keys.VOICE_ENABLED] = enabled }
    }

    suspend fun setSpeed(speed: Float) {
        context.voiceSettingsDataStore.edit { it[Keys.SPEED] = speed.coerceIn(0.5f, 2.0f) }
    }

    suspend fun setPitch(pitch: Float) {
        context.voiceSettingsDataStore.edit { it[Keys.PITCH] = pitch.coerceIn(0.5f, 2.0f) }
    }

    suspend fun setVolume(volume: Float) {
        context.voiceSettingsDataStore.edit { it[Keys.VOLUME] = volume.coerceIn(0f, 1.0f) }
    }
}
