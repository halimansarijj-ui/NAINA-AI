package com.example

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.auth.attemptSilentGoogleSignIn
import com.example.auth.authStateFlow
import com.example.auth.launchInteractiveGoogleSignIn
import com.example.auth.signOutGoogle
import com.example.data.CreationKind
import com.example.system.SystemActions
import com.example.ui.NainaViewModel
import com.example.ui.components.NainaBottomBar
import com.example.ui.navigation.NainaRoutes
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreatorPromptsScreen
import com.example.ui.screens.GameBuilderScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImageStudioScreen
import com.example.ui.screens.KotlinCodeGeneratorScreen
import com.example.ui.screens.MoreAndSettingsScreen
import com.example.ui.screens.PermissionConsentDialog
import com.example.ui.screens.ProCapabilitiesDialog
import com.example.ui.screens.SavedCreationsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VoiceSettingsBottomSheet
import com.example.ui.screens.WebAndAppBuilderScreen
import com.example.ui.theme.NainaAiTheme
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.ObsidianVoid
import com.example.voice.SpeechUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(ObsidianVoid.toArgb())
        )

        setContent {
            NainaAiTheme {
                NainaAuthGate()
            }
        }
    }
}

private enum class GalleryTarget {
    CHAT_VISION,
    IMAGE_STUDIO_EDIT
}

private enum class PendingConsentAction {
    NONE,
    MICROPHONE,
    CAMERA
}

