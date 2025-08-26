package com.jule.food

import android.os.Build
import android.preference.CheckBoxPreference
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.ui.theme.primaryColorsDark
import com.jule.food.ui.theme.primaryColorsLight

enum class ImportSetting { Recipe, Groceries, Both }
class ImportResult (val groceries: Int?, val recipes: Int?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    themeSetting: ThemeSetting,
    onChangeTheme: (ThemeSetting) -> Unit,
    colorSetting: ColorSetting,
    onChangeColor: (ColorSetting) -> Unit,
    language: Languages,
    onChangeLanguage: (Languages) -> Unit,
    onBack: () -> Unit,
    onPickFile: () -> Unit,
    onExport: () -> Unit,
    importingFile: String?,
    onCancelImport: () -> Unit,
    onStartImport: (ImportSetting) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings), textAlign = TextAlign.Center) },
                navigationIcon = { IconButton(onClick = onBack, modifier = Modifier.size(50.dp)) { Icon(painter = painterResource(R.drawable.arrow_left), contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val isImportingFile = importingFile != null
        val context = LocalContext.current

        var dynamicTheme by remember { mutableStateOf<ColorScheme?>(null)}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LaunchedEffect(darkTheme) {
                dynamicTheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }

        Column(modifier = Modifier.padding(innerPadding), verticalArrangement = Arrangement.spacedBy(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SettingsScreenCategory(name = stringResource(R.string.appearance)) {
                SelectTheme(currentThemeSetting = themeSetting, onChangeTheme = onChangeTheme, darkTheme = darkTheme)
                SelectColor(currentColorSetting = colorSetting, onChangeColor = onChangeColor, darkTheme = darkTheme, dynamicTheme = dynamicTheme)
            }
            SettingsScreenCategory(name = stringResource(R.string.language)) {
                SelectLanguage(currentLanguage = language, onChangeLanguage = onChangeLanguage)
            }
            SettingsScreenCategory(name = stringResource(R.string.data)) {
                Setting(icon = { Icon(painter = painterResource(id = R.drawable.import_data), contentDescription = "Import") }, name = stringResource(R.string.import_data), onClick = onPickFile)
                Setting(icon = { Icon(painter = painterResource(id = R.drawable.export_data), contentDescription = "Export") }, name = stringResource(R.string.export), onClick = onExport)
            }
        }

        var recipeChecked by remember { mutableStateOf(true) }
        var groceriesChecked by remember { mutableStateOf(true) }
        if (isImportingFile) {
            DefaultDialog(
                title = stringResource(R.string.import_data),
                onDismissRequest = onCancelImport,
                buttons = true,
                confirmEnabled = recipeChecked || groceriesChecked,
                onConfirm = { onStartImport(if (recipeChecked && groceriesChecked) ImportSetting.Both else if (recipeChecked) ImportSetting.Recipe else ImportSetting.Groceries) }
            ) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(5.dp)) {
                        Icon(painter = painterResource(id = R.drawable.zip_folder), contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(importingFile ?: "")
                    }
                }
                Column() {
                    DialogCheckbox(checked = groceriesChecked, onCheckedChange = { groceriesChecked = it }, label = stringResource(R.string.groceries), modifier = Modifier.width(200.dp))
                    DialogCheckbox(checked = recipeChecked, onCheckedChange = { recipeChecked = it }, label = stringResource(R.string.recipes), modifier = Modifier.width(200.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsScreenCategory (
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.padding(start = 10.dp))
        content()
    }
}

@Composable
fun DialogCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.clickable { onCheckedChange(!checked) }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) }
        )
        Text(label)
    }
}

@Composable
fun Setting(
    icon: @Composable () -> Unit,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentSetting: String? = null
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick).fillMaxWidth().height(65.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(15.dp), verticalAlignment = Alignment.CenterVertically) {
            icon()

            Column() {
                Text(name)
                if (currentSetting != null) {
                    Text(currentSetting, style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
    }
}
@Composable
fun SettingDialogElement(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        enabled = !selected,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20),
        modifier = modifier.width(250.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(start = 10.dp, top = 5.dp, bottom = 5.dp).height(48.dp).fillMaxWidth()) {
            Box(modifier = Modifier.width(24.dp)) {
                leadingIcon?.invoke()
            }
            Column() {
                Text(text = title)
                if (description != null) {
                    Text(text = description, style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
    }
}
@Composable
fun SettingDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    DefaultDialog(title = title, onDismissRequest = onDismissRequest, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            content()
        }
    }
}



@Composable
fun SelectTheme(
    currentThemeSetting: ThemeSetting,
    onChangeTheme: (ThemeSetting) -> Unit,
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }


    Setting(
        icon = { Icon(painter = painterResource(id = if (darkTheme) R.drawable.moon else R.drawable.sun), contentDescription = "Theme") },
        name = stringResource(R.string.theme),
        currentSetting = stringResource(themeSettingDisplay[currentThemeSetting]!!),
        modifier = modifier,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        SettingDialog(title = stringResource(R.string.theme), onDismissRequest = { showDialog = false})
        {
            ThemeSetting.entries.forEach { setting ->
                val leadingIcon: @Composable (() -> Unit)? = when (setting) {
                    ThemeSetting.System -> { { Icon(painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.moon else R.drawable.sun), contentDescription = null) } }
                    else -> { { Icon(painter = painterResource(id = if (setting == ThemeSetting.Dark) R.drawable.moon else R.drawable.sun), contentDescription = null) } }
                }

                SettingDialogElement(title = stringResource(themeSettingDisplay[setting]!!), selected = currentThemeSetting == setting, onClick = {
                    showDialog = false
                    onChangeTheme(setting)
                }, leadingIcon = leadingIcon)

//                DropdownMenuItem(
//                    leadingIcon = leadingIcon,
//                    text = { Text(stringResource(themeSettingDisplay[setting]!!))},
//                    onClick = {
//                        showDialog = false
//                        onChangeTheme(setting)
//                    },
//                    trailingIcon = { if (currentThemeSetting == setting) Icon(imageVector = Icons.Default.Done, contentDescription = "Selected") }
//                )
            }
        }
    }
}


