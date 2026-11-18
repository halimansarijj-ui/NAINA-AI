package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.VoiceSettings
import com.example.ui.HomeCockpitState
import com.example.ui.components.CompactTileButton
import com.example.ui.components.NainaHomeTopBar
import com.example.ui.components.SideRailTile
import com.example.ui.components.WaveformMicHub
import com.example.ui.theme.CrimsonBorder
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DeepCrimsonNight
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
import com.example.voice.SpeechUiState
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    userPhotoUrl: String?,
    homeState: HomeCockpitState,
    speechState: SpeechUiState,
    voiceSettings: VoiceSettings,
    isSpeaking: Boolean,
    onDraftChange: (String) -> Unit,
    onSendPrompt: () -> Unit,
    onMicClick: () -> Unit,
    onStopListeningClick: () -> Unit,
    onStopSpeaking: () -> Unit,
    onSpeakReply: (String) -> Unit,
    onOpenChat: () -> Unit,
    onOpenImageStudio: () -> Unit,
    onOpenWebBuilder: () -> Unit,
    onOpenGameBuilder: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenYouTube: () -> Unit,
    onOpenInstagram: () -> Unit,
    onShare: () -> Unit,
    onCycleLanguage: () -> Unit,
    onCycleVoiceStyle: () -> Unit,
    onCycleSpeed: () -> Unit,
    onCyclePitch: () -> Unit,
    onCycleVolume: () -> Unit,
    onOpenVoiceSettings: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onOpenTools: () -> Unit,
    onOpenProInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listeningState = speechState as? SpeechUiState.Listening

    // Subtle living character breathing animation on Naina 3D portrait
    val characterAnim = rememberInfiniteTransition(label = "naina_breathe")
    val portraitScale by characterAnim.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.032f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portrait_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonCrimson.copy(alpha = 0.28f),
                            DeepCrimsonNight,
                            ObsidianVoid
                        ),
                        radius = 1100f
                    )
                )
        )

        // Existing Naina 3D Anime Girl Full-Screen Background with subtle breathing animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(490.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_naina_avatar),
                contentDescription = stringResource(R.string.cd_naina_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = portraitScale
                        scaleY = portraitScale
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to ObsidianVoid.copy(alpha = 0.72f),
                            0.22f to Color.Transparent,
                            0.65f to Color.Transparent,
                            1.0f to ObsidianVoid
                        )
                    )
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 600.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = if (isWide) 680.dp else maxWidth)
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 104.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NainaHomeTopBar(
                    userPhotoUrl = userPhotoUrl,
                    onProfileClick = onOpenAccountSettings,
                    onProClick = onOpenProInfo,
                    onSettingsClick = onOpenVoiceSettings
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GlassCardBg.copy(alpha = 0.68f),
                        border = BorderStroke(1.dp, SoftCrimsonBorder),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 4.dp, top = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.naina_hello_banner),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontStyle = FontStyle.Italic,
                                lineHeight = 18.sp
                            ),
                            color = CrispWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            SideRailTile(
                                icon = Icons.AutoMirrored.Rounded.Chat,
                                label = "AI Chat",
                                accentColor = GlowingRuby,
                                testTag = "rail_ai_chat",
                                onClick = onOpenChat
                            )
                            SideRailTile(
                                icon = Icons.Rounded.Mic,
                                label = "Voice\nAssistant",
                                accentColor = NeonCrimson,
                                testTag = "rail_voice_assistant",
                                onClick = onMicClick
                            )
                            SideRailTile(
                                icon = Icons.Rounded.Palette,
                                label = "Image\nStudio",
                                accentColor = RoyalGold,
                                testTag = "rail_image_studio",
                                onClick = onOpenImageStudio
                            )
                            SideRailTile(
                                icon = Icons.Rounded.Code,
                                label = "App &\nWebsite",
                                accentColor = CrispWhite,
                                testTag = "rail_web_builder",
                                onClick = onOpenWebBuilder
                            )
                            SideRailTile(
                                icon = Icons.Rounded.SportsEsports,
                                label = "Game\nBuilder",
                                accentColor = SuccessMint,
                                testTag = "rail_game_builder",
                                onClick = onOpenGameBuilder
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            SideRailTile(
                                icon = Icons.Rounded.CameraAlt,
                                label = "Camera",
                                accentColor = CrispWhite,
                                testTag = "rail_camera",
                                onClick = onOpenCamera
                            )
                            SideRailTile(
                                icon = Icons.Rounded.PhotoLibrary,
                                label = "Gallery",
                                accentColor = RoyalGold,
                                testTag = "rail_gallery",
                                onClick = onOpenGallery
                            )
                            SideRailTile(
                                icon = Icons.Rounded.PlayCircle,
                                label = "YouTube",
                                accentColor = NeonCrimson,
                                testTag = "rail_youtube",
                                onClick = onOpenYouTube
                            )
                            SideRailTile(
                                icon = Icons.Rounded.AutoAwesome,
                                label = "Instagram",
                                accentColor = GlowingRuby,
                                testTag = "rail_instagram",
                                onClick = onOpenInstagram
                            )
                            SideRailTile(
                                icon = Icons.Rounded.Share,
                                label = "Share",
                                accentColor = SoftRoseText,
                                testTag = "rail_share",
                                onClick = onShare
                            )
                        }
                    }
                }

                // Live SpeechRecognizer Waveform + Mic Glow Hub
                WaveformMicHub(
                    isListening = listeningState != null,
                    isThinking = homeState.isThinking,
                    isSpeaking = isSpeaking,
                    rmsLevel = listeningState?.rmsLevel ?: 0f,
                    partialTranscript = listeningState?.partialText.orEmpty(),
                    activeLanguageLabel = voiceSettings.language.shortLabel,
                    onMicClick = onMicClick,
                    onStopListeningClick = onStopListeningClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GlassCardBg,
                    border = BorderStroke(
                        1.dp,
                        if (homeState.isError) ErrorCoral else SoftCrimsonBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .testTag("home_reply_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = homeState.nainaReply,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (homeState.isError) ErrorCoral else CrispWhite,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (homeState.isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeonCrimson,
                                strokeWidth = 2.dp
                            )
                        } else if (isSpeaking) {
                            IconButton(
                                onClick = onStopSpeaking,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Stop,
                                    contentDescription = stringResource(R.string.cd_stop_voice),
                                    tint = NeonCrimson
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { onSpeakReply(homeState.nainaReply) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = stringResource(R.string.cd_speak_reply),
                                    tint = RoyalGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = homeState.inputDraft,
                    onValueChange = onDraftChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.type_message_hint),
                            color = MutedRoseText
                        )
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = onOpenChat,
                            modifier = Modifier.testTag("open_chat_from_input")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Chat,
                                contentDescription = stringResource(R.string.nav_chat),
                                tint = GlowingRuby
                            )
                        }
                    },
                    trailingIcon = {
                        Surface(
                            onClick = onSendPrompt,
                            shape = CircleShape,
                            color = NeonCrimson,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(38.dp)
                                .testTag("home_send_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Send,
                                    contentDescription = stringResource(R.string.cd_send_message),
                                    tint = CrispWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendPrompt() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GlassCardBg,
                        unfocusedContainerColor = GlassCardBg,
                        focusedBorderColor = NeonCrimson,
                        unfocusedBorderColor = CrimsonBorder,
                        focusedTextColor = CrispWhite,
                        unfocusedTextColor = CrispWhite,
                        cursorColor = NeonCrimson
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .testTag("home_message_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                SectionStripCard(
                    title = stringResource(R.string.voice_settings_title),
                    headerIcon = Icons.Rounded.GraphicEq,
                    headerIconTint = NeonCrimson,
                    actionLabel = "More >",
                    actionTag = "voice_settings_more",
                    onActionClick = onOpenVoiceSettings
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompactTileButton(
                            icon = Icons.Rounded.Language,
                            title = "Language",
                            subtitle = "(${voiceSettings.language.shortLabel})",
                            iconTint = CrispWhite,
                            testTag = "tile_language",
                            onClick = onCycleLanguage,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.RecordVoiceOver,
                            title = "Voice",
                            subtitle = if (voiceSettings.voiceEnabled) {
                                "(${voiceSettings.voiceStyle.shortLabel})"
                            } else "(Muted)",
                            iconTint = if (voiceSettings.voiceEnabled) GlowingRuby else MutedRoseText,
                            testTag = "tile_voice_style",
                            onClick = onCycleVoiceStyle,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.Speed,
                            title = "Speed",
                            subtitle = "(${"%.1f".format(voiceSettings.speed)}x)",
                            iconTint = CrispWhite,
                            testTag = "tile_speed",
                            onClick = onCycleSpeed,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.MusicNote,
                            title = "Pitch",
                            subtitle = "(${"%.1f".format(voiceSettings.pitch)}x)",
                            iconTint = CrispWhite,
                            testTag = "tile_pitch",
                            onClick = onCyclePitch,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.VolumeUp,
                            title = "Volume",
                            subtitle = "(${(voiceSettings.volume * 100).roundToInt()}%)",
                            iconTint = CrispWhite,
                            testTag = "tile_volume",
                            onClick = onCycleVolume,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SectionStripCard(
                    title = stringResource(R.string.quick_features_title),
                    headerIcon = Icons.Rounded.Star,
                    headerIconTint = RoyalGold,
                    actionLabel = "See All >",
                    actionTag = "quick_features_see_all",
                    onActionClick = onOpenTools
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompactTileButton(
                            icon = Icons.Rounded.Public,
                            title = "Make\nWebsite",
                            iconTint = GlowingRuby,
                            testTag = "quick_make_website",
                            onClick = onOpenWebBuilder,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.Android,
                            title = "Create\nApp",
                            iconTint = SuccessMint,
                            testTag = "quick_create_app",
                            onClick = onOpenWebBuilder,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.SportsEsports,
                            title = "Play\nGame",
                            iconTint = SoftRoseText,
                            testTag = "quick_play_game",
                            onClick = onOpenGameBuilder,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.Image,
                            title = "Generate\nImage",
                            iconTint = RoyalGold,
                            testTag = "quick_generate_image",
                            onClick = onOpenImageStudio,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTileButton(
                            icon = Icons.Rounded.PlayCircle,
                            title = "YouTube\nPlay",
                            iconTint = NeonCrimson,
                            testTag = "quick_youtube_play",
                            onClick = onOpenYouTube,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionStripCard(
    title: String,
    headerIcon: androidx.compose.ui.graphics.vector.ImageVector,
    headerIconTint: Color,
    actionLabel: String,
    actionTag: String,
    onActionClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = GlassCardBg,
        border = BorderStroke(1.dp, SoftCrimsonBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = headerIconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = CrispWhite,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onActionClick,
                    modifier = Modifier.testTag(actionTag)
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = SoftRoseText
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}
