package com.jule.food.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.jule.food.data.ColorSetting

//region Green
private val GreenColorSchemeLight = lightColorScheme(
    primary = GreenPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = GreenPrimaryContainerLight,
    onPrimaryContainer = GreenOnPrimaryContainerLight,
    secondary = GreenSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = GreenSecondaryContainerLight,
    onSecondaryContainer = GreenOnSecondaryContainerLight,
    tertiary = GreenTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = GreenTertiaryContainerLight,
    onTertiaryContainer = GreenOnTertiaryContainerLight,
    error = GreenErrorLight,
    background = GreenBackgroundLight,
    onBackground = GreenOnBackgroundLight,
    surface = GreenBackgroundLight,
    onSurface = GreenOnBackgroundLight,
    surfaceVariant = GreenSurfaceVariantLight,
    onSurfaceVariant = GreenOnSurfaceVariantLight,
)

private val GreenColorSchemeDark = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = GreenOnPrimaryDark,
    primaryContainer = GreenPrimaryContainerDark,
    onPrimaryContainer = GreenOnPrimaryContainerDark,
    secondary = GreenSecondaryDark,
    onSecondary = GreenOnSecondaryDark,
    secondaryContainer = GreenSecondaryContainerDark,
    onSecondaryContainer = GreenOnSecondaryContainerDark,
    tertiary = GreenTertiaryDark,
    onTertiary = GreenOnTertiaryDark,
    tertiaryContainer = GreenTertiaryContainerDark,
    onTertiaryContainer = GreenOnTertiaryContainerDark,
    error = GreenErrorDark,
    background = GreenBackgroundDark,
    onBackground = GreenOnBackgroundDark,
    surface = GreenBackgroundDark,
    onSurface = GreenOnBackgroundDark,
    surfaceVariant = GreenSurfaceVariantDark,
    onSurfaceVariant = GreenOnSurfaceVariantDark,
)
//endregion

//region Blue1
private val Blue1ColorSchemeLight = lightColorScheme(
    primary = Blue1primaryLight,
    onPrimary = Color.White,
    primaryContainer = Blue1primaryContainerLight,
    onPrimaryContainer = Blue1onPrimaryContainerLight,
    secondary = Blue1secondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Blue1secondaryContainerLight,
    onSecondaryContainer = Blue1onSecondaryContainerLight,
    tertiary = Blue1tertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Blue1tertiaryContainerLight,
    onTertiaryContainer = Blue1onTertiaryContainerLight,
    error = Blue1errorLight,
    background = Blue1backgroundLight,
    onBackground = Blue1onBackgroundLight,
    surface = Blue1backgroundLight,
    onSurface = Blue1onBackgroundLight,
    surfaceVariant = Blue1surfaceVariantLight,
    onSurfaceVariant = Blue1onSurfaceVariantLight,
)

private val Blue1ColorSchemeDark = darkColorScheme(
    primary = Blue1primaryDark,
    onPrimary = Blue1onPrimaryDark,
    primaryContainer = Blue1primaryContainerDark,
    onPrimaryContainer = Blue1onPrimaryContainerDark,
    secondary = Blue1secondaryDark,
    onSecondary = Blue1onSecondaryDark,
    secondaryContainer = Blue1secondaryContainerDark,
    onSecondaryContainer = Blue1onSecondaryContainerDark,
    tertiary = Blue1tertiaryDark,
    onTertiary = Blue1onTertiaryDark,
    tertiaryContainer = Blue1tertiaryContainerDark,
    onTertiaryContainer = Blue1onTertiaryContainerDark,
    error = Blue1errorDark,
    background = Blue1backgroundDark,
    onBackground = Blue1onBackgroundDark,
    surface = Blue1backgroundDark,
    onSurface = Blue1onBackgroundDark,
    surfaceVariant = Blue1surfaceVariantDark,
    onSurfaceVariant = Blue1onSurfaceVariantDark,
)
//endregion

