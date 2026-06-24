package com.jule.food.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        setAction(Intent.ACTION_SEND)
        putExtra(Intent.EXTRA_TEXT, text)
        setType("text/plain")
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun getDownloadsDir(context: Context): File? {
    val downloadsDir = File(context.filesDir.toString() + "/downloads")
    if (!downloadsDir.exists()) {
        if (!downloadsDir.mkdirs()) {
            Log.e("MainActivity", "Failed to create downloads directory")
            return null
        }
        Log.d("MainActivity", "Created downloads directory")
    }

    return downloadsDir
}

fun shareFile(context: Context, file: File) {
    val shareUri = FileProvider.getUriForFile(context, "com.jule.food.fileprovider", file)

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        setType("application/zip")
        putExtra(Intent.EXTRA_STREAM, shareUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}