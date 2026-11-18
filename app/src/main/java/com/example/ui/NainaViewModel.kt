package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.ai.ChatTurn
import com.example.ai.NainaAiRepository
import com.example.ai.ThumbnailAndVideoResult
import com.example.data.CreationKind
import com.example.data.FirestoreRepository
import com.example.data.NainaLanguage
import com.example.data.NainaVoiceStyle
import com.example.data.SavedCreation
import com.example.data.UserProfile
import com.example.data.VoiceSettings
import com.example.data.VoiceSettingsRepository
import com.example.voice.NainaSpeechRecognizer
import com.example.voice.NainaTtsController
import com.example.voice.SpeechUiState
import com.example.voice.TtsVoiceOption
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NainaViewModel"

data class ChatMessageUi(
    val id: Long,
    val isUser: Boolean,
    val text: String,
    val image: Bitmap? = null,
    val isStreaming: Boolean = false,
    val isError: Boolean = false
)

sealed interface ImageStudioUiState {
    data object Idle : ImageStudioUiState
    data object Loading : ImageStudioUiState
    data class Success(val bitmap: Bitmap, val prompt: String) : ImageStudioUiState
    data class Error(val message: String) : ImageStudioUiState
}

sealed interface BuilderUiState {
    data object Idle : BuilderUiState
    data object Loading : BuilderUiState
    data class Success(val htmlCode: String, val prompt: String) : BuilderUiState
    data class Error(val message: String) : BuilderUiState
}

sealed interface TextGenUiState {
    data object Idle : TextGenUiState
    data object Loading : TextGenUiState
    data class Success(val output: String, val prompt: String) : TextGenUiState
    data class Error(val message: String) : TextGenUiState
}

sealed interface CreatorPackUiState {
    data object Idle : CreatorPackUiState
    data object Loading : CreatorPackUiState
    data class Success(val result: ThumbnailAndVideoResult, val topic: String) : CreatorPackUiState
    data class Error(val message: String) : CreatorPackUiState
}

sealed interface CreationsListUiState {
    data object Loading : CreationsListUiState
    data class Success(val items: List<SavedCreation>) : CreationsListUiState
    data class Error(val message: String) : CreationsListUiState
}

data class HomeCockpitState(
    val inputDraft: String = "",
    val nainaReply: String = "नमस्ते! मैं नैना हूँ ❤️ आपका क्या काम है?",
    val isThinking: Boolean = false,
    val isError: Boolean = false
)