//region Blue2
private val Blue2ColorSchemeLight = lightColorScheme(
    primary = Blue2primaryLight,
    onPrimary = Color.White,
    primaryContainer = Blue2primaryContainerLight,
    onPrimaryContainer = Blue2onPrimaryContainerLight,
    secondary = Blue2secondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Blue2secondaryContainerLight,
    onSecondaryContainer = Blue2onSecondaryContainerLight,
    tertiary = Blue2tertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Blue2tertiaryContainerLight,
    onTertiaryContainer = Blue2onTertiaryContainerLight,
    error = Blue2errorLight,
    background = Blue2backgroundLight,
    onBackground = Blue2onBackgroundLight,
    surface = Blue2backgroundLight,
    onSurface = Blue2onBackgroundLight,
    surfaceVariant = Blue2surfaceVariantLight,
    onSurfaceVariant = Blue2onSurfaceVariantLight,
)

private val Blue2ColorSchemeDark = darkColorScheme(
    primary = Blue2primaryDark,
    onPrimary = Blue2onPrimaryDark,
    primaryContainer = Blue2primaryContainerDark,
    onPrimaryContainer = Blue2onPrimaryContainerDark,
    secondary = Blue2secondaryDark,
    onSecondary = Blue2onSecondaryDark,
    secondaryContainer = Blue2secondaryContainerDark,
    onSecondaryContainer = Blue2onSecondaryContainerDark,
    tertiary = Blue2tertiaryDark,
    onTertiary = Blue2onTertiaryDark,
    tertiaryContainer = Blue2tertiaryContainerDark,
    onTertiaryContainer = Blue2onTertiaryContainerDark,
    error = Blue2errorDark,
    background = Blue2backgroundDark,
    onBackground = Blue2onBackgroundDark,
    surface = Blue2backgroundDark,
    onSurface = Blue2onBackgroundDark,
    surfaceVariant = Blue2surfaceVariantDark,
    onSurfaceVariant = Blue2onSurfaceVariantDark,
)
//endregion

//region Blue3
private val Blue3ColorSchemeLight = lightColorScheme(
    primary = Blue3primaryLight,
    onPrimary = Color.White,
    primaryContainer = Blue3primaryContainerLight,
    onPrimaryContainer = Blue3onPrimaryContainerLight,
    secondary = Blue3secondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Blue3secondaryContainerLight,
    onSecondaryContainer = Blue3onSecondaryContainerLight,
    tertiary = Blue3tertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Blue3tertiaryContainerLight,
    onTertiaryContainer = Blue3onTertiaryContainerLight,
    error = Blue3errorLight,
    background = Blue3backgroundLight,
    onBackground = Blue3onBackgroundLight,
    surface = Blue3backgroundLight,
    onSurface = Blue3onBackgroundLight,
    surfaceVariant = Blue3surfaceVariantLight,
    onSurfaceVariant = Blue3onSurfaceVariantLight,
)

private val Blue3ColorSchemeDark = darkColorScheme(
    primary = Blue3primaryDark,
    onPrimary = Blue3onPrimaryDark,
    primaryContainer = Blue3primaryContainerDark,
    onPrimaryContainer = Blue3onPrimaryContainerDark,
    secondary = Blue3secondaryDark,
    onSecondary = Blue3onSecondaryDark,
    secondaryContainer = Blue3secondaryContainerDark,
    onSecondaryContainer = Blue3onSecondaryContainerDark,
    tertiary = Blue3tertiaryDark,
    onTertiary = Blue3onTertiaryDark,
    tertiaryContainer = Blue3tertiaryContainerDark,
    onTertiaryContainer = Blue3onTertiaryContainerDark,
    error = Blue3errorDark,
    background = Blue3backgroundDark,
    onBackground = Blue3onBackgroundDark,
    surface = Blue3backgroundDark,
    onSurface = Blue3onBackgroundDark,
    surfaceVariant = Blue3surfaceVariantDark,
    onSurfaceVariant = Blue3onSurfaceVariantDark,
)
//endregion

//region Yellow
private val YellowColorSchemeLight = lightColorScheme(
    primary = YellowprimaryLight,
    onPrimary = Color.White,
    primaryContainer = YellowprimaryContainerLight,
    onPrimaryContainer = YellowonPrimaryContainerLight,
    secondary = YellowsecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = YellowsecondaryContainerLight,
    onSecondaryContainer = YellowonSecondaryContainerLight,
    tertiary = YellowtertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = YellowtertiaryContainerLight,
    onTertiaryContainer = YellowonTertiaryContainerLight,
    error = YellowerrorLight,
    background = YellowbackgroundLight,
    onBackground = YellowonBackgroundLight,
    surface = YellowbackgroundLight,
    onSurface = YellowonBackgroundLight,
    surfaceVariant = YellowsurfaceVariantLight,
    onSurfaceVariant = YellowonSurfaceVariantLight,
)

