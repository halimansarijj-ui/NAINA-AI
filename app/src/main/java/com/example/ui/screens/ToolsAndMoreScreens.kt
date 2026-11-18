package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MovieCreation
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.NainaLanguage
import com.example.data.NainaVoiceStyle
import com.example.data.UserProfile
import com.example.data.VoiceSettings
import com.example.ui.components.NainaSubScreenTopBar
import com.example.ui.theme.CrimsonBorder
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DeepCrimsonNight
import com.example.ui.theme.DeepRuby
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.GlowingRuby
import com.example.ui.theme.MutedRoseText
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SoftCrimsonBorder
import com.example.ui.theme.SoftRoseText
import com.example.ui.theme.SuccessMint
import com.example.ui.theme.VelvetSurface
import com.example.voice.TtsVoiceOption
import kotlin.math.roundToInt

private data class ToolTileItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
fun ToolsScreen(
    onBackClick: () -> Unit,
    onOpenChat: () -> Unit,
    onMicClick: () -> Unit,
    onOpenImageStudio: () -> Unit,
    onOpenWebBuilder: () -> Unit,
    onOpenGameBuilder: () -> Unit,
    onOpenKotlinBuilder: () -> Unit,
    onOpenCreatorPrompts: () -> Unit,
    onOpenSavedCreations: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenYouTube: () -> Unit,
    onOpenInstagram: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onOpenBrowser: () -> Unit,
    onOpenDeviceSettings: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tools = listOf(
        ToolTileItem("AI Chat", "Hindi, Urdu & English Gemini chat", Icons.AutoMirrored.Rounded.Chat, GlowingRuby, "tool_chat", onOpenChat),
        ToolTileItem("Voice Assistant", "Live SpeechRecognizer in Hindi/Urdu/EN", Icons.Rounded.Mic, NeonCrimson, "tool_voice", onMicClick),
        ToolTileItem("Image Studio", "Generate & remix images with Nano Banana", Icons.Rounded.Palette, RoyalGold, "tool_image", onOpenImageStudio),
        ToolTileItem("Website & App", "Editable HTML5 / CSS / JS + Live Preview", Icons.Rounded.Code, CrispWhite, "tool_web", onOpenWebBuilder),
        ToolTileItem("Game Builder", "Playable Canvas mini-games in seconds", Icons.Rounded.SportsEsports, SoftRoseText, "tool_game", onOpenGameBuilder),
        ToolTileItem("Kotlin Builder", "Generate Android Compose Kotlin code", Icons.Rounded.Android, SuccessMint, "tool_kotlin", onOpenKotlinBuilder),
        ToolTileItem("Video & Thumbnail", "YouTube/Reels prompts + 1-tap thumbnail", Icons.Rounded.MovieCreation, RoyalGold, "tool_creator_prompts", onOpenCreatorPrompts),
        ToolTileItem("Cloud Creations", "View saved websites, games & Kotlin", Icons.Rounded.CloudDone, SuccessMint, "tool_saved_creations", onOpenSavedCreations),
        ToolTileItem("Camera Vision", "Snap a photo & ask Naina about it", Icons.Rounded.CameraAlt, CrispWhite, "tool_camera", onOpenCamera),
        ToolTileItem("Gallery Vision", "Pick a photo to analyze or edit", Icons.Rounded.PhotoLibrary, RoyalGold, "tool_gallery", onOpenGallery),
        ToolTileItem("YouTube", "Open YouTube app or web", Icons.Rounded.PlayCircle, NeonCrimson, "tool_youtube", onOpenYouTube),
        ToolTileItem("Instagram", "Open Instagram directly", Icons.Rounded.AutoAwesome, GlowingRuby, "tool_instagram", onOpenInstagram),
        ToolTileItem("WhatsApp", "Open WhatsApp messenger", Icons.AutoMirrored.Rounded.Chat, SuccessMint, "tool_whatsapp", onOpenWhatsApp),
        ToolTileItem("Web Browser", "Open your default browser", Icons.Rounded.Public, CrispWhite, "tool_browser", onOpenBrowser),
        ToolTileItem("Android Settings", "Open device system settings", Icons.Rounded.Settings, SoftRoseText, "tool_settings", onOpenDeviceSettings),
        ToolTileItem("Share Naina", "Share Naina AI with friends", Icons.Rounded.Share, SoftRoseText, "tool_share", onShare)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
    ) {
        NainaSubScreenTopBar(
            title = "Naina AI Tools",
            subtitle = "AI Builders, Phone Actions & Cloud Studio",
            onBackClick = onBackClick
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 158.dp),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 12.dp,
                bottom = 110.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tools) { tool ->
                Surface(
                    onClick = tool.onClick,
                    shape = RoundedCornerShape(20.dp),
                    color = VelvetSurface,
                    border = BorderStroke(1.dp, SoftCrimsonBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(tool.testTag)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tool.tint.copy(alpha = 0.16f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = tool.tint,
                                modifier = Modifier.padding(9.dp)
                            )
                        }
                        Text(
                            text = tool.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = CrispWhite
                        )
                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedRoseText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoreAndSettingsScreen(
    userProfile: UserProfile?,
    firebaseUserDisplayName: String?,
    firebaseUserEmail: String?,
    firebaseUserPhotoUrl: String?,
    voiceSettings: VoiceSettings,
    availableVoices: List<TtsVoiceOption>,
    voiceStatusMessage: String?,
    isSpeaking: Boolean,
    onBackClick: () -> Unit,
    onSelectLanguage: (NainaLanguage) -> Unit,
    onSelectVoiceStyle: (NainaVoiceStyle) -> Unit,
    onSelectTtsVoice: (String?) -> Unit,
    onVoiceEnabledChange: (Boolean) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onOpenTtsSettings: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onOpenSavedCreations: () -> Unit,
    onSignOutClick: () -> Unit,
    onOpenProInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = userProfile?.displayName?.ifBlank { null }
        ?: firebaseUserDisplayName?.ifBlank { null }
        ?: "Naina AI User"
    val email = userProfile?.email?.ifBlank { null }
        ?: firebaseUserEmail.orEmpty()
    val photoUrl = userProfile?.photoUrl ?: firebaseUserPhotoUrl

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
    ) {
        NainaSubScreenTopBar(
            title = "Account & Voice Settings",
            subtitle = "Google Profile, Voice Menu & Cloud Library",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Google Account Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = VelvetSurface,
                border = BorderStroke(1.dp, CrimsonBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(DeepRuby)
                                .border(1.5.dp, RoyalGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Google account profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = null,
                                    tint = RoyalGold,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (email.isNotBlank()) {
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SoftRoseText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Signed in via Google • Cloud Sync Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessMint
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenSavedCreations,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepRuby),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("open_saved_creations_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudDone,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Saves", maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = onSignOutClick,
                            border = BorderStroke(1.dp, ErrorCoral),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = null,
                                tint = ErrorCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out", color = ErrorCoral)
                        }
                    }
                }
            }

            VoiceSettingsPanel(
                voiceSettings = voiceSettings,
                availableVoices = availableVoices,
                voiceStatusMessage = voiceStatusMessage,
                isSpeaking = isSpeaking,
                onSelectLanguage = onSelectLanguage,
                onSelectVoiceStyle = onSelectVoiceStyle,
                onSelectTtsVoice = onSelectTtsVoice,
                onVoiceEnabledChange = onVoiceEnabledChange,
                onSpeedChange = onSpeedChange,
                onPitchChange = onPitchChange,
                onVolumeChange = onVolumeChange,
                onTestVoice = onTestVoice,
                onStopVoice = onStopVoice,
                onOpenTtsSettings = onOpenTtsSettings
            )

            // System & Permissions Shortcuts
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GlassCardBg,
                border = BorderStroke(1.dp, SoftCrimsonBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Android Permissions & TTS Packs",
                        style = MaterialTheme.typography.titleSmall,
                        color = CrispWhite
                    )
                    Text(
                        text = "Microphone and Camera permissions are optional and only requested with your consent. Install offline Hindi/Urdu female voices via Android TTS Settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedRoseText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenTtsSettings,
                            border = BorderStroke(1.dp, RoyalGold),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("TTS Voices", color = RoyalGold)
                        }
                        OutlinedButton(
                            onClick = onOpenAppSettings,
                            border = BorderStroke(1.dp, SoftCrimsonBorder),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("App Permissions", color = CrispWhite)
                        }
                    }
                }
            }

            Surface(
                onClick = onOpenProInfo,
                shape = RoundedCornerShape(20.dp),
                color = GlassCardBg,
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Naina AI Pro Engine",
                            style = MaterialTheme.typography.titleMedium,
                            color = RoyalGold
                        )
                        Text(
                            text = "Gemini 3.8 Flash + Nano Banana 2.1 via Firebase AI Logic",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftRoseText
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsBottomSheet(
    voiceSettings: VoiceSettings,
    availableVoices: List<TtsVoiceOption>,
    voiceStatusMessage: String?,
    isSpeaking: Boolean,
    onDismiss: () -> Unit,
    onSelectLanguage: (NainaLanguage) -> Unit,
    onSelectVoiceStyle: (NainaVoiceStyle) -> Unit,
    onSelectTtsVoice: (String?) -> Unit,
    onVoiceEnabledChange: (Boolean) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onOpenTtsSettings: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepCrimsonNight,
        contentColor = CrispWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Naina Voice Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = CrispWhite
            )
            VoiceSettingsPanel(
                voiceSettings = voiceSettings,
                availableVoices = availableVoices,
                voiceStatusMessage = voiceStatusMessage,
                isSpeaking = isSpeaking,
                onSelectLanguage = onSelectLanguage,
                onSelectVoiceStyle = onSelectVoiceStyle,
                onSelectTtsVoice = onSelectTtsVoice,
                onVoiceEnabledChange = onVoiceEnabledChange,
                onSpeedChange = onSpeedChange,
                onPitchChange = onPitchChange,
                onVolumeChange = onVolumeChange,
                onTestVoice = onTestVoice,
                onStopVoice = onStopVoice,
                onOpenTtsSettings = onOpenTtsSettings
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VoiceSettingsPanel(
    voiceSettings: VoiceSettings,
    availableVoices: List<TtsVoiceOption>,
    voiceStatusMessage: String?,
    isSpeaking: Boolean,
    onSelectLanguage: (NainaLanguage) -> Unit,
    onSelectVoiceStyle: (NainaVoiceStyle) -> Unit,
    onSelectTtsVoice: (String?) -> Unit,
    onVoiceEnabledChange: (Boolean) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onTestVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onOpenTtsSettings: () -> Unit
) {
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    val selectedVoiceLabel = if (voiceSettings.selectedVoiceName == null) {
        "Auto (Best Female ${voiceSettings.language.shortLabel} Voice)"
    } else {
        availableVoices.firstOrNull { it.name == voiceSettings.selectedVoiceName }?.displayLabel
            ?: voiceSettings.selectedVoiceName
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = VelvetSurface,
        border = BorderStroke(1.dp, CrimsonBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Speak AI Replies",
                        style = MaterialTheme.typography.titleSmall,
                        color = CrispWhite
                    )
                    Text(
                        text = "Speak Naina's Hindi, Urdu & English replies aloud",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedRoseText
                    )
                }
                Switch(
                    checked = voiceSettings.voiceEnabled,
                    onCheckedChange = onVoiceEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CrispWhite,
                        checkedTrackColor = NeonCrimson
                    ),
                    modifier = Modifier.testTag("voice_enabled_switch")
                )
            }

            if (!voiceStatusMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RoyalGold.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, RoyalGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Voice Availability Notice",
                                style = MaterialTheme.typography.titleSmall,
                                color = RoyalGold
                            )
                        }
                        Text(
                            text = voiceStatusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite
                        )
                        TextButton(onClick = onOpenTtsSettings) {
                            Text("Open Android TTS Settings", color = RoyalGold)
                        }
                    }
                }
            }

            Text(
                text = "Conversation & Speech Language",
                style = MaterialTheme.typography.titleSmall,
                color = SoftRoseText
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NainaLanguage.entries.forEach { lang ->
                    FilterChip(
                        selected = voiceSettings.language == lang,
                        onClick = { onSelectLanguage(lang) },
                        label = { Text(lang.shortLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepRuby,
                            selectedLabelColor = CrispWhite,
                            containerColor = GlassCardBg,
                            labelColor = MutedRoseText
                        )
                    )
                }
            }

            // Installed Female Voice Selection Menu
            Text(
                text = "Female Voice Selection Menu",
                style = MaterialTheme.typography.titleSmall,
                color = SoftRoseText
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { voiceDropdownExpanded = true },
                    shape = RoundedCornerShape(14.dp),
                    color = GlassCardBg,
                    border = BorderStroke(1.dp, SoftCrimsonBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_selection_menu_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RecordVoiceOver,
                            contentDescription = null,
                            tint = GlowingRuby,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = selectedVoiceLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = "Choose installed voice",
                            tint = CrispWhite
                        )
                    }
                }

                DropdownMenu(
                    expanded = voiceDropdownExpanded,
                    onDismissRequest = { voiceDropdownExpanded = false },
                    modifier = Modifier.background(DeepCrimsonNight)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Auto (Best Female ${voiceSettings.language.shortLabel} Voice)",
                                color = RoyalGold
                            )
                        },
                        onClick = {
                            onSelectTtsVoice(null)
                            voiceDropdownExpanded = false
                        }
                    )
                    if (availableVoices.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "No extra device voices found — tap TTS Settings below",
                                    color = MutedRoseText
                                )
                            },
                            onClick = {
                                voiceDropdownExpanded = false
                                onOpenTtsSettings()
                            }
                        )
                    } else {
                        availableVoices.take(20).forEach { voiceOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = voiceOption.displayLabel,
                                        color = if (voiceOption.name == voiceSettings.selectedVoiceName) {
                                            RoyalGold
                                        } else {
                                            CrispWhite
                                        }
                                    )
                                },
                                onClick = {
                                    onSelectTtsVoice(voiceOption.name)
                                    voiceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Voice Tone",
                style = MaterialTheme.typography.titleSmall,
                color = SoftRoseText
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NainaVoiceStyle.entries.forEach { style ->
                    FilterChip(
                        selected = voiceSettings.voiceStyle == style,
                        onClick = { onSelectVoiceStyle(style) },
                        label = { Text(style.shortLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepRuby,
                            selectedLabelColor = CrispWhite,
                            containerColor = GlassCardBg,
                            labelColor = MutedRoseText
                        )
                    )
                }
            }

            Column {
                Text(
                    text = "Speaking Speed: ${"%.1f".format(voiceSettings.speed)}x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrispWhite
                )
                Slider(
                    value = voiceSettings.speed,
                    onValueChange = onSpeedChange,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCrimson,
                        activeTrackColor = NeonCrimson
                    )
                )
            }

            Column {
                Text(
                    text = "Voice Pitch: ${"%.1f".format(voiceSettings.pitch)}x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrispWhite
                )
                Slider(
                    value = voiceSettings.pitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = RoyalGold,
                        activeTrackColor = RoyalGold
                    )
                )
            }

            Column {
                Text(
                    text = "Voice Volume: ${(voiceSettings.volume * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrispWhite
                )
                Slider(
                    value = voiceSettings.volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = GlowingRuby,
                        activeTrackColor = GlowingRuby
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onTestVoice,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("test_voice_button")
                ) {
                    Icon(imageVector = Icons.Rounded.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Voice")
                }
                OutlinedButton(
                    onClick = onStopVoice,
                    enabled = isSpeaking,
                    border = BorderStroke(1.dp, CrimsonBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("stop_voice_button")
                ) {
                    Icon(imageVector = Icons.Rounded.Stop, contentDescription = null, tint = CrispWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stop", color = CrispWhite)
                }
            }
        }
    }
}

@Composable
fun PermissionConsentDialog(
    title: String,
    rationale: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCrimsonNight,
        titleContentColor = RoyalGold,
        textContentColor = SoftRoseText,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Security,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text(title) },
        text = { Text(rationale) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson)
            ) {
                Text("Allow & Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", color = MutedRoseText)
            }
        }
    )
}

@Composable
fun ProCapabilitiesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCrimsonNight,
        titleContentColor = RoyalGold,
        textContentColor = SoftRoseText,
        icon = {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text("NAINA AI Studio Engine")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Everything in Naina AI is powered live by Firebase AI Logic & Cloud Sync:")
                Text("• Streaming Hindi, Urdu, Hinglish & English Chat (Gemini 3.8 Flash)")
                Text("• Live Native SpeechRecognizer Microphone + RMS Waveform")
                Text("• Natural Female Hindi/Urdu/English TTS Voice Selector")
                Text("• AI Image Studio & Photo Remix (Nano Banana 2.1)")
                Text("• Editable Website, Game, Kotlin & Thumbnail Prompt Studio")
                Text("• Google Sign-In & Cloud Firestore Sync")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it", color = NeonCrimson)
            }
        }
    )
}