@Composable
fun SelectColor(
    darkTheme: Boolean,
    currentColorSetting: ColorSetting,
    onChangeColor: (ColorSetting) -> Unit,
    modifier: Modifier = Modifier,
    dynamicTheme: ColorScheme? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedColor: Color =
        if (currentColorSetting == ColorSetting.Dynamic) {
            dynamicTheme?.primary ?: Color.Magenta
        }
        else {
            (if (darkTheme) primaryColorsDark[currentColorSetting] else primaryColorsLight[currentColorSetting]) ?: Color.Magenta
        }

    Setting(icon = { Icon(painter = painterResource(id = R.drawable.palette), contentDescription = "Color", tint = selectedColor) }, name = stringResource(R.string.colors), onClick = { showDialog = true }, currentSetting = stringResource(colorSettingDisplay[currentColorSetting]!!), modifier = modifier)
    if (showDialog)
    {
        SettingDialog(title = stringResource(R.string.colors), onDismissRequest = { showDialog = false}) {
            ColorSetting.entries.forEach { setting ->
                if (setting == ColorSetting.Dynamic && dynamicTheme == null) {
                    return@forEach
                }
                val color: Color =
                    if (setting == ColorSetting.Dynamic) {
                        dynamicTheme?.primary ?: Color.Magenta
                    } else {
                        (if (darkTheme) primaryColorsDark[setting] else primaryColorsLight[setting])
                            ?: Color.Magenta
                    }
                SettingDialogElement(
                    leadingIcon = {
                        Box(modifier = Modifier.size(24.dp)) {
                            Box(
                                modifier = Modifier.size(18.dp).align(Alignment.Center).background(color = color, shape = RoundedCornerShape(50)).border(1.dp, MaterialTheme.colorScheme.background, RoundedCornerShape(50))
                            )
                        }
                    },
                    title = stringResource(colorSettingDisplay[setting]!!),
                    onClick = {
                        showDialog = false
                        onChangeColor(setting)
                    },
                    selected = currentColorSetting == setting,
                    description = if (setting == ColorSetting.Dynamic) stringResource(R.string.dynamic_color_description) else null
                )
            }
        }
    }
}


@Composable
fun SelectLanguage(
    currentLanguage: Languages,
    onChangeLanguage: (Languages) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Setting(icon = { Icon(painter = painterResource(id = R.drawable.language), contentDescription = "Language") }, name = stringResource(R.string.language), onClick = { showDialog = true }, currentSetting = stringResource(languageSettingsDisplay[currentLanguage]!!), modifier = modifier)

    if (showDialog) {
        SettingDialog(title = stringResource(R.string.language), onDismissRequest = { showDialog = false}) {
            Languages.entries.forEach { setting ->
                SettingDialogElement(
                    leadingIcon = { Image(painter = painterResource(id = languageSettingsFlags[setting]!!), contentDescription = null, modifier = Modifier.size(20.dp))},
//                    leadingIcon = { Spacer(modifier = Modifier.width(24.dp)) },
                    title = stringResource(languageSettingsDisplay[setting]!!),
                    onClick = {
                        showDialog = false
                        onChangeLanguage(setting)
                    },
                    selected = currentLanguage == setting
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsPreview() {

    var themeSetting by remember { mutableStateOf(ThemeSetting.Dark) }
    val darkTheme = when (themeSetting) {
        ThemeSetting.System -> isSystemInDarkTheme()
        ThemeSetting.Light -> false
        ThemeSetting.Dark -> true
    }
    var colorSetting by remember { mutableStateOf(ColorSetting.Dynamic) }
    var language by remember { mutableStateOf(Languages.English) }
    val navController = rememberNavController()
    FoodTheme(darkTheme = darkTheme, colorSetting = colorSetting) {
        SettingsScreen(darkTheme = darkTheme, themeSetting = themeSetting, onPickFile = { }, onExport = {}, onChangeTheme = { themeSetting = it }, colorSetting = colorSetting, onChangeColor = { colorSetting = it }, language = language, onChangeLanguage = { language = it }, bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = viewModel()) }, onBack = {}, importingFile = null, onCancelImport = {}, onStartImport = {})
    }
}