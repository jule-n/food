package com.jule.food

//import reorderable
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import java.io.File

class MainActivity : AppCompatActivity() {
// Import
    private var importingFileName = "Test"
    private var importingFileUri: Uri = Uri.EMPTY
    private var isImportingFile by mutableStateOf(false)

    // Activity for importing a file
    private val requestFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    isImportingFile = true
                    importingFileUri = uri
                    importingFileName = getFileNameFromUri(uri, this)
                }
            }
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

            Toast.makeText(this, "Exported successfully as \"${exportUri.path?.substringAfterLast('/')}\"", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Canceled Export", Toast.LENGTH_SHORT).show()
        }
    }


//    private fun shareZipFile() {
//        Toast.makeText(this, "Creating .zip file. This will take a few seconds...", Toast.LENGTH_LONG).show()
//        val downloadsDir = File(this.filesDir.toString() + "/downloads")
//        if (!downloadsDir.exists()) {
//            if (!downloadsDir.mkdirs()) {
//                Log.e("MainActivity", "Failed to create downloads directory")
//                return
//            }
//            Log.d("MainActivity", "Created downloads directory")
//        }
//        val groceries = File(filesDir, "groceries.json").apply { writeText(groceryViewModel.getJson())}
//        val recipes = File(filesDir, "recipes.json").apply { writeText(recipeViewModel.getJson())}
//        val images = createImageFilesFromPaths(recipeViewModel.getImagePaths())
//        val zipFile = createZipExportFile(this, listOf(groceries, recipes), images, "recipes.zip", addToDir = "/downloads")
//
//        val shareUri = FileProvider.getUriForFile(this, "com.jule.food.fileprovider", zipFile)
//
//        val sendIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "application/zip"
//            putExtra(Intent.EXTRA_STREAM, shareUri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        val shareIntent = Intent.createChooser(sendIntent, null)
//        startActivity(shareIntent, null)
//    }

    var jsonContent: String? by mutableStateOf(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        enableEdgeToEdge()

        // Get the current locale for the system
        val currentLocaleStr = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val currentLocaleEnum = if (currentLocaleStr.startsWith("de")) Languages.German else Languages.English

//        var jsonContent by mutableStateOf( handleJsonIntent(this, intent) )
        val fileUri = handleJsonIntent(intent)
        if (fileUri != null) {
            isImportingFile = true
            importingFileUri = fileUri
            importingFileName = getFileNameFromUri(importingFileUri, this)
        }


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
                        onPickZipFile = { openFolder("application/zip", requestFileLauncher) },
                        onPickJsonFile = { openFolder("application/json", requestFileLauncher) },
                        onExport = { createFileLauncher.launch("export_food.zip") },
                        bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = recipeViewModel) },
                        importingFile = if (isImportingFile) importingFileName else null,
                        onCancelImport = { isImportingFile = false },
                        onStartDataImport = { importSetting ->
                            isImportingFile = false
                            val importResult = importFromZipFile(context, importingFileUri, groceryViewModel, recipeViewModel, importSetting)
                            showImportResult(context, importResult)
                        },
                        onStartJsonImport = {
                            Log.d("onStartJsonImport", "Starting Json import of file: $importingFileName")
                            val json = readJsonFromUri(context, importingFileUri)
                            if (json != null) {
                                jsonContent = json
                            } else {
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        },
//                        importingJsonFile = if (importingJsonFile) importingJsonFileName else null,
                        groceryCategories = groceryViewModel.groceryItemCategories,
                        addToGroceries = { groceries, categoryId, recipeId ->
                            groceryViewModel.addToGroceries(groceries, categoryId, recipeId, context)
                        },
                        importJsonContent = jsonContent,
                        onHandledJsonImport = {
                            jsonContent = null
                            isImportingFile = false
                        }
                    )
            }
        }
    }
    // This gets called if the activity is already running
    // and a new intent arrives
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            val fileUri = handleJsonIntent(intent)
            if (fileUri != null) {
                isImportingFile = true
                importingFileUri = fileUri
                importingFileName = getFileNameFromUri(importingFileUri, this)
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
            context.getString(R.string.imported_groceries, importResult.groceries)
        } else if (!groceries) {
            context.getString(R.string.imported_recipes, importResult.recipes)
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
        NavigationHost(
            navController = navController,
            onExport = {},
            darkTheme = darkTheme,
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    recipeViewModel = viewModel()
                )
            },
            currentTheme = themeSetting,
            onChangeTheme = { themeSetting = it },
            currentColor = colorSetting,
            onChangeColor = { colorSetting = it },
            language = language,
            onChangeLanguage = { language = it },
            importingFile = null,
            onCancelImport = {},
            onStartDataImport = {},
            addToGroceries = { _, _, _ -> },
            groceryCategories = listOf(),
            groceryViewModel = groceryViewModel,
            importJsonContent = null,
            onHandledJsonImport = { },
            onPickZipFile = { },
            onPickJsonFile = { },
            onStartJsonImport =  {},
            recipeViewModel = viewModel()
        )
    }
}