private val YellowColorSchemeDark = darkColorScheme(
    primary = YellowprimaryDark,
    onPrimary = YellowonPrimaryDark,
    primaryContainer = YellowprimaryContainerDark,
    onPrimaryContainer = YellowonPrimaryContainerDark,
    secondary = YellowsecondaryDark,
    onSecondary = YellowonSecondaryDark,
    secondaryContainer = YellowsecondaryContainerDark,
    onSecondaryContainer = YellowonSecondaryContainerDark,
    tertiary = YellowtertiaryDark,
    onTertiary = YellowonTertiaryDark,
    tertiaryContainer = YellowtertiaryContainerDark,
    onTertiaryContainer = YellowonTertiaryContainerDark,
    error = YellowerrorDark,
    background = YellowbackgroundDark,
    onBackground = YellowonBackgroundDark,
    surface = YellowbackgroundDark,
    onSurface = YellowonBackgroundDark,
    surfaceVariant = YellowsurfaceVariantDark,
    onSurfaceVariant = YellowonSurfaceVariantDark,
)
//endregion

//region Red1
private val Red1ColorSchemeLight = lightColorScheme(
    primary = Red1primaryLight,
    onPrimary = Color.White,
    primaryContainer = Red1primaryContainerLight,
    onPrimaryContainer = Red1onPrimaryContainerLight,
    secondary = Red1secondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Red1secondaryContainerLight,
    onSecondaryContainer = Red1onSecondaryContainerLight,
    tertiary = Red1tertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Red1tertiaryContainerLight,
    onTertiaryContainer = Red1onTertiaryContainerLight,
    error = Red1errorLight,
    background = Red1backgroundLight,
    onBackground = Red1onBackgroundLight,
    surface = Red1backgroundLight,
    onSurface = Red1onBackgroundLight,
    surfaceVariant = Red1surfaceVariantLight,
    onSurfaceVariant = Red1onSurfaceVariantLight,
)

private val Red1ColorSchemeDark = darkColorScheme(
    primary = Red1primaryDark,
    onPrimary = Red1onPrimaryDark,
    primaryContainer = Red1primaryContainerDark,
    onPrimaryContainer = Red1onPrimaryContainerDark,
    secondary = Red1secondaryDark,
    onSecondary = Red1onSecondaryDark,
    secondaryContainer = Red1secondaryContainerDark,
    onSecondaryContainer = Red1onSecondaryContainerDark,
    tertiary = Red1tertiaryDark,
    onTertiary = Red1onTertiaryDark,
    tertiaryContainer = Red1tertiaryContainerDark,
    onTertiaryContainer = Red1onTertiaryContainerDark,
    error = Red1errorDark,
    background = Red1backgroundDark,
    onBackground = Red1onBackgroundDark,
    surface = Red1backgroundDark,
    onSurface = Red1onBackgroundDark,
    surfaceVariant = Red1surfaceVariantDark,
    onSurfaceVariant = Red1onSurfaceVariantDark,
)
//endregion

//region Red2
private val Red2ColorSchemeLight = lightColorScheme(
    primary = Red2primaryLight,
    onPrimary = Color.White,
    primaryContainer = Red2primaryContainerLight,
    onPrimaryContainer = Red2onPrimaryContainerLight,
    secondary = Red2secondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Red2secondaryContainerLight,
    onSecondaryContainer = Red2onSecondaryContainerLight,
    tertiary = Red2tertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Red2tertiaryContainerLight,
    onTertiaryContainer = Red2onTertiaryContainerLight,
    error = Red2errorLight,
    background = Red2backgroundLight,
    onBackground = Red2onBackgroundLight,
    surface = Red2backgroundLight,
    onSurface = Red2onBackgroundLight,
    surfaceVariant = Red2surfaceVariantLight,
    onSurfaceVariant = Red2onSurfaceVariantLight,
)

