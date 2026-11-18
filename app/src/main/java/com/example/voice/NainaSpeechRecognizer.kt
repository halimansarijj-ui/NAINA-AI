package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "NainaSpeechRecognizer"

sealed interface SpeechUiState {
    data object Idle : SpeechUiState
    data class Listening(
        val readyForSpeech: Boolean = false,
        val isSpeaking: Boolean = false,
        val rmsLevel: Float = 0f,
        val partialText: String = ""
    ) : SpeechUiState
    data class Error(val message: String) : SpeechUiState
}

class NainaSpeechRecognizer(
    private val context: Context,
    private val onFinalResult: (String) -> Unit
) {
    private val _state = MutableStateFlow<SpeechUiState>(SpeechUiState.Idle)
    val state: StateFlow<SpeechUiState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(languageTag: String = "hi-IN") {
        if (!isAvailable()) {
            _state.value = SpeechUiState.Error(
                "Speech recognition service is not available on this device. Please install or enable Google Speech Services."
            )
            return
        }

        // Destroy any stale instance before creating a fresh recognizer session
        speechRecognizer?.destroy()
        speechRecognizer = null

        try {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d(TAG, "onReadyForSpeech")
                    _state.value = SpeechUiState.Listening(readyForSpeech = true)
                }

                override fun onBeginningOfSpeech() {
                    Log.d(TAG, "onBeginningOfSpeech")
                    val current = _state.value as? SpeechUiState.Listening
                    _state.value = (current ?: SpeechUiState.Listening(readyForSpeech = true))
                        .copy(isSpeaking = true)
                }

                override fun onRmsChanged(rmsdB: Float) {
                    val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                    val current = _state.value as? SpeechUiState.Listening ?: return
                    _state.value = current.copy(rmsLevel = normalized)
                }

                override fun onBufferReceived(buffer: ByteArray?) = Unit

                override fun onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech")
                    val current = _state.value as? SpeechUiState.Listening ?: return
                    _state.value = current.copy(isSpeaking = false, rmsLevel = 0f)
                }

                override fun onError(error: Int) {
                    val message = mapSpeechError(error)
                    Log.w(TAG, "onError code=$error ($message)")
                    _state.value = if (error == SpeechRecognizer.ERROR_CLIENT) {
                        SpeechUiState.Idle
                    } else {
                        SpeechUiState.Error(message)
                    }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val recognized = matches?.firstOrNull()?.trim().orEmpty()
                    Log.d(TAG, "onResults: $recognized")
                    _state.value = SpeechUiState.Idle
                    if (recognized.isNotBlank()) {
                        onFinalResult(recognized)
                    } else {
                        _state.value = SpeechUiState.Error("No speech detected. Please speak again.")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (partial.isNotBlank()) {
                        val current = _state.value as? SpeechUiState.Listening
                        _state.value = (current ?: SpeechUiState.Listening(readyForSpeech = true))
                            .copy(partialText = partial)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer = recognizer
            _state.value = SpeechUiState.Listening(readyForSpeech = false)
            recognizer.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start SpeechRecognizer", e)
            _state.value = SpeechUiState.Error(
                e.localizedMessage ?: "Unable to start microphone speech recognition."
            )
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "stopListening ignored", e)
        }
        val current = _state.value as? SpeechUiState.Listening
        if (current != null && current.partialText.isNotBlank()) {
            val captured = current.partialText
            _state.value = SpeechUiState.Idle
            onFinalResult(captured)
        } else {
            _state.value = SpeechUiState.Idle
        }
    }

    fun clearError() {
        if (_state.value is SpeechUiState.Error) {
            _state.value = SpeechUiState.Idle
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w(TAG, "destroy ignored", e)
        } finally {
            speechRecognizer = null
            _state.value = SpeechUiState.Idle
        }
    }

    private fun mapSpeechError(errorCode: Int): String = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Microphone audio recording error."
        SpeechRecognizer.ERROR_CLIENT -> "Speech recognition stopped."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network error during speech recognition. Check internet connection."
        SpeechRecognizer.ERROR_NO_MATCH -> "Could not understand that clearly. Please try speaking again."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy. Tap Stop and try again."
        SpeechRecognizer.ERROR_SERVER -> "Speech recognition server error. Try again shortly."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Tap the mic and speak clearly."
        SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED,
        SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "Selected voice recognition language is not downloaded on this device."
        else -> "Speech recognition failed (code $errorCode). Please try again."
    }
}
