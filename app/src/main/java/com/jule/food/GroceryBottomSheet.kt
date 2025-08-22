package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddGroceryBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    focusRequester: FocusRequester,
    onConfirm: (GroceryItem) -> Unit,
    focusDetailsOnNext: Boolean = false,
    allowDismissIfEmpty: Boolean = true,
    imeActionDone: Boolean = false,
    activeRecipes: List<Recipe>,
    allRecipes: List<Recipe>,
    getRecipeNameFromId: ((UUID) -> String),
    title: String = stringResource(R.string.new_grocery),
    startValue: String = "",
    startDetails: String = ""
) {
    var currentText by remember { mutableStateOf(startValue) }
    val isError = currentText.isEmpty()

    var currentDetailText by remember { mutableStateOf(startDetails) }
    var selectedRecipeId: UUID? by remember { mutableStateOf(null) }
    val detailFocusRequester = remember { FocusRequester() }

    var recipeActive by remember { mutableStateOf(false) }
    var showRecipeDialog by remember { mutableStateOf(false) }

    fun confirm(){
        val recipeId = if (recipeActive) selectedRecipeId!! else null
        val newGroceryItem = GroceryItem(currentText, currentDetailText, recipeId)
        onConfirm(newGroceryItem)

        currentText = ""
        currentDetailText = ""
        recipeActive = false
    }

    val interactionSource = remember { MutableInteractionSource() }
    val detailColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val detailPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    ModalBottomSheet (
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus(true)
                }
        ) {
            BasicTextFieldWithBox(
                value = currentText,
                onValueChange = { currentText = it },
                placeholder = { Text("New Grocery...", maxLines = 1, color = detailPlaceholderColor, style = MaterialTheme.typography.titleMedium) },
                textStyle = MaterialTheme.typography.titleMedium,
                contentPadding = PaddingValues(15.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (currentText.isNotEmpty()) {
                        confirm()
                    }
                }),
//                colors = TextFieldDefaults.colors().copy(
//                    unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
//                    unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedContainerColor = MaterialTheme.colorScheme.surface
//                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
            Spacer(Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(painterResource(R.drawable.text), contentDescription = null, tint = if (currentDetailText.isEmpty()) detailPlaceholderColor else MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.width(5.dp))
                BasicTextFieldWithBox(
                    value = currentDetailText,
                    onValueChange = { currentDetailText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .focusRequester(detailFocusRequester),
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Text(stringResource(id = R.string.details), maxLines = 1, color = detailPlaceholderColor, style = MaterialTheme.typography.bodyMedium) }
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                GroceryBottomSheetSelectionField(
                    text = if (recipeActive) getRecipeNameFromId(selectedRecipeId!!) else "No Recipe",
                    icon = R.drawable.book,
                    isActive = recipeActive,
                    inactiveColor = detailPlaceholderColor,
                    onClick = { showRecipeDialog = true },
                    onClear = { recipeActive = false }
                )
                Spacer(Modifier.width(10.dp))
                GroceryBottomSheetSelectionField(
                    text = "No Location",
                    icon = R.drawable.location,
                    isActive = false,
                    inactiveColor = detailPlaceholderColor,
                    onClick = { },
                    onClear = { }
                )
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = { confirm() }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
//            }

        }

        if (showRecipeDialog) {
            SelectRecipeBottomSheet(
                onDismissRequest = { showRecipeDialog = false },
                onClickRecipe = { recipeId ->
                    selectedRecipeId = recipeId
                    showRecipeDialog = false
                },
                allRecipes = allRecipes,
                activeRecipes = activeRecipes
            )
        }
    }

}

@Composable
fun GroceryBottomSheetSelectionField(
    modifier: Modifier = Modifier,
    text: String,
    isActive: Boolean,
    inactiveColor: Color,
    onClick: () -> Unit,
    onClear: () -> Unit,
    @DrawableRes icon: Int,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        shape = RoundedCornerShape(50),
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(40.dp).animateContentSize().padding(start = 10.dp)
        ) {
            Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.width(5.dp))
            Text(text = text, maxLines = 1, style = MaterialTheme.typography.bodyMedium, color = inactiveColor)
            if (isActive) {
                IconButton(
                    modifier = Modifier.height(40.dp),
                    onClick = onClear
                ) {
                    Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                }
            } else {
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GroceryBottomSheetPreview() {
    MaterialTheme() {
        AddGroceryBottomSheet(
            onDismissRequest = {},
            focusRequester = remember { FocusRequester() },
            onConfirm = { },
            activeRecipes = listOf(
                Recipe("Recipe 1"),
                Recipe("Recipe 2"),
                Recipe("Recipe 3")
            ),
            allRecipes = listOf(
                Recipe("Recipe 1"),
                Recipe("Recipe 2"),
                Recipe("Recipe 3"),
                Recipe("Recipe 4"),
                Recipe("Recipe 5"),
                Recipe("Recipe 6")
            ),
            getRecipeNameFromId = { it.toString() }
        )
    }
}