class NainaViewModel(
    private val currentUserId: String,
    private val aiRepository: NainaAiRepository,
    private val settingsRepository: VoiceSettingsRepository,
    private val firestoreRepository: FirestoreRepository,
    private val ttsController: NainaTtsController,
    appContext: Context
) : ViewModel() {

    private var speechRecognizer: NainaSpeechRecognizer? = null

    private val _speechState = MutableStateFlow<SpeechUiState>(SpeechUiState.Idle)
    val speechState: StateFlow<SpeechUiState> = _speechState.asStateFlow()

    init {
        val recognizer = NainaSpeechRecognizer(
            context = appContext,
            onFinalResult = { recognizedText ->
                sendHomePrompt(recognizedText, fromVoice = true)
            }
        )
        speechRecognizer = recognizer
        viewModelScope.launch {
            recognizer.state.collect { _speechState.value = it }
        }
    }

    val voiceSettings: StateFlow<VoiceSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = VoiceSettings()
        )

    val isSpeaking: StateFlow<Boolean> = ttsController.isSpeaking
    val availableVoices: StateFlow<List<TtsVoiceOption>> = ttsController.availableVoices
    val voiceStatusMessage: StateFlow<String?> = ttsController.voiceStatusMessage

    val userProfile: StateFlow<UserProfile?> = firestoreRepository.observeUserProfile(currentUserId)
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing user profile", firestoreError ?: error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    val savedCreations: StateFlow<CreationsListUiState> =
        firestoreRepository.observeSavedCreations(currentUserId)
            .map<List<SavedCreation>, CreationsListUiState> { CreationsListUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing saved creations", root)
                emit(CreationsListUiState.Error(root.message ?: "Failed to load saved creations."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = CreationsListUiState.Loading
            )

    private val _homeState = MutableStateFlow(HomeCockpitState())
    val homeState: StateFlow<HomeCockpitState> = _homeState.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessageUi(
                id = 1L,
                isUser = false,
                text = "नमस्ते! मैं नैना हूँ ❤️ मुझसे हिन्दी, اردو या English में कुछ भी पूछें — या फ़ोटो भेजें!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessageUi>> = _chatMessages.asStateFlow()

    private val _chatPendingImage = MutableStateFlow<Bitmap?>(null)
    val chatPendingImage: StateFlow<Bitmap?> = _chatPendingImage.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _imageStudioState = MutableStateFlow<ImageStudioUiState>(ImageStudioUiState.Idle)
    val imageStudioState: StateFlow<ImageStudioUiState> = _imageStudioState.asStateFlow()

    private val _imageStudioReference = MutableStateFlow<Bitmap?>(null)
    val imageStudioReference: StateFlow<Bitmap?> = _imageStudioReference.asStateFlow()

    private val _imageStudioInitialPrompt = MutableStateFlow("")
    val imageStudioInitialPrompt: StateFlow<String> = _imageStudioInitialPrompt.asStateFlow()

    private val _webBuilderState = MutableStateFlow<BuilderUiState>(BuilderUiState.Idle)
    val webBuilderState: StateFlow<BuilderUiState> = _webBuilderState.asStateFlow()

    private val _gameBuilderState = MutableStateFlow<BuilderUiState>(BuilderUiState.Idle)
    val gameBuilderState: StateFlow<BuilderUiState> = _gameBuilderState.asStateFlow()

    private val _kotlinGenState = MutableStateFlow<TextGenUiState>(TextGenUiState.Idle)
    val kotlinGenState: StateFlow<TextGenUiState> = _kotlinGenState.asStateFlow()

    private val _creatorPackState = MutableStateFlow<CreatorPackUiState>(CreatorPackUiState.Idle)
    val creatorPackState: StateFlow<CreatorPackUiState> = _creatorPackState.asStateFlow()

    private val _cloudSyncMessage = MutableStateFlow<String?>(null)
    val cloudSyncMessage: StateFlow<String?> = _cloudSyncMessage.asStateFlow()

    private var nextMessageId = 2L

    fun syncSignedInUser(user: FirebaseUser) {
        viewModelScope.launch {
            val result = firestoreRepository.syncSignedInUserProfile(user, voiceSettings.value.language)
            result.onFailure { err ->
                Log.w(TAG, "Profile sync warning: ${err.message}")
            }
        }
    }

    fun isSpeechRecognitionAvailable(): Boolean {
        return speechRecognizer?.isAvailable() == true
    }

    fun startVoiceListening() {
        ttsController.stop()
        speechRecognizer?.startListening(voiceSettings.value.language.speechRecognizerLocale)
    }

    fun stopVoiceListening() {
        speechRecognizer?.stopListening()
    }

    fun clearSpeechError() {
        speechRecognizer?.clearError()
    }

    fun updateHomeDraft(text: String) {
        _homeState.update { it.copy(inputDraft = text) }
    }

    fun sendHomePrompt(promptText: String = _homeState.value.inputDraft, fromVoice: Boolean = false) {
        val cleanPrompt = promptText.trim()
        if (cleanPrompt.isBlank()) return

        _homeState.update {
            it.copy(
                inputDraft = if (fromVoice) cleanPrompt else "",
                isThinking = true,
                isError = false,
                nainaReply = "सोच रही हूँ…"
            )
        }

        sendMessageInternal(
            userText = cleanPrompt,
            attachedImage = null,
            updateHomeBanner = true
        )
    }

    fun attachChatImage(bitmap: Bitmap?) {
        _chatPendingImage.value = bitmap
    }

    fun sendChatMessage(userText: String) {
        val attached = _chatPendingImage.value
        val trimmed = userText.trim()
        if (trimmed.isBlank() && attached == null) return
        _chatPendingImage.value = null
        sendMessageInternal(
            userText = trimmed,
            attachedImage = attached,
            updateHomeBanner = true
        )
    }

    private fun sendMessageInternal(
        userText: String,
        attachedImage: Bitmap?,
        updateHomeBanner: Boolean
    ) {
        val settings = voiceSettings.value
        val userMsgId = nextMessageId++
        val assistantMsgId = nextMessageId++

        val displayUserText = userText.ifBlank { "Analyze this image" }
        val historySnapshot = _chatMessages.value
            .filter { !it.isError && !it.isStreaming }
            .map { ChatTurn(isUser = it.isUser, text = it.text, image = it.image) }

        _chatMessages.update { current ->
            current +
                ChatMessageUi(
                    id = userMsgId,
                    isUser = true,
                    text = displayUserText,
                    image = attachedImage
                ) +
                ChatMessageUi(
                    id = assistantMsgId,
                    isUser = false,
                    text = "…",
                    isStreaming = true
                )
        }
        _isChatLoading.value = true

        viewModelScope.launch {
            var latestText = ""
            try {
                aiRepository.streamChatReply(
                    history = historySnapshot,
                    userPrompt = userText,
                    attachedImage = attachedImage,
                    language = settings.language
                ).collect { partial ->
                    latestText = partial
                    _chatMessages.update { list ->
                        list.map { msg ->
                            if (msg.id == assistantMsgId) {
                                msg.copy(text = partial, isStreaming = true)
                            } else msg
                        }
                    }
                    if (updateHomeBanner) {
                        _homeState.update {
                            it.copy(nainaReply = partial, isThinking = true, isError = false)
                        }
                    }
                }

                _chatMessages.update { list ->
                    list.map { msg ->
                        if (msg.id == assistantMsgId) msg.copy(isStreaming = false) else msg
                    }
                }
                if (updateHomeBanner) {
                    _homeState.update {
                        it.copy(nainaReply = latestText, isThinking = false, isError = false)
                    }
                }
                if (settings.voiceEnabled && latestText.isNotBlank()) {
                    ttsController.speak(latestText, settings)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat request timed out", e)
                applyChatError(assistantMsgId, "Response timed out after 60s. Please try again.", updateHomeBanner)
            } catch (e: QuotaExceededException) {
                Log.e(TAG, "Quota exceeded", e)
                applyChatError(assistantMsgId, "Gemini quota reached. Please try again shortly.", updateHomeBanner)
            } catch (e: Exception) {
                Log.e(TAG, "Chat generation failed", e)
                applyChatError(assistantMsgId, e.localizedMessage ?: "Failed to reach Naina AI.", updateHomeBanner)
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun applyChatError(assistantMsgId: Long, errorMessage: String, updateHomeBanner: Boolean) {
        _chatMessages.update { list ->
            list.map { msg ->
                if (msg.id == assistantMsgId) {
                    msg.copy(text = errorMessage, isStreaming = false, isError = true)
                } else msg
            }
        }
        if (updateHomeBanner) {
            _homeState.update {
                it.copy(nainaReply = errorMessage, isThinking = false, isError = true)
            }
        }
    }

    fun clearChat() {
        ttsController.stop()
        _chatMessages.value = listOf(
            ChatMessageUi(
                id = nextMessageId++,
                isUser = false,
                text = "नमस्ते! नई बातचीत शुरू हो गई है ❤️ बताइए, आज क्या मदद करूँ?"
            )
        )
    }

    fun setImageStudioReference(bitmap: Bitmap?) {
        _imageStudioReference.value = bitmap
    }

    fun seedImageStudioPrompt(prompt: String) {
        _imageStudioInitialPrompt.value = prompt
    }

    fun generateImage(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return
        val refBitmap = _imageStudioReference.value

        viewModelScope.launch {
            _imageStudioState.value = ImageStudioUiState.Loading
            try {
                val generated = aiRepository.generateOrEditImage(cleanPrompt, refBitmap)
                _imageStudioState.value = ImageStudioUiState.Success(generated, cleanPrompt)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _imageStudioState.value = ImageStudioUiState.Error("Image generation timed out after 60s.")
            } catch (e: QuotaExceededException) {
                _imageStudioState.value = ImageStudioUiState.Error("Image generation quota exceeded (requires Blaze plan).")
            } catch (e: Exception) {
                Log.e(TAG, "Image generation failed", e)
                _imageStudioState.value = ImageStudioUiState.Error(e.localizedMessage ?: "Image generation failed.")
            }
        }
    }

    fun buildWebsiteOrApp(prompt: String, isAppMode: Boolean) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return
        val lang = voiceSettings.value.language

        viewModelScope.launch {
            _webBuilderState.value = BuilderUiState.Loading
            try {
                val html = aiRepository.generateWebsiteOrApp(cleanPrompt, isAppMode, lang)
                _webBuilderState.value = BuilderUiState.Success(html, cleanPrompt)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _webBuilderState.value = BuilderUiState.Error("Generation timed out after 60s.")
            } catch (e: Exception) {
                Log.e(TAG, "Website/App builder failed", e)
                _webBuilderState.value = BuilderUiState.Error(e.localizedMessage ?: "Failed to build project.")
            }
        }
    }

    fun updateWebsiteHtmlCode(newHtml: String) {
        val current = _webBuilderState.value as? BuilderUiState.Success ?: return
        _webBuilderState.value = current.copy(htmlCode = newHtml)
    }

    fun buildPlayableGame(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return

        viewModelScope.launch {
            _gameBuilderState.value = BuilderUiState.Loading
            try {
                val html = aiRepository.generatePlayableGame(cleanPrompt)
                _gameBuilderState.value = BuilderUiState.Success(html, cleanPrompt)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _gameBuilderState.value = BuilderUiState.Error("Game generation timed out after 60s.")
            } catch (e: Exception) {
                Log.e(TAG, "Game builder failed", e)
                _gameBuilderState.value = BuilderUiState.Error(e.localizedMessage ?: "Failed to build game.")
            }
        }
    }

    fun updateGameHtmlCode(newHtml: String) {
        val current = _gameBuilderState.value as? BuilderUiState.Success ?: return
        _gameBuilderState.value = current.copy(htmlCode = newHtml)
    }

    fun generateKotlinCode(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return

        viewModelScope.launch {
            _kotlinGenState.value = TextGenUiState.Loading
            try {
                val code = aiRepository.generateKotlinCode(cleanPrompt)
                _kotlinGenState.value = TextGenUiState.Success(code, cleanPrompt)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _kotlinGenState.value = TextGenUiState.Error("Kotlin generation timed out after 60s.")
            } catch (e: Exception) {
                Log.e(TAG, "Kotlin codegen failed", e)
                _kotlinGenState.value = TextGenUiState.Error(e.localizedMessage ?: "Kotlin generation failed.")
            }
        }
    }

    fun generateCreatorPrompts(topic: String, platform: String) {
        val cleanTopic = topic.trim()
        if (cleanTopic.isBlank()) return

        viewModelScope.launch {
            _creatorPackState.value = CreatorPackUiState.Loading
            try {
                val result = aiRepository.generateThumbnailAndVideoPrompts(
                    topic = cleanTopic,
                    platform = platform,
                    language = voiceSettings.value.language
                )
                _creatorPackState.value = CreatorPackUiState.Success(result, cleanTopic)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _creatorPackState.value = CreatorPackUiState.Error("Prompt generation timed out after 60s.")
            } catch (e: Exception) {
                Log.e(TAG, "Creator prompt generation failed", e)
                _creatorPackState.value = CreatorPackUiState.Error(e.localizedMessage ?: "Generation failed.")
            }
        }
    }

    fun saveCreationToCloud(kind: CreationKind, title: String, prompt: String, content: String) {
        viewModelScope.launch {
            val result = firestoreRepository.saveCreation(
                userId = currentUserId,
                kind = kind,
                title = title,
                prompt = prompt,
                content = content
            )
            _cloudSyncMessage.value = if (result.isSuccess) {
                "Saved ${kind.label} to your Google Cloud Firestore!"
            } else {
                result.exceptionOrNull()?.localizedMessage ?: "Failed to save to Firestore."
            }
        }
    }

    fun deleteCreationFromCloud(creationId: String) {
        viewModelScope.launch {
            val result = firestoreRepository.deleteCreation(currentUserId, creationId)
            if (result.isFailure) {
                _cloudSyncMessage.value =
                    result.exceptionOrNull()?.localizedMessage ?: "Failed to delete item."
            }
        }
    }

    fun clearCloudMessage() {
        _cloudSyncMessage.value = null
    }

    fun clearVoiceStatusMessage() {
        ttsController.clearStatusMessage()
    }

    fun speakText(text: String) {
        ttsController.speak(text, voiceSettings.value)
    }

    fun testVoice() {
        val current = voiceSettings.value
        val sample = when (current.language) {
            NainaLanguage.HINDI -> "नमस्ते! मैं नैना हूँ, आपकी पर्सनल AI असिस्टेंट।"
            NainaLanguage.URDU -> "السلام علیکم! میں نینا ہوں، آپ کی ذاتی اے آئی اسسٹنٹ۔"
            NainaLanguage.HINGLISH -> "Hello! Main Naina hoon, aapki smart AI assistant."
            NainaLanguage.ENGLISH -> "Hello! I am Naina, your personal AI assistant."
        }
        ttsController.speak(sample, current)
    }

    fun stopSpeaking() {
        ttsController.stop()
    }

    fun setLanguage(language: NainaLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            firestoreRepository.updatePreferredLanguage(currentUserId, language)
        }
    }

    fun cycleLanguage() {
        val entries = NainaLanguage.entries
        val next = entries[(voiceSettings.value.language.ordinal + 1) % entries.size]
        setLanguage(next)
    }

    fun setVoiceStyle(style: NainaVoiceStyle) {
        viewModelScope.launch { settingsRepository.setVoiceStyle(style) }
    }

    fun cycleVoiceStyle() {
        val entries = NainaVoiceStyle.entries
        val next = entries[(voiceSettings.value.voiceStyle.ordinal + 1) % entries.size]
        setVoiceStyle(next)
    }

    fun selectTtsVoiceName(voiceName: String?) {
        viewModelScope.launch { settingsRepository.setSelectedVoiceName(voiceName) }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        if (!enabled) ttsController.stop()
        viewModelScope.launch { settingsRepository.setVoiceEnabled(enabled) }
    }

    fun setSpeed(speed: Float) {
        viewModelScope.launch { settingsRepository.setSpeed(speed) }
    }

    fun cycleSpeedPreset() {
        val presets = listOf(0.8f, 1.0f, 1.2f, 1.5f)
        val current = voiceSettings.value.speed
        val next = presets.firstOrNull { it > current + 0.05f } ?: presets.first()
        setSpeed(next)
    }

    fun setPitch(pitch: Float) {
        viewModelScope.launch { settingsRepository.setPitch(pitch) }
    }

    fun cyclePitchPreset() {
        val presets = listOf(0.85f, 1.0f, 1.15f, 1.3f)
        val current = voiceSettings.value.pitch
        val next = presets.firstOrNull { it > current + 0.05f } ?: presets.first()
        setPitch(next)
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch { settingsRepository.setVolume(volume) }
    }

    fun cycleVolumePreset() {
        val presets = listOf(0.5f, 0.8f, 1.0f)
        val current = voiceSettings.value.volume
        val next = presets.firstOrNull { it > current + 0.05f } ?: presets.first()
        setVolume(next)
    }

    override fun onCleared() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        ttsController.shutdown()
        super.onCleared()
    }

    companion object {
        fun provideFactory(currentUserId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                NainaViewModel(
                    currentUserId = currentUserId,
                    aiRepository = NainaAiRepository(),
                    settingsRepository = VoiceSettingsRepository(app),
                    firestoreRepository = FirestoreRepository(db),
                    ttsController = NainaTtsController(app),
                    appContext = app
                )
            }
        }
    }
}
