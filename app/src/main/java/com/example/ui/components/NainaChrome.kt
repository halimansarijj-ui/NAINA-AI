package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.navigation.NainaRoutes
import com.example.ui.theme.CrimsonBorder
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DeepCrimsonNight
import com.example.ui.theme.DeepRuby
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.GlassTileBg
import com.example.ui.theme.GlowingRuby
import com.example.ui.theme.MutedRoseText
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SoftCrimsonBorder
import com.example.ui.theme.SoftRoseText
import com.example.ui.theme.VelvetSurface
import com.example.ui.theme.WarmGold
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun NainaHomeTopBar(
    userPhotoUrl: String?,
    onProfileClick: () -> Unit,
    onProClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Crown / User Profile Avatar Badge
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = ObsidianVoid,
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(RoyalGold, NeonCrimson))
            ),
            modifier = Modifier
                .size(44.dp)
                .testTag("top_profile_button")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(Brush.radialGradient(listOf(DeepRuby, ObsidianVoid)))
            ) {
                if (!userPhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = "Google account profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = stringResource(R.string.cd_naina_crown),
                        tint = RoyalGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "NAINA AI",
                style = MaterialTheme.typography.headlineMedium,
                color = CrispWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.naina_subtitle),
                style = MaterialTheme.typography.labelMedium,
                color = SoftRoseText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Surface(
            onClick = onProClick,
            shape = RoundedCornerShape(50),
            color = GlassCardBg,
            border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(RoyalGold, NeonCrimson))),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("pro_badge_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "PRO",
                    style = MaterialTheme.typography.titleSmall,
                    color = RoyalGold,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Surface(
            onClick = onSettingsClick,
            shape = CircleShape,
            color = GlassCardBg,
            border = BorderStroke(1.dp, SoftCrimsonBorder),
            modifier = Modifier
                .size(44.dp)
                .testTag("settings_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = CrispWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun NainaSubScreenTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    Surface(
        color = DeepCrimsonNight.copy(alpha = 0.94f),
        border = BorderStroke(0.5.dp, SoftCrimsonBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = CrispWhite
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = CrispWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedRoseText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            actions()
        }
    }
}

@Composable
fun SideRailTile(
    icon: ImageVector,
    label: String,
    accentColor: Color = GlowingRuby,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = GlassTileBg,
        border = BorderStroke(1.dp, CrimsonBorder),
        shadowElevation = 6.dp,
        modifier = modifier
            .width(74.dp)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = CrispWhite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 11.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WaveformMicHub(
    isListening: Boolean,
    isThinking: Boolean,
    isSpeaking: Boolean,
    rmsLevel: Float,
    partialTranscript: String,
    activeLanguageLabel: String,
    onMicClick: () -> Unit,
    onStopListeningClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when {
            isListening -> 1.25f
            isSpeaking || isThinking -> 1.15f
            else -> 1.06f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 550 else 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(104.dp)
        ) {
            // Animated Neon Waveform reacting to live speech RMS & TTS state
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                val barCount = 28
                val barWidth = 4.dp.toPx()
                val gap = (size.width - barCount * barWidth) / (barCount - 1).coerceAtLeast(1)
                val centerY = size.height / 2f
                val activeBoost = when {
                    isListening -> (0.55f + rmsLevel * 0.75f).coerceIn(0.55f, 1.3f)
                    isSpeaking || isThinking -> 0.95f
                    else -> 0.4f
                }

                for (i in 0 until barCount) {
                    val distFromCenter = abs(i - barCount / 2f) / (barCount / 2f)
                    if (distFromCenter < 0.25f) continue
                    val envelope = (1f - distFromCenter).coerceIn(0.15f, 1f)
                    val osc = (0.45f + 0.55f * sin(wavePhase + i * 0.65f))
                    val barHeight = (10.dp.toPx() + 44.dp.toPx() * envelope * osc * activeBoost)
                    val x = i * (barWidth + gap)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            if (isListening) listOf(RoyalGold, NeonCrimson, DeepRuby)
                            else listOf(GlowingRuby, NeonCrimson, DeepRuby)
                        ),
                        topLeft = Offset(x, centerY - barHeight / 2f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                    )
                }
            }

            // Outer Glowing Halo while listening/speaking
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .graphicsLayer {
                        scaleX = pulseScale + (if (isListening) rmsLevel * 0.2f else 0f)
                        scaleY = pulseScale + (if (isListening) rmsLevel * 0.2f else 0f)
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                (if (isListening) RoyalGold else NeonCrimson).copy(alpha = 0.6f),
                                NeonCrimson.copy(alpha = 0.08f)
                            )
                        )
                    )
            )

            // Center Mic / Stop Listening Button
            Surface(
                onClick = if (isListening) onStopListeningClick else onMicClick,
                shape = CircleShape,
                color = DeepRuby,
                border = BorderStroke(
                    2.5.dp,
                    Brush.linearGradient(
                        if (isListening) listOf(RoyalGold, NeonCrimson) else listOf(CrispWhite, NeonCrimson)
                    )
                ),
                shadowElevation = 14.dp,
                modifier = Modifier
                    .size(72.dp)
                    .testTag("voice_mic_button")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(
                        Brush.radialGradient(
                            if (isListening) listOf(NeonCrimson, DeepRuby, ObsidianVoid)
                            else listOf(NeonCrimson, DeepRuby, ObsidianVoid)
                        )
                    )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                        contentDescription = if (isListening) "Stop listening" else stringResource(R.string.cd_voice_mic),
                        tint = CrispWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = if (isListening) onStopListeningClick else onMicClick,
                shape = RoundedCornerShape(50),
                color = GlassCardBg,
                border = BorderStroke(1.2.dp, if (isListening) RoyalGold else CrimsonBorder),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("tap_to_speak_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = if (isListening) RoyalGold else GlowingRuby,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when {
                            isListening && partialTranscript.isNotBlank() -> partialTranscript
                            isListening -> "Stop Listening ($activeLanguageLabel)"
                            isThinking -> "Naina is thinking…"
                            else -> "Tap to Speak ($activeLanguageLabel)"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = CrispWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CompactTileButton(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = CrispWhite,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = VelvetSurface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, SoftCrimsonBorder),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = CrispWhite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 11.sp,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftRoseText,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NainaBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNainaCenterMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        ObsidianVoid.copy(alpha = 0.92f),
                        ObsidianVoid
                    )
                )
            )
            .navigationBarsPadding()
    ) {
        Surface(
            color = DeepCrimsonNight.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, CrimsonBorder),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    label = stringResource(R.string.nav_home),
                    selected = currentRoute == NainaRoutes.ROUTE_HOME,
                    selectedIcon = Icons.Rounded.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    testTag = "nav_home",
                    onClick = { onNavigate(NainaRoutes.ROUTE_HOME) },
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    label = stringResource(R.string.nav_chat),
                    selected = currentRoute == NainaRoutes.ROUTE_CHAT,
                    selectedIcon = Icons.AutoMirrored.Rounded.Chat,
                    unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                    testTag = "nav_chat",
                    onClick = { onNavigate(NainaRoutes.ROUTE_CHAT) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.weight(1f))

                BottomNavItem(
                    label = stringResource(R.string.nav_tools),
                    selected = currentRoute == NainaRoutes.ROUTE_TOOLS,
                    selectedIcon = Icons.Rounded.Handyman,
                    unselectedIcon = Icons.Outlined.Handyman,
                    testTag = "nav_tools",
                    onClick = { onNavigate(NainaRoutes.ROUTE_TOOLS) },
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    label = stringResource(R.string.nav_more),
                    selected = currentRoute == NainaRoutes.ROUTE_MORE,
                    selectedIcon = Icons.Rounded.GridView,
                    unselectedIcon = Icons.Outlined.GridView,
                    testTag = "nav_more",
                    onClick = { onNavigate(NainaRoutes.ROUTE_MORE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-2).dp)
        ) {
            Surface(
                onClick = onNainaCenterMicClick,
                shape = CircleShape,
                color = DeepCrimsonNight,
                border = BorderStroke(
                    2.dp,
                    Brush.linearGradient(listOf(RoyalGold, NeonCrimson, WarmGold))
                ),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(12.dp, CircleShape, ambientColor = NeonCrimson, spotColor = NeonCrimson)
                    .testTag("nav_naina_orb")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(
                        Brush.radialGradient(listOf(DeepRuby, ObsidianVoid))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = stringResource(R.string.nav_naina),
                        tint = RoyalGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = stringResource(R.string.nav_naina),
                style = MaterialTheme.typography.labelSmall,
                color = RoyalGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (selected) NeonCrimson else MutedRoseText,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) CrispWhite else MutedRoseText,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