@Composable
private fun NainaAuthGate() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val authUser by Firebase.auth.authStateFlow().collectAsStateWithLifecycle(
        initialValue = Firebase.auth.currentUser
    )

    var silentChecked by rememberSaveable { mutableStateOf(Firebase.auth.currentUser != null) }
    var isSigningIn by rememberSaveable { mutableStateOf(false) }
    var authError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!silentChecked) {
            attemptSilentGoogleSignIn(
                context = context,
                credentialManager = credentialManager,
                scope = scope,
                onSuccess = { silentChecked = true },
                onDone = { silentChecked = true }
            )
        }
    }

    val currentUser = authUser
    when {
        currentUser == null && !silentChecked -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianVoid),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonCrimson)
            }
        }

        currentUser == null -> {
            AuthScreen(
                isSigningIn = isSigningIn,
                errorMessage = authError,
                onGoogleSignInClick = {
                    if (isSigningIn) return@AuthScreen
                    isSigningIn = true
                    authError = null
                    launchInteractiveGoogleSignIn(
                        context = context,
                        credentialManager = credentialManager,
                        scope = scope,
                        onSuccess = {
                            isSigningIn = false
                            authError = null
                        },
                        onCancelled = {
                            isSigningIn = false
                        },
                        onError = { message ->
                            isSigningIn = false
                            authError = message
                        }
                    )
                }
            )
        }

        else -> {
            key(currentUser.uid) {
                val viewModel: NainaViewModel = viewModel(
                    key = currentUser.uid,
                    factory = NainaViewModel.provideFactory(currentUser.uid)
                )
                LaunchedEffect(currentUser.uid) {
                    viewModel.syncSignedInUser(currentUser)
                }
                NainaAppRoot(
                    firebaseUser = currentUser,
                    viewModel = viewModel,
                    onSignOut = {
                        signOutGoogle(
                            credentialManager = credentialManager,
                            scope = scope,
                            onComplete = { }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun NainaAppRoot(
    firebaseUser: FirebaseUser,
    viewModel: NainaViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NainaRoutes.ROUTE_HOME

    val voiceSettings by viewModel.voiceSettings.collectAsStateWithLifecycle()
    val speechState by viewModel.speechState.collectAsStateWithLifecycle()
    val availableVoices by viewModel.availableVoices.collectAsStateWithLifecycle()
    val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val savedCreations by viewModel.savedCreations.collectAsStateWithLifecycle()

    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatPendingImage by viewModel.chatPendingImage.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val imageStudioState by viewModel.imageStudioState.collectAsStateWithLifecycle()
    val imageStudioReference by viewModel.imageStudioReference.collectAsStateWithLifecycle()
    val imageStudioInitialPrompt by viewModel.imageStudioInitialPrompt.collectAsStateWithLifecycle()
    val webBuilderState by viewModel.webBuilderState.collectAsStateWithLifecycle()
    val gameBuilderState by viewModel.gameBuilderState.collectAsStateWithLifecycle()
    val kotlinGenState by viewModel.kotlinGenState.collectAsStateWithLifecycle()
    val creatorPackState by viewModel.creatorPackState.collectAsStateWithLifecycle()
    val cloudSyncMessage by viewModel.cloudSyncMessage.collectAsStateWithLifecycle()

    var showVoiceSheet by rememberSaveable { mutableStateOf(false) }
    var showProDialog by rememberSaveable { mutableStateOf(false) }
    var galleryTarget by remember { mutableStateOf(GalleryTarget.CHAT_VISION) }
    var pendingConsent by rememberSaveable { mutableStateOf(PendingConsentAction.NONE) }

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun showPermissionDeniedSnackbar(message: String) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Settings",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                SystemActions.openAppSettings(context, ::showMessage)
            }
        }
    }

    LaunchedEffect(speechState) {
        val error = speechState as? SpeechUiState.Error ?: return@LaunchedEffect
        showMessage(error.message)
        viewModel.clearSpeechError()
    }

    LaunchedEffect(cloudSyncMessage) {
        val msg = cloudSyncMessage ?: return@LaunchedEffect
        showMessage(msg)
        viewModel.clearCloudMessage()
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (!viewModel.isSpeechRecognitionAvailable()) {
                showMessage(context.getString(R.string.speech_not_available))
            } else {
                viewModel.startVoiceListening()
            }
        } else {
            showPermissionDeniedSnackbar(context.getString(R.string.mic_permission_denied))
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = decodeUriToBitmap(context, uri)
            if (bitmap != null) {
                when (galleryTarget) {
                    GalleryTarget.CHAT_VISION -> {
                        viewModel.attachChatImage(bitmap)
                        if (currentRoute != NainaRoutes.ROUTE_CHAT) {
                            navController.navigate(NainaRoutes.ROUTE_CHAT) {
                                launchSingleTop = true
                            }
                        }
                    }
                    GalleryTarget.IMAGE_STUDIO_EDIT -> {
                        viewModel.setImageStudioReference(bitmap)
                    }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.attachChatImage(bitmap)
            if (currentRoute != NainaRoutes.ROUTE_CHAT) {
                navController.navigate(NainaRoutes.ROUTE_CHAT) {
                    launchSingleTop = true
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                cameraLauncher.launch(null)
            } catch (_: ActivityNotFoundException) {
                showMessage(context.getString(R.string.app_not_installed))
            }
        } else {
            showPermissionDeniedSnackbar(context.getString(R.string.camera_permission_denied))
        }
    }

    fun startVoiceRecognition() {
        if (speechState is SpeechUiState.Listening) {
            viewModel.stopVoiceListening()
            return
        }
        viewModel.stopSpeaking()
        val hasMic = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasMic) {
            if (!viewModel.isSpeechRecognitionAvailable()) {
                showMessage(context.getString(R.string.speech_not_available))
            } else {
                viewModel.startVoiceListening()
            }
        } else {
            pendingConsent = PendingConsentAction.MICROPHONE
        }
    }

    fun launchCamera() {
        val hasCamera = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCamera) {
            try {
                cameraLauncher.launch(null)
            } catch (_: ActivityNotFoundException) {
                showMessage(context.getString(R.string.app_not_installed))
            }
        } else {
            pendingConsent = PendingConsentAction.CAMERA
        }
    }

    fun launchGallery(target: GalleryTarget) {
        galleryTarget = target
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    fun shareNaina() {
        SystemActions.shareText(
            context = context,
            subject = "Naina AI Assistant",
            text = "Naina AI Assistant: ${homeState.nainaReply}"
        )
    }

    fun navigateTopTab(route: String) {
        if (currentRoute == route) return
        navController.navigate(route) {
            popUpTo(NainaRoutes.ROUTE_HOME) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val showBottomBar = currentRoute in setOf(
        NainaRoutes.ROUTE_HOME,
        NainaRoutes.ROUTE_TOOLS,
        NainaRoutes.ROUTE_MORE
    )

    Scaffold(
        containerColor = ObsidianVoid,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = NainaRoutes.ROUTE_HOME,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(NainaRoutes.ROUTE_HOME) {
                    HomeScreen(
                        userPhotoUrl = userProfile?.photoUrl ?: firebaseUser.photoUrl?.toString(),
                        homeState = homeState,
                        speechState = speechState,
                        voiceSettings = voiceSettings,
                        isSpeaking = isSpeaking,
                        onDraftChange = viewModel::updateHomeDraft,
                        onSendPrompt = { viewModel.sendHomePrompt() },
                        onMicClick = ::startVoiceRecognition,
                        onStopListeningClick = viewModel::stopVoiceListening,
                        onStopSpeaking = viewModel::stopSpeaking,
                        onSpeakReply = viewModel::speakText,
                        onOpenChat = { navigateTopTab(NainaRoutes.ROUTE_CHAT) },
                        onOpenImageStudio = {
                            navController.navigate(NainaRoutes.ROUTE_IMAGE_STUDIO) {
                                launchSingleTop = true
                            }
                        },
                        onOpenWebBuilder = {
                            navController.navigate(NainaRoutes.ROUTE_WEB_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onOpenGameBuilder = {
                            navController.navigate(NainaRoutes.ROUTE_GAME_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onOpenCamera = ::launchCamera,
                        onOpenGallery = { launchGallery(GalleryTarget.CHAT_VISION) },
                        onOpenYouTube = { SystemActions.openYouTube(context, ::showMessage) },
                        onOpenInstagram = { SystemActions.openInstagram(context, ::showMessage) },
                        onShare = ::shareNaina,
                        onCycleLanguage = viewModel::cycleLanguage,
                        onCycleVoiceStyle = viewModel::cycleVoiceStyle,
                        onCycleSpeed = viewModel::cycleSpeedPreset,
                        onCyclePitch = viewModel::cyclePitchPreset,
                        onCycleVolume = viewModel::cycleVolumePreset,
                        onOpenVoiceSettings = { showVoiceSheet = true },
                        onOpenAccountSettings = { navigateTopTab(NainaRoutes.ROUTE_MORE) },
                        onOpenTools = { navigateTopTab(NainaRoutes.ROUTE_TOOLS) },
                        onOpenProInfo = { showProDialog = true }
                    )
                }

                composable(NainaRoutes.ROUTE_CHAT) {
                    ChatScreen(
                        messages = chatMessages,
                        pendingImage = chatPendingImage,
                        speechState = speechState,
                        activeLanguageLabel = voiceSettings.language.shortLabel,
                        isLoading = isChatLoading,
                        isSpeaking = isSpeaking,
                        voiceEnabled = voiceSettings.voiceEnabled,
                        onBackClick = { navController.popBackStack() },
                        onSendMessage = viewModel::sendChatMessage,
                        onMicClick = ::startVoiceRecognition,
                        onStopListeningClick = viewModel::stopVoiceListening,
                        onPickImage = { launchGallery(GalleryTarget.CHAT_VISION) },
                        onTakePhoto = ::launchCamera,
                        onClearPendingImage = { viewModel.attachChatImage(null) },
                        onToggleVoice = { viewModel.setVoiceEnabled(!voiceSettings.voiceEnabled) },
                        onSpeakMessage = viewModel::speakText,
                        onStopSpeaking = viewModel::stopSpeaking,
                        onClearChat = viewModel::clearChat
                    )
                }

                composable(NainaRoutes.ROUTE_IMAGE_STUDIO) {
                    ImageStudioScreen(
                        uiState = imageStudioState,
                        referenceBitmap = imageStudioReference,
                        initialPrompt = imageStudioInitialPrompt,
                        onConsumeInitialPrompt = { viewModel.seedImageStudioPrompt("") },
                        onBackClick = { navController.popBackStack() },
                        onPickReferenceImage = { launchGallery(GalleryTarget.IMAGE_STUDIO_EDIT) },
                        onClearReferenceImage = { viewModel.setImageStudioReference(null) },
                        onGenerateImage = viewModel::generateImage
                    )
                }

                composable(NainaRoutes.ROUTE_WEB_BUILDER) {
                    WebAndAppBuilderScreen(
                        uiState = webBuilderState,
                        onBackClick = { navController.popBackStack() },
                        onBuildProject = viewModel::buildWebsiteOrApp,
                        onUpdateHtmlCode = viewModel::updateWebsiteHtmlCode,
                        onSaveToCloud = { title, prompt, html ->
                            viewModel.saveCreationToCloud(CreationKind.WEBSITE, title, prompt, html)
                        }
                    )
                }

                composable(NainaRoutes.ROUTE_GAME_BUILDER) {
                    GameBuilderScreen(
                        uiState = gameBuilderState,
                        onBackClick = { navController.popBackStack() },
                        onBuildGame = viewModel::buildPlayableGame,
                        onUpdateHtmlCode = viewModel::updateGameHtmlCode,
                        onSaveToCloud = { title, prompt, html ->
                            viewModel.saveCreationToCloud(CreationKind.GAME, title, prompt, html)
                        }
                    )
                }

                composable(NainaRoutes.ROUTE_KOTLIN_BUILDER) {
                    KotlinCodeGeneratorScreen(
                        uiState = kotlinGenState,
                        onBackClick = { navController.popBackStack() },
                        onGenerateKotlin = viewModel::generateKotlinCode,
                        onSaveToCloud = { title, prompt, code ->
                            viewModel.saveCreationToCloud(CreationKind.KOTLIN, title, prompt, code)
                        }
                    )
                }

                composable(NainaRoutes.ROUTE_CREATOR_PROMPTS) {
                    CreatorPromptsScreen(
                        uiState = creatorPackState,
                        onBackClick = { navController.popBackStack() },
                        onGeneratePrompts = viewModel::generateCreatorPrompts,
                        onSendToImageStudio = { thumbnailPrompt ->
                            viewModel.seedImageStudioPrompt(thumbnailPrompt)
                            navController.navigate(NainaRoutes.ROUTE_IMAGE_STUDIO) {
                                launchSingleTop = true
                            }
                        },
                        onSaveToCloud = { title, prompt, content ->
                            viewModel.saveCreationToCloud(CreationKind.PROMPT, title, prompt, content)
                        }
                    )
                }

                composable(NainaRoutes.ROUTE_SAVED_CREATIONS) {
                    SavedCreationsScreen(
                        uiState = savedCreations,
                        onBackClick = { navController.popBackStack() },
                        onDeleteCreation = viewModel::deleteCreationFromCloud
                    )
                }

                composable(NainaRoutes.ROUTE_TOOLS) {
                    ToolsScreen(
                        onBackClick = { navigateTopTab(NainaRoutes.ROUTE_HOME) },
                        onOpenChat = { navigateTopTab(NainaRoutes.ROUTE_CHAT) },
                        onMicClick = ::startVoiceRecognition,
                        onOpenImageStudio = {
                            navController.navigate(NainaRoutes.ROUTE_IMAGE_STUDIO) {
                                launchSingleTop = true
                            }
                        },
                        onOpenWebBuilder = {
                            navController.navigate(NainaRoutes.ROUTE_WEB_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onOpenGameBuilder = {
                            navController.navigate(NainaRoutes.ROUTE_GAME_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onOpenKotlinBuilder = {
                            navController.navigate(NainaRoutes.ROUTE_KOTLIN_BUILDER) {
                                launchSingleTop = true
                            }
                        },
                        onOpenCreatorPrompts = {
                            navController.navigate(NainaRoutes.ROUTE_CREATOR_PROMPTS) {
                                launchSingleTop = true
                            }
                        },
                        onOpenSavedCreations = {
                            navController.navigate(NainaRoutes.ROUTE_SAVED_CREATIONS) {
                                launchSingleTop = true
                            }
                        },
                        onOpenCamera = ::launchCamera,
                        onOpenGallery = { launchGallery(GalleryTarget.CHAT_VISION) },
                        onOpenYouTube = { SystemActions.openYouTube(context, ::showMessage) },
                        onOpenInstagram = { SystemActions.openInstagram(context, ::showMessage) },
                        onOpenWhatsApp = { SystemActions.openWhatsApp(context, ::showMessage) },
                        onOpenBrowser = { SystemActions.openBrowser(context, onMissing = ::showMessage) },
                        onOpenDeviceSettings = { SystemActions.openDeviceSettings(context, ::showMessage) },
                        onShare = ::shareNaina
                    )
                }

                composable(NainaRoutes.ROUTE_MORE) {
                    MoreAndSettingsScreen(
                        userProfile = userProfile,
                        firebaseUserDisplayName = firebaseUser.displayName,
                        firebaseUserEmail = firebaseUser.email,
                        firebaseUserPhotoUrl = firebaseUser.photoUrl?.toString(),
                        voiceSettings = voiceSettings,
                        availableVoices = availableVoices,
                        voiceStatusMessage = voiceStatusMessage,
                        isSpeaking = isSpeaking,
                        onBackClick = { navigateTopTab(NainaRoutes.ROUTE_HOME) },
                        onSelectLanguage = viewModel::setLanguage,
                        onSelectVoiceStyle = viewModel::setVoiceStyle,
                        onSelectTtsVoice = viewModel::selectTtsVoiceName,
                        onVoiceEnabledChange = viewModel::setVoiceEnabled,
                        onSpeedChange = viewModel::setSpeed,
                        onPitchChange = viewModel::setPitch,
                        onVolumeChange = viewModel::setVolume,
                        onTestVoice = viewModel::testVoice,
                        onStopVoice = viewModel::stopSpeaking,
                        onOpenTtsSettings = { SystemActions.openTtsSettings(context, ::showMessage) },
                        onOpenAppSettings = { SystemActions.openAppSettings(context, ::showMessage) },
                        onOpenSavedCreations = {
                            navController.navigate(NainaRoutes.ROUTE_SAVED_CREATIONS) {
                                launchSingleTop = true
                            }
                        },
                        onSignOutClick = {
                            viewModel.stopSpeaking()
                            viewModel.stopVoiceListening()
                            onSignOut()
                        },
                        onOpenProInfo = { showProDialog = true }
                    )
                }
            }

            if (showBottomBar) {
                NainaBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = ::navigateTopTab,
                    onNainaCenterMicClick = ::startVoiceRecognition,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (showVoiceSheet) {
        VoiceSettingsBottomSheet(
            voiceSettings = voiceSettings,
            availableVoices = availableVoices,
            voiceStatusMessage = voiceStatusMessage,
            isSpeaking = isSpeaking,
            onDismiss = { showVoiceSheet = false },
            onSelectLanguage = viewModel::setLanguage,
            onSelectVoiceStyle = viewModel::setVoiceStyle,
            onSelectTtsVoice = viewModel::selectTtsVoiceName,
            onVoiceEnabledChange = viewModel::setVoiceEnabled,
            onSpeedChange = viewModel::setSpeed,
            onPitchChange = viewModel::setPitch,
            onVolumeChange = viewModel::setVolume,
            onTestVoice = viewModel::testVoice,
            onStopVoice = viewModel::stopSpeaking,
            onOpenTtsSettings = { SystemActions.openTtsSettings(context, ::showMessage) }
        )
    }

    if (pendingConsent != PendingConsentAction.NONE) {
        val isMic = pendingConsent == PendingConsentAction.MICROPHONE
        PermissionConsentDialog(
            title = if (isMic) "Microphone Access Consent" else "Camera Access Consent",
            rationale = if (isMic) {
                "Naina AI uses your microphone only while you are actively speaking so SpeechRecognizer can transcribe Hindi, Urdu, or English voice commands."
            } else {
                "Naina AI uses your camera only when you choose to snap a photo for Gemini Vision analysis."
            },
            onConfirm = {
                val action = pendingConsent
                pendingConsent = PendingConsentAction.NONE
                when (action) {
                    PendingConsentAction.MICROPHONE -> micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    PendingConsentAction.CAMERA -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    PendingConsentAction.NONE -> Unit
                }
            },
            onDismiss = { pendingConsent = PendingConsentAction.NONE }
        )
    }

    if (showProDialog) {
        ProCapabilitiesDialog(onDismiss = { showProDialog = false })
    }
}

@Suppress("DEPRECATION")
private fun decodeUriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (_: Exception) {
        null
    }
}
