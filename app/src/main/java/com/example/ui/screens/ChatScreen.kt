package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.ChatMessageUi
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
import com.example.ui.theme.VelvetSurface
import com.example.voice.SpeechUiState

@Composable
fun ChatScreen(
    messages: List<ChatMessageUi>,
    pendingImage: Bitmap?,
    speechState: SpeechUiState,
    activeLanguageLabel: String,
    isLoading: Boolean,
    isSpeaking: Boolean,
    voiceEnabled: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onMicClick: () -> Unit,
    onStopListeningClick: () -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onClearPendingImage: () -> Unit,
    onToggleVoice: () -> Unit,
    onSpeakMessage: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val listeningState = speechState as? SpeechUiState.Listening

    val quickPrompts = listOf(
        "आज का मौसम और मूड कैसा रखें?",
        "اردو میں ایک خوبصورت شعر سنائیں ❤️",
        "Explain Quantum AI simply",
        "Give me 5 productivity tips"
    )

    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepCrimsonNight, ObsidianVoid, ObsidianVoid)
                )
            )
            .imePadding()
    ) {
        NainaSubScreenTopBar(
            title = "NAINA AI Chat",
            subtitle = "Gemini 3.8 Flash • $activeLanguageLabel • Multimodal",
            onBackClick = onBackClick,
            actions = {
                IconButton(
                    onClick = {
                        if (isSpeaking) onStopSpeaking() else onToggleVoice()
                    },
                    modifier = Modifier.testTag("chat_voice_toggle")
                ) {
                    Icon(
                        imageVector = when {
                            isSpeaking -> Icons.Rounded.Stop
                            voiceEnabled -> Icons.Rounded.VolumeUp
                            else -> Icons.Rounded.VolumeOff
                        },
                        contentDescription = if (voiceEnabled) "Mute Naina voice" else "Enable Naina voice",
                        tint = if (voiceEnabled) RoyalGold else MutedRoseText
                    )
                }
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("chat_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Clear conversation",
                        tint = SoftRoseText
                    )
                }
            }
        )

        // Starter prompt chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                AssistChip(
                    onClick = { onSendMessage(prompt) },
                    label = {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelMedium,
                            color = CrispWhite
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = VelvetSurface
                    ),
                    border = BorderStroke(1.dp, SoftCrimsonBorder)
                )
            }
        }

        // Message list
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeakClick = { onSpeakMessage(msg.text) },
                    onStopClick = onStopSpeaking,
                    isSpeaking = isSpeaking
                )
            }
        }

        // Live SpeechRecognizer listening banner with Stop Listening button
        if (listeningState != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepRuby.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, RoyalGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .testTag("chat_listening_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = listeningState.partialText.ifBlank {
                            "Listening in $activeLanguageLabel… Speak now"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = CrispWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onStopListeningClick,
                        modifier = Modifier.testTag("chat_stop_listening_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stop", color = RoyalGold)
                    }
                }
            }
        }

        // Pending image attachment preview
        if (pendingImage != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VelvetSurface,
                border = BorderStroke(1.dp, CrimsonBorder),
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = pendingImage.asImageBitmap(),
                        contentDescription = "Attached image preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Image attached for Naina Vision",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftRoseText,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearPendingImage) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Remove attached image",
                            tint = CrispWhite
                        )
                    }
                }
            }
        }

        // Bottom Composer
        Surface(
            color = DeepCrimsonNight.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, SoftCrimsonBorder),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPickImage,
                    modifier = Modifier.testTag("chat_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoLibrary,
                        contentDescription = stringResource(R.string.cd_attach_image),
                        tint = RoyalGold
                    )
                }
                IconButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.testTag("chat_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = stringResource(R.string.cd_take_photo),
                        tint = SoftRoseText
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask Naina anything…",
                            color = MutedRoseText
                        )
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank() || pendingImage != null) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GlassCardBg,
                        unfocusedContainerColor = GlassCardBg,
                        focusedBorderColor = NeonCrimson,
                        unfocusedBorderColor = SoftCrimsonBorder,
                        focusedTextColor = CrispWhite,
                        unfocusedTextColor = CrispWhite,
                        cursorColor = NeonCrimson
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = if (listeningState != null) onStopListeningClick else onMicClick,
                    modifier = Modifier.testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = if (listeningState != null) Icons.Rounded.Stop else Icons.Rounded.Mic,
                        contentDescription = if (listeningState != null) {
                            "Stop listening"
                        } else {
                            stringResource(R.string.cd_voice_mic)
                        },
                        tint = if (listeningState != null) RoyalGold else GlowingRuby
                    )
                }

                Surface(
                    onClick = {
                        if (!isLoading && (inputText.isNotBlank() || pendingImage != null)) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    shape = CircleShape,
                    color = NeonCrimson,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("chat_send_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CrispWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = stringResource(R.string.cd_send_message),
                                tint = CrispWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageUi,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Image(
                painter = painterResource(id = R.drawable.img_naina_avatar),
                contentDescription = stringResource(R.string.cd_naina_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            color = when {
                message.isError -> ErrorCoral.copy(alpha = 0.16f)
                message.isUser -> DeepRuby.copy(alpha = 0.85f)
                else -> VelvetSurface
            },
            border = BorderStroke(
                1.dp,
                when {
                    message.isError -> ErrorCoral
                    message.isUser -> NeonCrimson
                    else -> SoftCrimsonBorder
                }
            ),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.image != null) {
                    Image(
                        bitmap = message.image.asImageBitmap(),
                        contentDescription = "User attached photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (message.isError) ErrorCoral else CrispWhite
                )

                if (!message.isUser && !message.isStreaming && !message.isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = if (isSpeaking) onStopClick else onSpeakClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                                contentDescription = stringResource(R.string.cd_speak_reply),
                                tint = RoyalGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
