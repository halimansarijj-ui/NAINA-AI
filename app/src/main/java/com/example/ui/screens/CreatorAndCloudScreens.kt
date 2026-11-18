package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.MovieCreation
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.SavedCreation
import com.example.system.SystemActions
import com.example.ui.CreationsListUiState
import com.example.ui.CreatorPackUiState
import com.example.ui.TextGenUiState
import com.example.ui.components.NainaSubScreenTopBar
import com.example.ui.theme.CrimsonBorder
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DeepCrimsonNight
import com.example.ui.theme.DeepRuby
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.GlowingRuby
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MutedRoseText
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SoftCrimsonBorder
import com.example.ui.theme.SoftRoseText
import com.example.ui.theme.SuccessMint
import com.example.ui.theme.VelvetSurface

@Composable
fun KotlinCodeGeneratorScreen(
    uiState: TextGenUiState,
    onBackClick: () -> Unit,
    onGenerateKotlin: (String) -> Unit,
    onSaveToCloud: (title: String, prompt: String, code: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var prompt by rememberSaveable { mutableStateOf("") }

    val presets = listOf(
        "Material 3 Login Screen with Jetpack Compose",
        "Music Player Card with Animated Visualizer",
        "Expense Tracker Room Entity + ViewModel",
        "Coroutines Retrofit API Repository"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
            .navigationBarsPadding()
    ) {
        NainaSubScreenTopBar(
            title = "Android Kotlin Studio",
            subtitle = "Jetpack Compose & Material 3 Codegen",
            onBackClick = onBackClick,
            actions = {
                if (uiState is TextGenUiState.Success) {
                    IconButton(
                        onClick = {
                            onSaveToCloud(
                                uiState.prompt.take(48).ifBlank { "Kotlin Component" },
                                uiState.prompt,
                                uiState.output
                            )
                        },
                        modifier = Modifier.testTag("kotlin_save_cloud_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = "Save Kotlin code to cloud",
                            tint = SuccessMint
                        )
                    }
                    IconButton(onClick = { clipboard.setText(AnnotatedString(uiState.output)) }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Kotlin code",
                            tint = RoyalGold
                        )
                    }
                    IconButton(
                        onClick = {
                            SystemActions.shareText(context, uiState.prompt, uiState.output)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share Kotlin code",
                            tint = SoftRoseText
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = {
                    Text(
                        text = "Describe the Android Compose screen or Kotlin class…",
                        color = MutedRoseText
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
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
                    .fillMaxWidth()
                    .testTag("kotlin_prompt_input")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presets) { preset ->
                    AssistChip(
                        onClick = { prompt = preset },
                        label = {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelMedium,
                                color = CrispWhite
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = VelvetSurface),
                        border = BorderStroke(1.dp, SoftCrimsonBorder)
                    )
                }
            }

            Button(
                onClick = { onGenerateKotlin(prompt) },
                enabled = prompt.isNotBlank() && uiState !is TextGenUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCrimson,
                    contentColor = CrispWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("kotlin_generate_button")
            ) {
                Icon(imageVector = Icons.Rounded.Android, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Kotlin Code", style = MaterialTheme.typography.titleSmall)
            }
        }

        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = VelvetSurface,
            border = BorderStroke(1.dp, SoftCrimsonBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            when (uiState) {
                is TextGenUiState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Android,
                            contentDescription = null,
                            tint = SuccessMint,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Generate Jetpack Compose & Kotlin",
                            style = MaterialTheme.typography.titleMedium,
                            color = CrispWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Naina writes clean Material 3 Composables, ViewModels, and Kotlin helpers ready to copy or save.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedRoseText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is TextGenUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SuccessMint)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Writing Jetpack Compose Kotlin…",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                        }
                    }
                }

                is TextGenUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Kotlin Generation Failed",
                            style = MaterialTheme.typography.titleMedium,
                            color = ErrorCoral
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftRoseText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is TextGenUiState.Success -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(14.dp)
                    ) {
                        Text(
                            text = uiState.output,
                            fontFamily = JetBrainsMonoFamily,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorPromptsScreen(
    uiState: CreatorPackUiState,
    onBackClick: () -> Unit,
    onGeneratePrompts: (topic: String, platform: String) -> Unit,
    onSendToImageStudio: (String) -> Unit,
    onSaveToCloud: (title: String, prompt: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var topic by rememberSaveable { mutableStateOf("") }
    var platform by rememberSaveable { mutableStateOf("YouTube") }

    val platforms = listOf("YouTube", "Instagram Reels", "YouTube Shorts")
    val presets = listOf(
        "Top 5 AI Apps in 2026 (Hindi Tech)",
        "Mumbai Street Food Midnight Vlog",
        "Cyberpunk Neon Gaming Montage",
        "Morning Study & Focus Routine"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
            .navigationBarsPadding()
    ) {
        NainaSubScreenTopBar(
            title = "Thumbnail & Video AI",
            subtitle = "Titles, Hooks, Video & Thumbnail Prompts",
            onBackClick = onBackClick,
            actions = {
                if (uiState is CreatorPackUiState.Success) {
                    IconButton(
                        onClick = {
                            onSaveToCloud(
                                uiState.topic.take(48).ifBlank { "Creator Prompts" },
                                uiState.topic,
                                uiState.result.fullGuide
                            )
                        },
                        modifier = Modifier.testTag("creator_save_cloud_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = "Save prompt pack to cloud",
                            tint = SuccessMint
                        )
                    }
                    IconButton(
                        onClick = { clipboard.setText(AnnotatedString(uiState.result.fullGuide)) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy prompt pack",
                            tint = RoyalGold
                        )
                    }
                    IconButton(
                        onClick = {
                            SystemActions.shareText(context, uiState.topic, uiState.result.fullGuide)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share prompt pack",
                            tint = SoftRoseText
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                platforms.forEach { option ->
                    FilterChip(
                        selected = platform == option,
                        onClick = { platform = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepRuby,
                            selectedLabelColor = CrispWhite,
                            containerColor = VelvetSurface,
                            labelColor = SoftRoseText
                        )
                    )
                }
            }

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                placeholder = {
                    Text(
                        text = "Enter your video topic or niche…",
                        color = MutedRoseText
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
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
                    .fillMaxWidth()
                    .testTag("creator_topic_input")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { preset ->
                    AssistChip(
                        onClick = { topic = preset },
                        label = { Text(preset, color = CrispWhite) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = VelvetSurface),
                        border = BorderStroke(1.dp, SoftCrimsonBorder)
                    )
                }
            }

            Button(
                onClick = { onGeneratePrompts(topic, platform) },
                enabled = topic.isNotBlank() && uiState !is CreatorPackUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("creator_generate_button")
            ) {
                Icon(imageVector = Icons.Rounded.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Video & Thumbnail Pack")
            }

            when (uiState) {
                is CreatorPackUiState.Idle -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = VelvetSurface,
                        border = BorderStroke(1.dp, SoftCrimsonBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MovieCreation,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Creator Prompt Studio",
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispWhite
                            )
                            Text(
                                text = "Get high-CTR titles, a spoken opening hook, an AI video prompt, and a one-tap thumbnail prompt ready for Image Studio.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedRoseText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                is CreatorPackUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = RoyalGold)
                    }
                }

                is CreatorPackUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = ErrorCoral.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ErrorCoral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.message,
                            color = ErrorCoral,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is CreatorPackUiState.Success -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = GlassCardBg,
                        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Ready Thumbnail Prompt",
                                style = MaterialTheme.typography.titleSmall,
                                color = RoyalGold
                            )
                            Text(
                                text = uiState.result.extractedThumbnailPrompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CrispWhite
                            )
                            Button(
                                onClick = {
                                    onSendToImageStudio(uiState.result.extractedThumbnailPrompt)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RoyalGold,
                                    contentColor = ObsidianVoid
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_to_image_studio_button")
                            ) {
                                Icon(imageVector = Icons.Rounded.Image, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Thumbnail in Image Studio")
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = VelvetSurface,
                        border = BorderStroke(1.dp, SoftCrimsonBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.result.fullGuide,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftRoseText,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedCreationsScreen(
    uiState: CreationsListUiState,
    onBackClick: () -> Unit,
    onDeleteCreation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
            .navigationBarsPadding()
    ) {
        NainaSubScreenTopBar(
            title = "Cloud Creations",
            subtitle = "Synced in your Google Cloud Firestore",
            onBackClick = onBackClick
        )

        when (uiState) {
            is CreationsListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCrimson)
                }
            }

            is CreationsListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = ErrorCoral,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CreationsListUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDone,
                            contentDescription = null,
                            tint = GlowingRuby,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Saved Creations Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = CrispWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Build a website, game, Kotlin screen, or creator prompt and tap the cloud save icon to sync it here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedRoseText,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.items, key = { it.id }) { item ->
                            SavedCreationCard(
                                creation = item,
                                onCopy = { clipboard.setText(AnnotatedString(item.content)) },
                                onShare = {
                                    SystemActions.shareText(context, item.title, item.content)
                                },
                                onDelete = { onDeleteCreation(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCreationCard(
    creation: SavedCreation,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = VelvetSurface,
        border = BorderStroke(1.dp, CrimsonBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = DeepRuby
                ) {
                    Text(
                        text = creation.kind,
                        style = MaterialTheme.typography.labelSmall,
                        color = RoyalGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = creation.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.titleSmall,
                    color = CrispWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if (creation.prompt.isNotBlank()) {
                Text(
                    text = creation.prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftRoseText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    border = BorderStroke(1.dp, SoftCrimsonBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", color = CrispWhite)
                }
                OutlinedButton(
                    onClick = onShare,
                    border = BorderStroke(1.dp, SoftCrimsonBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        tint = SoftRoseText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = CrispWhite)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete creation",
                        tint = ErrorCoral
                    )
                }
            }
        }
    }
}
