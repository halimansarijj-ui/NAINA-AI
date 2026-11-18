package com.example.system

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object SystemActions {

    fun openYouTube(context: Context, onMissing: (String) -> Unit) {
        launchPackageOrUrl(
            context = context,
            packageName = "com.google.android.youtube",
            fallbackUrl = "https://www.youtube.com",
            appLabel = "YouTube",
            onMissing = onMissing
        )
    }

    fun openInstagram(context: Context, onMissing: (String) -> Unit) {
        launchPackageOrUrl(
            context = context,
            packageName = "com.instagram.android",
            fallbackUrl = "https://www.instagram.com",
            appLabel = "Instagram",
            onMissing = onMissing
        )
    }

    fun openWhatsApp(context: Context, onMissing: (String) -> Unit) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
        if (launchIntent != null) {
            try {
                context.startActivity(launchIntent)
                return
            } catch (_: ActivityNotFoundException) {
                // Fall through to web link
            }
        }
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/"))
            )
        } catch (_: ActivityNotFoundException) {
            onMissing("WhatsApp is not installed and no browser was found.")
        }
    }

    fun openBrowser(
        context: Context,
        url: String = "https://www.google.com",
        onMissing: (String) -> Unit
    ) {
        try {
            val normalized = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            onMissing("No web browser app found on this device.")
        }
    }

    fun openAppSettings(context: Context, onMissing: (String) -> Unit) {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            openDeviceSettings(context, onMissing)
        }
    }

    fun openDeviceSettings(context: Context, onMissing: (String) -> Unit) {
        try {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        } catch (_: ActivityNotFoundException) {
            onMissing("Unable to open Android Settings.")
        }
    }

    fun openTtsSettings(context: Context, onMissing: (String) -> Unit) {
        try {
            context.startActivity(Intent("com.android.settings.TTS_SETTINGS"))
        } catch (_: ActivityNotFoundException) {
            openDeviceSettings(context, onMissing)
        }
    }

    fun shareText(context: Context, subject: String, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share via Naina AI"))
    }

    private fun launchPackageOrUrl(
        context: Context,
        packageName: String,
        fallbackUrl: String,
        appLabel: String,
        onMissing: (String) -> Unit
    ) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            try {
                context.startActivity(launchIntent)
                return
            } catch (_: ActivityNotFoundException) {
                // Fall back to web URL
            }
        }
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)))
        } catch (_: ActivityNotFoundException) {
            onMissing("$appLabel or a web browser is not installed on this device.")
        }
    }
}
