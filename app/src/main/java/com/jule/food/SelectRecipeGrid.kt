package com.jule.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeGrid(
    modifier: Modifier = Modifier,
    recipes: List<Recipe>,
    searchFocusRequester: FocusRequester,
    onClickRecipe: (UUID) -> Unit,
    onCancel: () -> Unit,
    subtitle: String? = null
) {
    val searchState = rememberTextFieldState()

    Column(
        modifier = modifier.padding(bottom = 500.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(text = stringResource(R.string.select_recipe), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onCancel) {
                Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
            }
        }
        if (subtitle != null) {
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.Start).padding(start = 10.dp, bottom = 10.dp))
        }
        TextField(
            state = searchState,
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = stringResource(R.string.search_recipes))
            },
            shape = SearchBarDefaults.inputFieldShape,
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = Modifier.focusRequester(searchFocusRequester)
        )
        BoxWithConstraints (modifier = Modifier.padding(10.dp)){
            val itemSize = (maxWidth - 30.dp - 10.dp) / 4
            FlowRow(
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
//                modifier = Modifier.width(maxWidth)
            ) {
                recipes.filter { recipe -> recipe.name.contains(searchState.text, ignoreCase = true) }.forEach { recipe ->
                    RecipeTinyDisplay(
                        recipe = recipe,
                        modifier = Modifier.width(itemSize),
                        onClick = { onClickRecipe(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeTinyDisplay(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: () -> Unit,
    selected: Boolean? = null
//    isInactive: Boolean,
) {
//    val color = if (!isInactive) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
//    val textColor = if (!isInactive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
    val color = if (selected == null) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else
        if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)

    Surface(
        onClick = onClick,
//        enabled = !isInactive,
        color = color,
        modifier = modifier.padding(0.dp).aspectRatio(1.5f),
        shape = RoundedCornerShape(20)
    ) {
        Text(
            text = recipe.name,
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically).padding(5.dp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 16.sp)
        )
        if (selected != null) {
            Box(modifier = Modifier.size(20.dp)) {
                Surface(
                    color = Color.Black.copy(alpha = if (selected) 0.3f else 0f),
//                    shape = RoundedCornerShape(60),
                    shape = RoundedCornerShape(topStartPercent = 60, topEndPercent = 20, bottomStartPercent = 20, bottomEndPercent = 20),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)),
                    modifier = Modifier.size(20.dp)
                ) {
                    if (selected)
                        Icon(painter = painterResource(R.drawable.done), tint = Color.White, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeTinyDisplayPreview() {
    FoodTheme {
        RecipeTinyDisplay(
            recipe = Recipe("Recipe 1"),
            onClick = {},
            modifier = Modifier.height(60.dp),
            selected = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectRecipeGridPreview() {
    val recipes = mutableListOf<Recipe>()
    for(i in 1..10) {
        recipes.add(Recipe("Recipe ${i.toDouble().pow(i.toDouble())}"))
    }
    FoodTheme {
        SelectRecipeGrid(
            recipes = recipes,
            onClickRecipe = {},
            onCancel = { },
            searchFocusRequester = FocusRequester(),
            subtitle = "Subtitle"
        )
    }

}