package com.jule.food

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

fun openFolder(filetype: String, fileLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = filetype  // Set the type to specifically check for .zip files
        val uri = "content://com.android.externalstorage.documents/document/primary".toUri()
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
    }

    fileLauncher.launch(intent)
}
// Get the file name of the imported file to display to user
fun getFileNameFromUri(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val displayName = it.getString(if (index >= 0) index else 0)
            Log.d("Selected File", displayName)

            return displayName
        }
    }
    return ""
}