package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.ImageStudioUiState
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

@Composable
fun ImageStudioScreen(
    uiState: ImageStudioUiState,
    referenceBitmap: Bitmap?,
    initialPrompt: String = "",
    onConsumeInitialPrompt: () -> Unit = {},
    onBackClick: () -> Unit,
    onPickReferenceImage: () -> Unit,
    onClearReferenceImage: () -> Unit,
    onGenerateImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var prompt by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            prompt = initialPrompt
            onConsumeInitialPrompt()
        }
    }

    val stylePresets = listOf(
        "Cyberpunk Neon",
        "Anime Portrait",
        "YouTube Thumbnail 16:9",
        "3D Glass Icon",
        "Royal Indian Palace"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepCrimsonNight, ObsidianVoid))
            )
    ) {
        NainaSubScreenTopBar(
            title = "Image Studio",
            subtitle = "Powered by Nano Banana 2.1",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Optional reference photo for Image-to-Image editing
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = GlassCardBg,
                border = BorderStroke(1.dp, SoftCrimsonBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (referenceBitmap != null) {
                        Image(
                            bitmap = referenceBitmap.asImageBitmap(),
                            contentDescription = "Reference image for editing",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit Mode Active",
                                style = MaterialTheme.typography.titleSmall,
                                color = RoyalGold
                            )
                            Text(
                                text = "Describe how Naina should transform this image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftRoseText
                            )
                        }
                        IconButton(onClick = onClearReferenceImage) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear reference image",
                                tint = CrispWhite
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.PhotoLibrary,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit Existing Photo (Optional)",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                            Text(
                                text = "Pick from Gallery to remix or restyle",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedRoseText
                            )
                        }
                        OutlinedButton(
                            onClick = onPickReferenceImage,
                            border = BorderStroke(1.dp, CrimsonBorder),
                            modifier = Modifier.testTag("image_studio_pick_ref")
                        ) {
                            Text("Pick", color = CrispWhite)
                        }
                    }
                }
            }

            // Prompt input
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe the image you want to create") },
                placeholder = { Text("e.g., Futuristic neon Mumbai skyline at night in anime style") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassCardBg,
                    unfocusedContainerColor = GlassCardBg,
                    focusedBorderColor = NeonCrimson,
                    unfocusedBorderColor = SoftCrimsonBorder,
                    focusedTextColor = CrispWhite,
                    unfocusedTextColor = CrispWhite,
                    focusedLabelColor = GlowingRuby,
                    unfocusedLabelColor = MutedRoseText,
                    cursorColor = NeonCrimson
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_studio_prompt_input")
            )

            // Style chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(stylePresets) { preset ->
                    AssistChip(
                        onClick = {
                            prompt = if (prompt.isBlank()) {
                                "$preset artwork with vibrant lighting and intricate details"
                            } else {
                                "$prompt, $preset style"
                            }
                        },
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
                onClick = { onGenerateImage(prompt) },
                enabled = prompt.isNotBlank() && uiState !is ImageStudioUiState.Loading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCrimson,
                    contentColor = CrispWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("image_studio_generate_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (referenceBitmap != null) "Remix Photo with AI" else "Generate Image",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Result Preview Area
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = VelvetSurface,
                border = BorderStroke(1.dp, CrimsonBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                when (uiState) {
                    is ImageStudioUiState.Idle -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Image,
                                contentDescription = null,
                                tint = GlowingRuby.copy(alpha = 0.7f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your AI Artwork Appears Here",
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Enter a prompt or pick a style preset above to create high-resolution art.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedRoseText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is ImageStudioUiState.Loading -> {
                        ShimmerGeneratingBox()
                    }

                    is ImageStudioUiState.Success -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                bitmap = uiState.bitmap.asImageBitmap(),
                                contentDescription = "Generated artwork for ${uiState.prompt}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    is ImageStudioUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Image Generation Failed",
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
                }
            }

            if (uiState is ImageStudioUiState.Success) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { saveBitmapToGallery(context, uiState.bitmap, shareAfter = false) },
                        border = BorderStroke(1.dp, RoyalGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("image_studio_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = null,
                            tint = RoyalGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", color = RoyalGold)
                    }
                    Button(
                        onClick = { saveBitmapToGallery(context, uiState.bitmap, shareAfter = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRuby),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("image_studio_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            tint = CrispWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = CrispWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerGeneratingBox() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_shift"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VelvetSurface,
                        DeepRuby.copy(alpha = 0.55f),
                        VelvetSurface
                    ),
                    start = Offset(shift - 400f, 0f),
                    end = Offset(shift, 600f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = RoyalGold, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Naina is painting your image…",
                style = MaterialTheme.typography.titleSmall,
                color = CrispWhite
            )
            Text(
                text = "This usually takes 20–45 seconds",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftRoseText
            )
        }
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, shareAfter: Boolean) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val filename = "NainaAI_${System.currentTimeMillis()}.png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NainaAI")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                if (shareAfter) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Naina AI Image"))
                } else {
                    Toast.makeText(context, "Saved to Pictures/NainaAI", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "Saved preview ready (Android 10+ required for direct Pictures export).",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to export image: ${e.localizedMessage}", Toast.LENGTH_SHORT)
            .show()
    }
}
