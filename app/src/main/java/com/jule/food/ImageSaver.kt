package com.jule.food

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class ImageSaver(private val context: Context) {

    fun saveImageFromUri(imageUri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var savedImagePath: String? = null

        try {
            // Get the input stream from the URI
            inputStream = contentResolver.openInputStream(imageUri)
                ?: throw IOException("Failed to open input stream")

            // Create a unique file name
            val fileName = "image_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)

            // Get the output stream for the new file
            outputStream = FileOutputStream(file)

            // Copy the data
            inputStream.copyTo(outputStream)

            // Save the new file path
            savedImagePath = file.absolutePath
            Log.d("ImageSaver", "Image saved to: $savedImagePath")

        } catch (e: IOException) {
            Log.e("ImageSaver", "Error saving image", e)
        } finally {
            // Close streams
            inputStream?.close()
            outputStream?.close()
        }
        return savedImagePath
    }
}