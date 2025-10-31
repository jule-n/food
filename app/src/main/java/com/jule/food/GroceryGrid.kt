package com.jule.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID


@Composable
fun GroceryGrid(
    groceryItems: List<GroceryItem>,
    onClickItem: (index: Int) -> Unit,
    onLongClickItem: (index: Int) -> Unit,
    getRecipeNameFromId: (UUID) -> String,
    modifier: Modifier = Modifier,
    center: Boolean = false,
    minSize: Dp = 100.dp,
    contentPadding: PaddingValues = PaddingValues(),
    showRecipeName: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(items = groceryItems, key = { _, item -> item.id }) {index, groceryItem ->
            GroceryItemDisplay(
                item = groceryItem,
                onClick = { onClickItem(index) },
                onLongClick = { onLongClickItem(index) },
                getRecipeNameFromId = getRecipeNameFromId,
                center = center,
                showRecipeName = showRecipeName,
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
fun GroceryItemDisplay(
    item: GroceryItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    center: Boolean = false,
//    itemColor: Color = MaterialTheme.colorScheme.primary,
////    itemBrush: Brush ? = null,
//    textColor: Color = MaterialTheme.colorScheme.onPrimary,
//    detailTextColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
    showRecipeName: Boolean = true,
    showSelection: Boolean = false,
    isSelected: Boolean = false,
    deleted: Boolean = false,
    getRecipeNameFromId: ((UUID) -> String)? = null
) {
    val itemColor = if (!showSelection) {
        if (!deleted) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    } else {
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    }
    val textColor = MaterialTheme.colorScheme.contentColorFor(itemColor)
    val detailTextColor = textColor.copy(alpha = 0.6f)

    val prim1 = lerp(itemColor, Color.White, 0.1f)
    val prim2 = lerp(itemColor, Color.White, 0.3f)
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
            .background(brush, RoundedCornerShape(10))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (item.details != "" && !center) {
                Spacer(Modifier.height(20.dp))
            }
            Text(
                text = item.name,
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.bodyMedium,
                autoSize = TextAutoSize.StepBased(6.sp, 15.sp),
                color = textColor,
                maxLines = 1
            )
            if (item.details != "") {
                Text(
                    text = item.details,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodySmall,
                    autoSize = TextAutoSize.StepBased(5.sp, 13.sp),
                    color = detailTextColor,
                    maxLines = 1
                )
            }
        }
        if (showRecipeName) {
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(0.8f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50)
            ) {
                if (item.recipeId != null && getRecipeNameFromId != null) {
                    Text(
                        text = getRecipeNameFromId(item.recipeId!!),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        color = detailTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (showSelection) {
            Surface(color = Color.Black.copy(alpha = if (isSelected) 0.3f else 0f), shape = RoundedCornerShape(20), border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)), modifier = Modifier.size(20.dp)) {
                if (isSelected)
                    Icon(painter = painterResource(R.drawable.done), tint = Color.White, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

val items = listOf(
    GroceryItem("Tomatenpfeffersauce","500g"),
    GroceryItem("Zucker","500g mit Käse und so weiter", UUID.randomUUID()),
    GroceryItem("Mehl","3kg"),
    GroceryItem("Pizza","")
)

@Preview(showBackground = true)
@Composable
fun GroceryItemPreview() {
    FoodTheme {
        Row {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(10.dp)
            ) {
                GroceryItemDisplay(item = items[0], onClick = { }, onLongClick = {}, getRecipeNameFromId = { it.toString() }, showSelection = true,isSelected = true)
            }
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(10.dp)
            ) {
                GroceryItemDisplay(item = items[1], onClick = { }, onLongClick = {}, getRecipeNameFromId = { it.toString() })
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GroceryGridPreview() {
    FoodTheme {
        GroceryGrid(groceryItems = listOf(items[0], items[1]), onClickItem = {}, onLongClickItem = {}, getRecipeNameFromId = { it.toString() }, showRecipeName = true)
    }
}