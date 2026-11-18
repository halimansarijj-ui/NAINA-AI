package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Visibility
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.BuilderUiState
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
fun WebAndAppBuilderScreen(
    uiState: BuilderUiState,
    onBackClick: () -> Unit,
    onBuildProject: (prompt: String, isAppMode: Boolean) -> Unit,
    onUpdateHtmlCode: (String) -> Unit,
    onSaveToCloud: (title: String, prompt: String, htmlCode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    var isAppMode by rememberSaveable { mutableStateOf(false) }

    val presets = listOf(
        "Portfolio Website for an AI Designer",
        "Expense Tracker Mobile Web App",
        "Cafe & Chai Menu Landing Page",
        "Pomodoro Focus Timer App"
    )

    BuilderScreenShell(
        title = "App & Website Builder",
        subtitle = "Editable HTML5 / CSS / JS + Live Preview",
        emptyHeadline = "Build Websites & Mini Apps Instantly",
        emptyDescription = "Describe any website or interactive mobile app. Edit the generated HTML/CSS/JS live and save to your cloud library.",
        presets = presets,
        prompt = prompt,
        onPromptChange = { prompt = it },
        uiState = uiState,
        generateButtonLabel = if (isAppMode) "Create Mobile Web App" else "Build Website",
        onGenerate = { onBuildProject(prompt, isAppMode) },
        onUpdateHtmlCode = onUpdateHtmlCode,
        onSaveToCloud = onSaveToCloud,
        onBackClick = onBackClick,
        modeSelector = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = !isAppMode,
                    onClick = { isAppMode = false },
                    label = { Text("Website") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepRuby,
                        selectedLabelColor = CrispWhite,
                        containerColor = VelvetSurface,
                        labelColor = SoftRoseText
                    ),
                    modifier = Modifier.testTag("builder_mode_website")
                )
                FilterChip(
                    selected = isAppMode,
                    onClick = { isAppMode = true },
                    label = { Text("Mobile App") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepRuby,
                        selectedLabelColor = CrispWhite,
                        containerColor = VelvetSurface,
                        labelColor = SoftRoseText
                    ),
                    modifier = Modifier.testTag("builder_mode_app")
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun GameBuilderScreen(
    uiState: BuilderUiState,
    onBackClick: () -> Unit,
    onBuildGame: (prompt: String) -> Unit,
    onUpdateHtmlCode: (String) -> Unit,
    onSaveToCloud: (title: String, prompt: String, htmlCode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    val presets = listOf(
        "Neon Space Shooter with touch joystick",
        "Cyberpunk Snake with glowing orbs",
        "Brick Breaker Arcade with powerups",
        "Tap-to-Fly Neon Flappy Bird"
    )

    BuilderScreenShell(
        title = "AI Game Builder",
        subtitle = "Instant Playable HTML5 Canvas Games",
        emptyHeadline = "Create & Play Your Own Game",
        emptyDescription = "Describe a mini-game and tap Build Game. Play it right inside Naina AI, tweak its code, or save it to Firestore!",
        presets = presets,
        prompt = prompt,
        onPromptChange = { prompt = it },
        uiState = uiState,
        generateButtonLabel = "Build & Play Game",
        onGenerate = { onBuildGame(prompt) },
        onUpdateHtmlCode = onUpdateHtmlCode,
        onSaveToCloud = onSaveToCloud,
        onBackClick = onBackClick,
        modeSelector = {},
        isGame = true,
        modifier = modifier
    )
}

@Composable
private fun BuilderScreenShell(
    title: String,
    subtitle: String,
    emptyHeadline: String,
    emptyDescription: String,
    presets: List<String>,
    prompt: String,
    onPromptChange: (String) -> Unit,
    uiState: BuilderUiState,
    generateButtonLabel: String,
    onGenerate: () -> Unit,
    onUpdateHtmlCode: (String) -> Unit,
    onSaveToCloud: (title: String, prompt: String, htmlCode: String) -> Unit,
    onBackClick: () -> Unit,
    modeSelector: @Composable () -> Unit,
    isGame: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var reloadTick by remember { mutableIntStateOf(0) }
    var editableCode by rememberSaveable { mutableStateOf("") }

    LaunchedEffect((uiState as? BuilderUiState.Success)?.htmlCode) {
        val code = (uiState as? BuilderUiState.Success)?.htmlCode ?: return@LaunchedEffect
        editableCode = code
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid)))
            .navigationBarsPadding()
    ) {
        NainaSubScreenTopBar(
            title = title,
            subtitle = subtitle,
            onBackClick = onBackClick,
            actions = {
                if (uiState is BuilderUiState.Success) {
                    IconButton(onClick = { reloadTick++ }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Restart preview",
                            tint = CrispWhite
                        )
                    }
                    IconButton(
                        onClick = {
                            onSaveToCloud(
                                uiState.prompt.take(48).ifBlank { title },
                                uiState.prompt,
                                uiState.htmlCode
                            )
                        },
                        modifier = Modifier.testTag("builder_save_cloud_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = "Save to cloud",
                            tint = SuccessMint
                        )
                    }
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(uiState.htmlCode))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy HTML code",
                            tint = RoyalGold
                        )
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, uiState.prompt)
                                putExtra(Intent.EXTRA_TEXT, uiState.htmlCode)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Generated Code"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share generated code",
                            tint = SoftRoseText
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modeSelector()

            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = if (isGame) "Describe your game idea…" else "Describe your website or app…",
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
                    .testTag("builder_prompt_input")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(presets) { preset ->
                    AssistChip(
                        onClick = { onPromptChange(preset) },
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
                onClick = onGenerate,
                enabled = prompt.isNotBlank() && uiState !is BuilderUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGame) SuccessMint.copy(alpha = 0.85f) else NeonCrimson,
                    contentColor = if (isGame) ObsidianVoid else CrispWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("builder_generate_button")
            ) {
                Icon(
                    imageVector = if (isGame) Icons.Rounded.SportsEsports else Icons.Rounded.AutoAwesome,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = generateButtonLabel,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        // Preview / Editable Code Tabs + Stage
        if (uiState is BuilderUiState.Success) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DeepCrimsonNight,
                contentColor = CrispWhite,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCrimson
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isGame) "Play Game" else "Live Preview") },
                    icon = {
                        Icon(
                            imageVector = if (isGame) Icons.Rounded.PlayArrow else Icons.Rounded.Visibility,
                            contentDescription = null
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Edit Code") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null
                        )
                    }
                )
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
                is BuilderUiState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isGame) Icons.Rounded.SportsEsports else Icons.Rounded.Code,
                            contentDescription = null,
                            tint = GlowingRuby,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = emptyHeadline,
                            style = MaterialTheme.typography.titleMedium,
                            color = CrispWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = emptyDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedRoseText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is BuilderUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NeonCrimson)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isGame) "Naina is coding your game…" else "Naina is building your project…",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                        }
                    }
                }

                is BuilderUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Generation Failed",
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

                is BuilderUiState.Success -> {
                    if (selectedTab == 0) {
                        LiveHtmlPreview(
                            html = uiState.htmlCode,
                            reloadTick = reloadTick,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Edit HTML / CSS / JS directly:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SoftRoseText
                                )
                                Button(
                                    onClick = {
                                        onUpdateHtmlCode(editableCode)
                                        reloadTick++
                                        selectedTab = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RoyalGold,
                                        contentColor = ObsidianVoid
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("builder_apply_code_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Apply & Preview")
                                }
                            }
                            OutlinedTextField(
                                value = editableCode,
                                onValueChange = { editableCode = it },
                                textStyle = TextStyle(
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = CrispWhite
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ObsidianVoid,
                                    unfocusedContainerColor = ObsidianVoid,
                                    focusedBorderColor = CrimsonBorder,
                                    unfocusedBorderColor = SoftCrimsonBorder,
                                    cursorColor = RoyalGold
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .testTag("builder_code_editor")
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LiveHtmlPreview(
    html: String,
    reloadTick: Int,
    modifier: Modifier = Modifier
) {
    var lastLoadedSignature by remember { mutableStateOf("") }
    val signature = "$reloadTick:${html.hashCode()}"

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                lastLoadedSignature = signature
            }
        },
        update = { webView ->
            if (lastLoadedSignature != signature) {
                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                lastLoadedSignature = signature
            }
        }
    )
}