private val Red2ColorSchemeDark = darkColorScheme(
    primary = Red2primaryDark,
    onPrimary = Red2onPrimaryDark,
    primaryContainer = Red2primaryContainerDark,
    onPrimaryContainer = Red2onPrimaryContainerDark,
    secondary = Red2secondaryDark,
    onSecondary = Red2onSecondaryDark,
    secondaryContainer = Red2secondaryContainerDark,
    onSecondaryContainer = Red2onSecondaryContainerDark,
    tertiary = Red2tertiaryDark,
    onTertiary = Red2onTertiaryDark,
    tertiaryContainer = Red2tertiaryContainerDark,
    onTertiaryContainer = Red2onTertiaryContainerDark,
    error = Red2errorDark,
    background = Red2backgroundDark,
    onBackground = Red2onBackgroundDark,
    surface = Red2backgroundDark,
    onSurface = Red2onBackgroundDark,
    surfaceVariant = Red2surfaceVariantDark,
    onSurfaceVariant = Red2onSurfaceVariantDark,
)
//endregion

//region Pink
private val PinkColorSchemeLight = lightColorScheme(
    primary = PinkprimaryLight,
    onPrimary = Color.White,
    primaryContainer = PinkprimaryContainerLight,
    onPrimaryContainer = PinkonPrimaryContainerLight,
    secondary = PinksecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = PinksecondaryContainerLight,
    onSecondaryContainer = PinkonSecondaryContainerLight,
    tertiary = PinktertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = PinktertiaryContainerLight,
    onTertiaryContainer = PinkonTertiaryContainerLight,
    error = PinkerrorLight,
    background = PinkbackgroundLight,
    onBackground = PinkonBackgroundLight,
    surface = PinkbackgroundLight,
    onSurface = PinkonBackgroundLight,
    surfaceVariant = PinksurfaceVariantLight,
    onSurfaceVariant = PinkonSurfaceVariantLight,
)

private val PinkColorSchemeDark = darkColorScheme(
    primary = PinkprimaryDark,
    onPrimary = PinkonPrimaryDark,
    primaryContainer = PinkprimaryContainerDark,
    onPrimaryContainer = PinkonPrimaryContainerDark,
    secondary = PinksecondaryDark,
    onSecondary = PinkonSecondaryDark,
    secondaryContainer = PinksecondaryContainerDark,
    onSecondaryContainer = PinkonSecondaryContainerDark,
    tertiary = PinktertiaryDark,
    onTertiary = PinkonTertiaryDark,
    tertiaryContainer = PinktertiaryContainerDark,
    onTertiaryContainer = PinkonTertiaryContainerDark,
    error = PinkerrorDark,
    background = PinkbackgroundDark,
    onBackground = PinkonBackgroundDark,
    surface = PinkbackgroundDark,
    onSurface = PinkonBackgroundDark,
    surfaceVariant = PinksurfaceVariantDark,
    onSurfaceVariant = PinkonSurfaceVariantDark,
)
//endregion


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    colorSetting: ColorSetting = ColorSetting.Dynamic,
    content: @Composable () -> Unit
) {
    val colorScheme = when (colorSetting) {
        ColorSetting.Dynamic ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                if (darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
            else
                if (darkTheme) GreenColorSchemeDark else GreenColorSchemeLight
        ColorSetting.Green -> if (darkTheme) GreenColorSchemeDark else GreenColorSchemeLight
        ColorSetting.Blue1 -> if (darkTheme) Blue1ColorSchemeDark else Blue1ColorSchemeLight
        ColorSetting.Blue2 -> if (darkTheme) Blue2ColorSchemeDark else Blue2ColorSchemeLight
        ColorSetting.Blue3 -> if (darkTheme) Blue3ColorSchemeDark else Blue3ColorSchemeLight
        ColorSetting.Yellow -> if (darkTheme) YellowColorSchemeDark else YellowColorSchemeLight
        ColorSetting.Red1 -> if (darkTheme) Red1ColorSchemeDark else Red1ColorSchemeLight
        ColorSetting.Red2 -> if (darkTheme) Red2ColorSchemeDark else Red2ColorSchemeLight
        ColorSetting.Pink -> if (darkTheme) PinkColorSchemeDark else PinkColorSchemeLight
    }
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}