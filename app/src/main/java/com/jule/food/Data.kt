package com.jule.food

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
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

fun writeStringToFile(context: Context, filename: String, string: String) {
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(string.toByteArray())
    }
}

inline fun <reified T> writeJsonToFile(context: Context, filename: String, json: T) {
    val str = Json.encodeToString(value = json)
    Log.d("WriteJsonToFile", str)
    writeStringToFile(context, filename, str)
}

fun getStringFromFile(context: Context, filename: String): String? {
    return try {
        context.openFileInput(filename).bufferedReader().use {
            it.readText()
        }
    } catch (e: FileNotFoundException) {
        null
    }
}
inline fun <reified T> getJsonFromFile(context: Context, fileName: String) : T? {
    val str = getStringFromFile(context, fileName) ?: return null
    Log.d("GetJsonFromFile", str)
    return try {
        Json.decodeFromString<T>(str)
    } catch (e: Exception) {
        null
    }
}

fun getLinesFromFile(context: Context, filename: String, lineFun: (Sequence<String>) -> Unit): Boolean {
    val file = context.getFileStreamPath(filename)
    if (!file.exists()) {
        return false
    }
    context.openFileInput(filename).bufferedReader().useLines { lines -> lineFun(lines) }
    return true

}

fun deleteFile(path: String) {
    val file = File(path)
    if (file.exists()) {
        val isDeleted = file.delete()
        if (isDeleted) {
            Log.d("ImageSaver", "Image deleted: $path")
        } else {
            Log.e("ImageSaver", "Failed to delete image: $path")
        }
    } else {
        Log.w("ImageSaver", "Image file does not exist: $path")
    }
}

fun createZipExportFile(context: Context, dataFiles: List<File>, imageFiles: List<File>, name: String): File {
    val zipFile = File(context.filesDir, name)
    ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
        zipOut.putNextEntry(ZipEntry("data/"))
        zipOut.closeEntry()

        dataFiles.forEach { file ->
            FileInputStream(file).use { fileIn ->
                val zipEntry = ZipEntry("data/${file.name}")
                zipOut.putNextEntry(zipEntry)
                fileIn.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }

        zipOut.putNextEntry(ZipEntry("images/"))

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

fun exportFileToLocation(context: Context, file: File, uri: Uri) {
    val contentResolver = context.contentResolver
    contentResolver.openOutputStream(uri)?.use { outputStream ->
        file.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

fun createImageFilesFromPaths(context: Context, paths: List<String>): List<File> {
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

class ImportData (val recipes: SaveableRecipes?, val groceries: SaveableGroceryItemCategories?, val images: List<File>?)

fun getImportDataFromFile(context: Context, uri: Uri, setting: ImportSetting): ImportData {
    var recipesJson = ""
    var groceriesJson = ""
    val images = mutableListOf<File>()

    val importGroceries = setting == ImportSetting.Groceries || setting == ImportSetting.Both
    val importRecipes = setting == ImportSetting.Recipe || setting == ImportSetting.Both

    Log.d("URI", uri.toString())
    val contentResolver = context.contentResolver
    contentResolver.openInputStream(uri)?.use { input ->
        ZipInputStream(input).use { zipIn ->
            var zipEntry: ZipEntry? = zipIn.nextEntry

            while (zipEntry != null) {
                if (zipEntry.isDirectory) {
                    zipIn.closeEntry()
                    zipEntry = zipIn.nextEntry
                    continue
                }
                Log.d("Import", "Zip Entry: ${zipEntry.name}")

                if (zipEntry.name == "data/groceries.json" && importGroceries) {
                    groceriesJson = getStringFromZipEntry(zipIn)
                    Log.d("Import", "Imported Groceries: $groceriesJson")
                }
                if (zipEntry.name == "data/recipes.json" && importRecipes) {
                    recipesJson = getStringFromZipEntry(zipIn)
                    Log.d("Import", "Imported Recipes: $recipesJson")
                }
                else if (zipEntry.name.startsWith("images/") && importRecipes) {
                    Log.d("Import", "Importing Images")

                    val file = File(context.filesDir, zipEntry.name.removePrefix("images/"))
                    if (file.exists())
                        file.delete()
                    if (!file.createNewFile()) {
                        Log.e("Import", "Failed to create file: ${file.name}")
                    }

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
    var str = ""
    images.forEach {
        str += "${it.name}\n"
    }
    Log.d("Import", "Imported Images: $str")

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
private fun getStringFromZipEntry(zipIn: ZipInputStream): String {
    val buffer = ByteArray(1024)
    val outputStream = ByteArrayOutputStream()
    var len: Int
    while (zipIn.read(buffer).also { len = it } > 0) {
        outputStream.write(buffer, 0, len)
    }
    return String(outputStream.toByteArray())
}