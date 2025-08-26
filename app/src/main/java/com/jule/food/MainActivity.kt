package com.jule.food

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
//import reorderable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastFirstOrNull
import androidx.core.os.LocaleListCompat
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.read

class MainActivity : AppCompatActivity() {
// Import
    private var importingFileName = "Test"
    private var importingFileUri: Uri = Uri.EMPTY
    private var isImportingFile by mutableStateOf(false)
    // Activity for importing a file
    private val requestFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    isImportingFile = true
                    importingFileUri = uri
                    importingFileName = getFileNameFromUri(uri)
                }
            }
        }

    private fun openSpecificFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"  // Set the type to specifically check for .zip files
            val uri = Uri.parse("content://com.android.externalstorage.documents/document/primary")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }

        requestFileLauncher.launch(intent)
    }
// Get the file name of the imported file to display to user
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
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

//  Export
    private lateinit var exportUri: Uri
    private lateinit var groceryViewModel: GroceryViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    // Activity for exporting all of the data to a .zip file
    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
        if (uri != null) {
            // The user has selected a directory
            exportUri = uri
            // Now you can use exportUri to write the file to the chosen directory

            val groceries = File(filesDir, "groceries.json").apply { writeText(groceryViewModel.getJson())}
            val recipes = File(filesDir, "recipes.json").apply { writeText(recipeViewModel.getJson())}
            val images = createImageFilesFromPaths(recipeViewModel.getImagePaths())
            val zipFile = createZipExportFile(this, listOf(groceries, recipes), images, "test.zip")
            exportFileToLocation(this, zipFile, exportUri)

            Toast.makeText(this, "Exported successfully as \"${exportUri.path}\"", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Canceled Export", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        enableEdgeToEdge()

        // Get the current locale for the system
        val currentLocaleStr = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val currentLocaleEnum = if (currentLocaleStr.startsWith("de")) Languages.German else Languages.English

        setContent {
            val context = this

            groceryViewModel = viewModel()
            recipeViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val navController = rememberNavController()

            // Get all of the data from the app files and preferences
            LaunchedEffect(true) {
                settingsViewModel.getSettingsFromPreferences(context)
                groceryViewModel.getFromFile(context)
                recipeViewModel.getFromFile(context)
            }

            val darkTheme = when (settingsViewModel.themeSetting) {
                ThemeSetting.System -> isSystemInDarkTheme()
                ThemeSetting.Light -> false
                ThemeSetting.Dark -> true
            }

            // Composable for handling when the app stops so the data can be saved
            HandleLifeCycle(settingsViewModel = settingsViewModel, groceryViewModel = groceryViewModel, recipeViewModel = recipeViewModel)

            FoodTheme(darkTheme = darkTheme, colorSetting = settingsViewModel.colorSetting) {
                    NavigationHost(
                        navController = navController,
                        darkTheme = darkTheme,
                        currentTheme = settingsViewModel.themeSetting,
                        onChangeTheme = { settingsViewModel.setThemeSetting(it) },
                        currentColor = settingsViewModel.colorSetting,
                        onChangeColor = { settingsViewModel.setColorSetting(it) },
                        language = currentLocaleEnum,
                        onChangeLanguage = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeOptions[it])) },
                        groceryViewModel = groceryViewModel,
                        recipeViewModel = recipeViewModel,
                        onPickFile = { openSpecificFolder() },
                        onExport = { createFileLauncher.launch("export_food.zip") },
                        bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = recipeViewModel) },
                        importingFile = if (isImportingFile) importingFileName else null,
                        onCancelImport = { isImportingFile = false },
                        onStartImport = { importSetting ->
                            isImportingFile = false
                            val importResult = importFromZipFile(context, importingFileUri, groceryViewModel, recipeViewModel, importSetting)
                            showImportResult(context, importResult)
                        },
                        groceryCategories = groceryViewModel.groceryItemCategories,
                        addToGroceries = { groceries, categoryId, recipeId ->
                            groceryViewModel.addToGroceries(groceries, categoryId, recipeId, context)
                        },
                        onDeleteRecipeImage = { id, path -> Log.d("onDeleteRecipeImage", "Request to delete at Recipe: $id, Path: $path") }
                    )
            }
        }
    }
}

// Function for importing both groceries and recipes from a zip file into the view models
private fun importFromZipFile(context: Context, uri: Uri, groceryViewModel: GroceryViewModel, recipeViewModel: RecipeViewModel, setting: ImportSetting): ImportResult {
    val importGroceries = setting == ImportSetting.Groceries || setting == ImportSetting.Both
    val importRecipes = setting == ImportSetting.Recipe || setting == ImportSetting.Both
    val importData = getImportDataFromFile(context, uri, setting)

    var groceries: Int? = null
    var recipes: Int? = null

    if (importGroceries && importData.groceries != null) {
        groceryViewModel.import(importData.groceries)
        groceries = 0
        importData.groceries.categories.forEach {
            groceries += it.items.size
        }
    }
    if (importRecipes && importData.recipes != null) {
        recipeViewModel.deleteImageFiles()
        recipeViewModel.import(importData.recipes)
        recipes = importData.recipes.recipes.size
    }

    return ImportResult(groceries, recipes)
}

// Shows the result of the import as a toast
fun showImportResult(context: Context, importResult: ImportResult) {
    val groceries = importResult.groceries != null
    val recipes = importResult.recipes != null
    val message =
        if (!groceries && !recipes) {
            context.getString(R.string.import_error)
        } else if (groceries && !recipes) {
            context.getString(R.string.import_groceries, importResult.groceries)
        } else if (!groceries) {
            context.getString(R.string.import_recipes, importResult.recipes)
        } else {
            context.getString(R.string.import_groceries_and_recipes, importResult.groceries, importResult.recipes)
        }

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    val navController = rememberNavController()

    var themeSetting by remember { mutableStateOf(ThemeSetting.Light) }
    val darkTheme = when (themeSetting) {
        ThemeSetting.System -> isSystemInDarkTheme()
        ThemeSetting.Light -> false
        ThemeSetting.Dark -> true
    }

    var colorSetting by remember { mutableStateOf(ColorSetting.Dynamic) }
    var language by remember { mutableStateOf(Languages.English) }

    val groceryViewModel: GroceryViewModel = viewModel()
    groceryViewModel.initializeEmpty()

    FoodTheme(darkTheme = darkTheme) {
        NavigationHost(navController = navController, onPickFile = {}, onExport = {}, darkTheme = darkTheme, bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = viewModel()) }, currentTheme = themeSetting, onChangeTheme = {themeSetting = it}, currentColor = colorSetting, onChangeColor = { colorSetting = it }, language = language, onChangeLanguage = {language = it}, importingFile = null, onCancelImport = {}, onStartImport = {}, addToGroceries = { _, _, _ ->}, groceryCategories = listOf(), onDeleteRecipeImage = { _, _ ->}, groceryViewModel = groceryViewModel)
    }
}

