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



// Create an export file including several data files and image files in specific folders
//fun createShareGroceriesFile(context: Context, groceries: SaveableGroceryItemCategories, name: String, addToDir: String = ""): File {
//    val jsonFile = File()
//    writeJsonToFile()
//
//    return zipFile
//}