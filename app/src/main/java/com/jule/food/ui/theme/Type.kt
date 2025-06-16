package com.jule.food.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jule.food.R

@OptIn(ExperimentalTextApi::class)
val displayLargeFontFamily = FontFamily(
    Font(R.font.roboto_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(950),
            FontVariation.width(30f),
            FontVariation.slant(-6f),
        )
    )
)
@OptIn(ExperimentalTextApi::class)
val displaySmallFontFamily = FontFamily(
    Font(R.font.roboto_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.width(25f),
            FontVariation.slant(-6f),
            FontVariation.Setting("XOPQ", 175f),
            FontVariation.Setting("YOPQ", 135f),
        )
    )
)
// Set of Material typography styles to start with
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayLargeEmphasized = TextStyle(
        fontFamily = displayLargeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displaySmallEmphasized = TextStyle(
        fontFamily = displaySmallFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)