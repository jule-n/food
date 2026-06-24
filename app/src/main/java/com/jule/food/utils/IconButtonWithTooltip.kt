package com.jule.food.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ToggleButtonWithTooltip(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shapes: ToggleButtonShapes = ToggleButtonDefaults.shapes(),
    colors: ToggleButtonColors = ToggleButtonDefaults.toggleButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    TooltipBox(
        modifier = modifier,
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        ToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            shapes = shapes,
            colors = colors,
            enabled = enabled,
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfaceWithTooltip(
    modifier: Modifier = Modifier,
    tooltipText: String,
    onClick: () -> Unit,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    TooltipBox(
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        Surface(
            shape = shape,
            color = color,
            onClick = onClick,
            content = content,
            modifier = modifier
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OutlinedIconButtonWithTooltip(
    onClick: () -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shapes: IconButtonShapes = IconButtonDefaults.shapes(),
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    content: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        tooltip = { PlainTooltip { Text(tooltipText) } },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        state = rememberTooltipState()
    ) {
        OutlinedIconButton(
            onClick = onClick,
            shapes = shapes,
            colors = colors,
            enabled = enabled,
            content = content
        )
    }
}

