package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jule.food.ui.theme.FoodTheme




@Composable
fun GroceryGrid(
    groceryItems: List<GroceryItem>,
    onClickItem: (index: Int) -> Unit,
    onLongClickItem: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    center: Boolean = false,
    minSize: Dp = 100.dp,
    itemColor: Color? = null,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    detailTextColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
) {
//    val mainColor = getMainColors(darkTheme)[colorIndex]
//    val accentColor = getAccentColors(darkTheme)[colorIndex]

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(items = groceryItems, key = { _, item -> item.id }) {index, groceryItem ->
//            Box() {

            GroceryItemDisplay(
                item = groceryItem,
                onClick = { onClickItem(index) },
                onLongClick = { onLongClickItem(index) },
                itemColor = itemColor,
                textColor = textColor,
                detailTextColor = detailTextColor,
                center = center,
                modifier = Modifier.animateItem()
            )
//            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroceryItemDisplay(
    item: GroceryItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    center: Boolean = false,
    itemColor: Color? = null,
    itemBrush: Brush ? = null,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    detailTextColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
) {
//    val mainColor = getMainColors(darkTheme)[colorIndex]
//    val accentColor = getAccentColors(darkTheme)[colorIndex]

    val prim1 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.1f)
    val prim2 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.3f)
    val brush = Brush.linearGradient(listOf(prim1, prim2))
    Box(
//        shape = RoundedCornerShape(10),
//        color = MaterialTheme.colorScheme.primary,
//        color = primaryColor,
//        contentColor = textColor,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1.6f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
//            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), RoundedCornerShape(10))
//            .advancedShadow(offsetX = 0.dp, offsetY = 0.dp, blurRadius = 3.dp, roundedCornerRadius = 10.dp)
            .conditional(itemColor == null,
                ifTrue = {
                    Modifier.background(itemBrush ?: brush, RoundedCornerShape(10))
                }, ifFalse = {
                    Modifier.background(itemColor!!, RoundedCornerShape(10))
                })
//            .shadow(1.dp, RoundedCornerShape(10))
//            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (item.details != "" && !center) {
                Spacer(Modifier.height(20.dp))
            }
            VariableText(
                text = item.name,
                modifier = Modifier.padding(horizontal = 5.dp),
                minTextSize = 10.sp,
                maxTextSize = 15.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                maxLines = 1
            )
            if (item.details != "") {
                VariableText(
                    text = item.details,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    minTextSize = 8.sp,
                    maxTextSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall,
                    color = detailTextColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun VariableText(
    text: String,
    modifier: Modifier = Modifier,
    minTextSize: TextUnit = 8.sp,
    maxTextSize: TextUnit = 24.sp,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontFamily: FontFamily? = null
) {
    var textSize by remember { mutableStateOf(maxTextSize) }
    var readyToDraw by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        if (readyToDraw) {
            Text(
                text = text,
                color = color,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                style = style.copy(
                    fontSize = textSize,
                    fontWeight = fontWeight,
                    fontStyle = fontStyle,
                    fontFamily = fontFamily
                ),
                textAlign = textAlign,
            )
        }
        while (!readyToDraw) {
            val textStyle = style.copy(
                fontSize = textSize,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily
            )
            val measuredText = textMeasurer.measure(
                text = text,
                style = textStyle,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                constraints = Constraints(maxWidth = constraints.maxWidth)
            )
            if (measuredText.hasVisualOverflow) {
                if (textSize > minTextSize) {
                    textSize = (textSize.value - 1).sp
                } else {
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroceryItemPreview() {
    FoodTheme {
        Row() {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(10.dp)
            ) {
                GroceryItemDisplay(item = GroceryItem("Zucker","500g mit Käse und so weiter"), onClick = { }, onLongClick = {})
            }
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(10.dp)
            ) {
                GroceryItemDisplay(item = GroceryItem("Tomatenpfeffersauce","500g"), onClick = { }, onLongClick = {})
            }
        }

    }
}