package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.NainaLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "NainaAiRepository"
private const val CHAT_MODEL = "gemini-3.8-flash"
private const val IMAGE_MODEL = "gemini-nano-banana-2.1"
private const val AI_TIMEOUT_MS = 60_000L

data class ChatTurn(
    val isUser: Boolean,
    val text: String,
    val image: Bitmap? = null
)

class NainaAiRepository {

    private val aiBackend by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
    }

    private val chatModel by lazy {
        aiBackend.generativeModel(
            modelName = CHAT_MODEL,
            systemInstruction = content {
                text(
                    "You are Naina (नैना / نینا), a warm, intelligent female personal AI assistant. " +
                        "You speak fluent Hindi, Urdu, Hinglish, and English. " +
                        "Maintain conversation context naturally and keep replies clear, helpful, and natural to speak aloud."
                )
            }
        )
    }

    private val builderModel by lazy {
        aiBackend.generativeModel(
            modelName = CHAT_MODEL,
            systemInstruction = content {
                text(
                    "You are an expert front-end web & HTML5 game engineer. " +
                        "Return ONLY a single complete, self-contained HTML5 document starting with <!DOCTYPE html> and ending with </html>. " +
                        "Include all CSS inside <style> and all JavaScript inside <script>. " +
                        "Do NOT wrap the output in markdown code fences or include any commentary."
                )
            }
        )
    }

    private val kotlinCodeModel by lazy {
        aiBackend.generativeModel(
            modelName = CHAT_MODEL,
            systemInstruction = content {
                text(
                    "You are a senior Android Kotlin & Jetpack Compose engineer. " +
                        "Generate clean, idiomatic, production-ready Kotlin code using Material 3 and Jetpack Compose. " +
                        "Output clean Kotlin source code without unnecessary filler."
                )
            }
        )
    }

    private val creatorPromptModel by lazy {
        aiBackend.generativeModel(
            modelName = CHAT_MODEL,
            systemInstruction = content {
                text(
                    "You are a creative YouTube/Instagram Reels strategist and visual AI prompt engineer. " +
                        "Produce punchy, high-CTR video titles, hooks, thumbnail image prompts, and AI video generation prompts."
                )
            }
        )
    }

    private val imageModel by lazy {
        aiBackend.generativeModel(
            modelName = IMAGE_MODEL,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    fun streamChatReply(
        history: List<ChatTurn>,
        userPrompt: String,
        attachedImage: Bitmap?,
        language: NainaLanguage
    ): Flow<String> = flow {
        val recentHistory = history.takeLast(14).map { turn ->
            content(role = if (turn.isUser) "user" else "model") {
                if (turn.image != null && turn.isUser) {
                    image(scaleDownBitmap(turn.image))
                }
                text(turn.text)
            }
        }

        val chat = chatModel.startChat(history = recentHistory)
        val enrichedPrompt = buildString {
            append("[Language Preference: ${language.promptInstruction}]\n")
            if (userPrompt.isNotBlank()) {
                append(userPrompt)
            } else if (attachedImage != null) {
                append("Describe and analyze this image helpfully.")
            }
        }

        val requestContent: Content = content("user") {
            if (attachedImage != null) {
                image(scaleDownBitmap(attachedImage))
            }
            text(enrichedPrompt)
        }

        val accumulated = StringBuilder()
        withTimeout(AI_TIMEOUT_MS) {
            chat.sendMessageStream(requestContent).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    accumulated.append(part)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("Naina received an empty response from Gemini.")
        }
    }

    suspend fun generateWebsiteOrApp(
        prompt: String,
        isAppMode: Boolean,
        language: NainaLanguage
    ): String {
        val modeDesc = if (isAppMode) {
            "a sleek, interactive mobile-first web application with local interactive state, cards, tabs or controls, and touch-friendly buttons"
        } else {
            "a modern, responsive, visually striking website with hero section, interactive features, smooth styling, and clean layout"
        }
        val fullPrompt = """
            Build $modeDesc for the following request: "$prompt".
            Requirements:
            - Output ONLY pure valid HTML5 starting with <!DOCTYPE html> and ending with </html>.
            - No external CDN dependencies required; work completely offline inside a mobile WebView.
            - Mobile viewport meta tag included, touch-friendly layout, attractive modern dark or matching color theme.
            - Content copy language hint: ${language.shortLabel}.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            builderModel.generateContent(fullPrompt)
        }
        val raw = response.text?.trim()
            ?: throw IllegalStateException("Model returned empty HTML output.")
        return extractHtmlDocument(raw)
    }

    suspend fun generatePlayableGame(
        prompt: String
    ): String {
        val fullPrompt = """
            Create a complete, fun, immediately playable mobile touch-friendly HTML5 Canvas / JS mini-game for: "$prompt".
            Requirements:
            - Output ONLY pure valid HTML5 starting with <!DOCTYPE html> and ending with </html>.
            - Include responsive full-viewport canvas or touch controls (on-screen buttons or swipe/tap controls that work smoothly in an Android WebView without needing a physical keyboard).
            - Include score display, start/restart button, and vibrant neon visuals.
            - No external assets or CDNs.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            builderModel.generateContent(fullPrompt)
        }
        val raw = response.text?.trim()
            ?: throw IllegalStateException("Model returned empty game output.")
        return extractHtmlDocument(raw)
    }

    suspend fun generateKotlinCode(prompt: String): String {
        val fullPrompt = """
            Write clean, complete Android Kotlin + Jetpack Compose (Material 3) code for:
            "$prompt"
            Include necessary imports and clear comments. Do not wrap in unnecessary prose.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            kotlinCodeModel.generateContent(fullPrompt)
        }
        val raw = response.text?.trim()
            ?: throw IllegalStateException("Model returned empty Kotlin code.")
        return raw
            .removePrefix("```kotlin")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    suspend fun generateThumbnailAndVideoPrompts(
        topic: String,
        platform: String,
        language: NainaLanguage
    ): ThumbnailAndVideoResult {
        val fullPrompt = """
            Create a complete creator pack for a $platform video about: "$topic".
            Audience language preference: ${language.shortLabel}.
            Provide clearly labeled sections:
            1) TITLE: One catchy high-CTR title
            2) HOOK: 15-second opening spoken hook
            3) THUMBNAIL_PROMPT: A rich, detailed image-generation prompt (visual style, subject expression, lighting, colors, composition, no watermark) suitable for generating the thumbnail image
            4) VIDEO_PROMPT: A cinematic AI video generation prompt describing camera motion, scene transitions, lighting, and atmosphere.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            creatorPromptModel.generateContent(fullPrompt)
        }
        val fullText = response.text?.trim()
            ?: throw IllegalStateException("Model returned empty creator prompt output.")

        val thumbnailLine = fullText.lines()
            .dropWhile { !it.contains("THUMBNAIL_PROMPT", ignoreCase = true) }
            .drop(1)
            .takeWhile { !it.contains("VIDEO_PROMPT", ignoreCase = true) }
            .joinToString(" ")
            .trim()
            .ifBlank {
                fullText.lines()
                    .firstOrNull { it.contains("THUMBNAIL_PROMPT", ignoreCase = true) }
                    ?.substringAfter(":", "")
                    ?.trim()
                    .orEmpty()
            }
            .ifBlank { "High-contrast cinematic YouTube thumbnail for $topic, vibrant neon lighting, expressive focal subject, dramatic depth" }

        return ThumbnailAndVideoResult(
            fullGuide = fullText,
            extractedThumbnailPrompt = thumbnailLine.removePrefix(":").trim()
        )
    }

    suspend fun generateOrEditImage(
        prompt: String,
        referenceBitmap: Bitmap?
    ): Bitmap {
        val response = withTimeout(AI_TIMEOUT_MS) {
            if (referenceBitmap != null) {
                imageModel.generateContent(
                    content {
                        image(scaleDownBitmap(referenceBitmap))
                        text(prompt)
                    }
                )
            } else {
                imageModel.generateContent(prompt)
            }
        }
        val imagePart = response.candidates.firstOrNull()
            ?.content?.parts?.filterIsInstance<ImagePart>()
            ?.firstOrNull()

        return imagePart?.image
            ?: throw IllegalStateException("No image was returned by Gemini Image Studio.")
    }

    private fun extractHtmlDocument(raw: String): String {
        val trimmed = raw.trim()
        val unfenced = if (trimmed.startsWith("```")) {
            trimmed
                .removePrefix("```html")
                .removePrefix("```HTML")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
        } else {
            trimmed
        }
        val docStart = unfenced.indexOf("<!DOCTYPE", ignoreCase = true)
            .takeIf { it >= 0 }
            ?: unfenced.indexOf("<html", ignoreCase = true)
        return if (docStart >= 0) unfenced.substring(docStart) else unfenced
    }

    private fun scaleDownBitmap(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap
        val ratio = minOf(maxDim.toFloat() / width, maxDim.toFloat() / height)
        val targetW = (width * ratio).toInt().coerceAtLeast(1)
        val targetH = (height * ratio).toInt().coerceAtLeast(1)
        return try {
            Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Bitmap scaling failed, using original", e)
            bitmap
        }
    }
}

data class ThumbnailAndVideoResult(
    val fullGuide: String,
    val extractedThumbnailPrompt: String
)
