package com.jule.food

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconButtonWithTooltip(
    onClick: () -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick, content = icon, enabled = enabled)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilledIconButtonWithTooltip(
    onClick: () -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    shape: Shape = IconButtonDefaults.filledShape,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
            FilledIconButton(onClick = onClick, colors = colors, shape = shape, content = icon, enabled = enabled)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilledExpressiveIconButtonWithTooltip(
    onClick: () -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    shapes: IconButtonShapes = IconButtonDefaults.shapes(),
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        FilledIconButton(onClick = onClick, colors = colors, shapes = shapes, content = icon, enabled = enabled)
    }
}