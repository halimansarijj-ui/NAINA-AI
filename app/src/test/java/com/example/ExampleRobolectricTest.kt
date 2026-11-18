package com.example

import android.content.Context
import android.speech.SpeechRecognizer
import androidx.test.core.app.ApplicationProvider
import com.example.data.NainaLanguage
import com.example.data.VoiceSettings
import com.example.voice.NainaSpeechRecognizer
import com.example.voice.SpeechUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app_name matches Naina AI`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Naina AI", appName)
    }

    @Test
    fun `default voice settings use hi-IN Hindi`() {
        val defaults = VoiceSettings()
        assertEquals(NainaLanguage.HINDI, defaults.language)
        assertEquals("hi-IN", defaults.language.speechRecognizerLocale)
        assertEquals("ur-PK", NainaLanguage.URDU.speechRecognizerLocale)
        assertEquals("en-IN", NainaLanguage.ENGLISH.speechRecognizerLocale)
    }

    @Test
    fun `speech recognizer handles unavailable service gracefully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recognizer = NainaSpeechRecognizer(context = context, onFinalResult = {})
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            assertFalse(recognizer.isAvailable())
            recognizer.startListening("hi-IN")
            assertTrue(recognizer.state.value is SpeechUiState.Error)
        }
        recognizer.destroy()
        assertEquals(SpeechUiState.Idle, recognizer.state.value)
    }
}
