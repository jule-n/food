package com.jule.food

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.jule.food.ui.theme.Blue1
import com.jule.food.ui.theme.Blue2
import com.jule.food.ui.theme.Blue3
import com.jule.food.ui.theme.Green
import com.jule.food.ui.theme.Pink
import com.jule.food.ui.theme.Red1
import com.jule.food.ui.theme.Red2
import com.jule.food.ui.theme.Yellow


enum class ThemeSetting { System, Light, Dark }
enum class ColorSetting { Dynamic, Green,  Blue1, Blue2, Blue3, Yellow, Red1, Red2, Pink}

enum class Languages { German, English }

val localeOptions = mapOf(
    Languages.English to "en",
    Languages.German to "de"
)

val themeSettingDisplay = mapOf(
    ThemeSetting.System to R.string.system,
    ThemeSetting.Dark to R.string.dark,
    ThemeSetting.Light to R.string.light
)

val colorSettingColors = mapOf(
    ColorSetting.Green to Green,
    ColorSetting.Blue1 to Blue1,
    ColorSetting.Blue2 to Blue2,
    ColorSetting.Blue3 to Blue3,
    ColorSetting.Yellow to Yellow,
    ColorSetting.Red1 to Red1,
    ColorSetting.Red2 to Red2,
    ColorSetting.Pink to Pink,
)

val colorSettingDisplay = mapOf(
    ColorSetting.Dynamic to R.string.dynamic,
    ColorSetting.Green to R.string.green,
    ColorSetting.Blue1 to R.string.blue1,
    ColorSetting.Blue2 to R.string.blue2,
    ColorSetting.Blue3 to R.string.blue3,
    ColorSetting.Yellow to R.string.yellow,
    ColorSetting.Red1 to R.string.red1,
    ColorSetting.Red2 to R.string.red2,
    ColorSetting.Pink to R.string.pink,
)

val languageSettingsDisplay = mapOf(
    Languages.English to R.string.english,
    Languages.German to R.string.german,
)
val languageSettingsFlags = mapOf(
    Languages.English to R.drawable.flag_uk,
    Languages.German to R.drawable.flag_germany
)

class SettingsViewModel : ViewModel() {
    private var gotFromPreferences = false

    private var _themeSetting by mutableStateOf<ThemeSetting>(ThemeSetting.System)
    val themeSetting get() = _themeSetting

    private var _colorSetting by mutableStateOf<ColorSetting>(ColorSetting.Dynamic)
    val colorSetting get() = _colorSetting

//    val themeSetting: MutableState<ThemeSetting> = mutableStateOf<ThemeSetting>(ThemeSetting.System)

    fun setThemeSetting(newThemeSetting: ThemeSetting) {
        Log.d("setThemeSetting", "New Theme Setting: $newThemeSetting")
        _themeSetting = newThemeSetting
    }
    fun setColorSetting(newColorSetting: ColorSetting) {
        Log.d("setColorSetting", "New Color Setting: $newColorSetting")
        _colorSetting = newColorSetting
    }
    fun getSettingsFromPreferences(context: Context) {
        if (gotFromPreferences) return

        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)

        _themeSetting = prefs.getInt("theme_setting", ThemeSetting.System.ordinal).toEnum<ThemeSetting>() ?: ThemeSetting.System
        _colorSetting = prefs.getInt("color_setting", ColorSetting.Dynamic.ordinal).toEnum<ColorSetting>() ?: ColorSetting.Dynamic
        Log.d("getSettings", "Got Theme Setting: $themeSetting, Color Setting: $colorSetting")
        gotFromPreferences = true

    }
    fun saveSettingsToPreferences(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
        prefs.edit()
            .putInt("theme_setting", themeSetting.toInt())
            .putInt("color_setting", colorSetting.toInt())
            .apply()
        Log.d("saveSettings", "Saved Theme Setting: $themeSetting, Color Setting: $colorSetting")
    }
}