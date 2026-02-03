package com.jule.food

import android.content.Context
import android.content.Intent
import android.net.Uri


// Handle incoming Json Intent
fun handleJsonIntent(context: Context, intent: Intent): String? {
    // Check if this intent is for viewing a file
    return when (intent.action) {
        Intent.ACTION_VIEW -> {
            // intent.data contains the URI of the file to open
            intent.data?.let { uri ->
                readJsonFromUri(context, uri)
            }
        }
        else -> null
    }
}
private fun readJsonFromUri(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().readText()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}