package com.jule.food

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

// Write a string to a file
fun writeStringToFile(context: Context, filename: String, string: String) {
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(string.toByteArray())
    }
}

// Write any json object to a file (as a string)
inline fun <reified T> writeJsonToFile(context: Context, filename: String, json: T) {
    val str = Json.encodeToString(value = json)
    Log.d("WriteJsonToFile", str)
    writeStringToFile(context, filename, str)
}

// Get string from a file
fun getStringFromFile(context: Context, filename: String): String? {
    return try {
        context.openFileInput(filename).bufferedReader().use {
            it.readText()
        }
    } catch (e: FileNotFoundException) {
        Log.e("getStringFromFile", e.toString())
        null
    }
}
// Get a json object from a file. Log an error if it is not possible to decode the file into that object
inline fun <reified T> getJsonFromFile(context: Context, fileName: String, ignoreKeys: Boolean = false) : T? {
    val str = getStringFromFile(context, fileName) ?: return null
    Log.d("getJsonFromFile", str)
    return getJsonFromString(context, str, ignoreKeys)
}

inline fun <reified T> getJsonFromString(context: Context, string: String, ignoreKeys: Boolean = false) : T? {
    val json = Json {
        ignoreUnknownKeys = ignoreKeys
    }

    return try {
        json.decodeFromString<T>(string)
    } catch (e: Exception) {
        Log.e("getJsonFromString", e.toString())
        null
    }
}

// Delete a file
fun deleteFile(path: String) {
    val file = File(path)
    if (file.exists()) {
        val isDeleted = file.delete()
        if (isDeleted) {
            Log.d("deleteFile", "File deleted: $path")
        } else {
            Log.e("deleteFile", "Failed to delete file: $path")
        }
    } else {
        Log.w("deleteFile", "File does not exist: $path")
    }
}
// Delete several files
fun deleteFiles(paths: List<String>) {
    paths.forEach {
        deleteFile(it)
    }
}

// Create an export file including several data files and image files in specific folders
fun createZipExportFile(context: Context, dataFiles: List<File>, imageFiles: List<File>, name: String, addToDir: String = ""): File {
    val zipFile = File(context.filesDir.toString() + addToDir, name)
    ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
        // Add data folder
        zipOut.putNextEntry(ZipEntry("data/"))
        zipOut.closeEntry()

        // Add all data files
        dataFiles.forEach { file ->
            FileInputStream(file).use { fileIn ->
                val zipEntry = ZipEntry("data/${file.name}")
                zipOut.putNextEntry(zipEntry)
                fileIn.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }

        // Add images folder
        zipOut.putNextEntry(ZipEntry("images/"))

        // Add all image files
        imageFiles.forEach { file ->
            FileInputStream(file).use { fileIn ->
                val zipEntry = ZipEntry("images/${file.name}")
                zipOut.putNextEntry(zipEntry)
                fileIn.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
    }

    return zipFile
}

// Export (e.g. zip) file to a location on the phone
fun exportFileToLocation(context: Context, file: File, uri: Uri) {
    val contentResolver = context.contentResolver
    contentResolver.openOutputStream(uri)?.use { outputStream ->
        file.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

// Creates image files from image paths in order to export them
fun createImageFilesFromPaths(paths: List<String>): List<File> {
    val output = mutableListOf<File>()
    paths.forEach { path ->
        File(path)
        val file = File(path)
        if (!file.exists()) {
            Log.e("CreateImageFiles", "File $path not found")
        } else {
            output.add(file)
        }
    }

    return output
}

// Data class for the result of importing data
class ImportData (val recipes: SaveableRecipes?, val groceries: SaveableGroceryItemCategories?, val images: List<File>?)

// Get the data from a zip file
fun getImportDataFromFile(context: Context, uri: Uri, setting: ImportSetting): ImportData {
    var recipesJson = ""
    var groceriesJson = ""
    val images = mutableListOf<File>()

    // Determine which files to import
    val importGroceries = setting == ImportSetting.Groceries || setting == ImportSetting.Both
    val importRecipes = setting == ImportSetting.Recipe || setting == ImportSetting.Both

    Log.d("URI", uri.toString())
    val contentResolver = context.contentResolver
    contentResolver.openInputStream(uri)?.use { input ->
        // Loop through all entries in the zip file
        ZipInputStream(input).use { zipIn ->
            var zipEntry: ZipEntry? = zipIn.nextEntry

            while (zipEntry != null) {
                // Only check files, not directories
                if (zipEntry.isDirectory) {
                    zipIn.closeEntry()
                    zipEntry = zipIn.nextEntry
                    continue
                }
                Log.d("Import", "Zip Entry: ${zipEntry.name}")

                // Get groceries and recipes data if the user has selected it
                if (zipEntry.name == "data/groceries.json" && importGroceries) {
                    groceriesJson = getStringFromZipEntry(zipIn)
                    Log.d("Import", "Imported Groceries: $groceriesJson")
                }
                if (zipEntry.name == "data/recipes.json" && importRecipes) {
                    recipesJson = getStringFromZipEntry(zipIn)
                    Log.d("Import", "Imported Recipes: $recipesJson")
                }
                // If the zip entry is an image
                else if (zipEntry.name.startsWith("images/") && importRecipes) {
                    Log.d("Import", "Importing Images")

                    // Create new image file
                    val file = File(context.filesDir, zipEntry.name.removePrefix("images/"))
                    if (file.exists())
                        file.delete()
                    if (!file.createNewFile()) {
                        Log.e("Import", "Failed to create file: ${file.name}")
                    }
                    // Copy image data into the new file
                    file.outputStream().use { output ->
                        zipIn.copyTo(output)
                    }
                    images.add(file)
                }

                zipIn.closeEntry()
                zipEntry = zipIn.nextEntry
            }
        }
    }
    var importedImages = ""
    images.forEach {
        importedImages += "${it.name}\n"
    }
    Log.d("Import", "Imported Images: $importedImages")

    // Decode the strings into the data classes
    var groceries: SaveableGroceryItemCategories? = null
    var recipes: SaveableRecipes? = null
    if (importGroceries) {
        try {
            groceries = Json.decodeFromString<SaveableGroceryItemCategories>(groceriesJson)
        } catch (e: Exception) {
            Log.e("Import", "Failed to decode groceries: $e")
        }
    }
    if (importRecipes) {
        try {
            recipes = Json.decodeFromString<SaveableRecipes>(recipesJson)
        } catch (e: Exception) {
            Log.e("Import", "Failed to decode recipes: $e")
        }
    }

    return ImportData(recipes, groceries, if (importRecipes) images else null)
}
// Gets a string from a zip entry
private fun getStringFromZipEntry(zipIn: ZipInputStream): String {
    val buffer = ByteArray(1024)
    val outputStream = ByteArrayOutputStream()
    var len: Int
    while (zipIn.read(buffer).also { len = it } > 0) {
        outputStream.write(buffer, 0, len)
    }
    return String(outputStream.toByteArray())
}