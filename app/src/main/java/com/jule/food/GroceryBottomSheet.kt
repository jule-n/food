package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
    val scope = rememberCoroutineScope()
    
    var detailsActive by remember { mutableStateOf(false) }
//    var recipeActive by remember { mutableStateOf(false) }
    var showRecipeDialog by remember { mutableStateOf(false) }

    fun confirm(){
//        val recipeId = if (recipeActive) selectedRecipeId!! else null
        val newGroceryItem = GroceryItem(currentText, currentDetailText, selectedRecipeId)
        onConfirm(newGroceryItem)

        currentText = ""
        currentDetailText = ""
        detailsActive = false
        selectedRecipeId = null
//        recipeActive = false
    }

    val interactionSource = remember { MutableInteractionSource() }
    val detailColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val detailPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier.padding(horizontal = 10.dp).clickable {
                focusManager.clearFocus(true)
            },
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            OutlinedTextField(value = currentText, onValueChange = { currentText = it }, modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester), shape = RoundedCornerShape(20), placeholder = { Text(
                stringResource(id = R.string.name)
            ) }, keyboardOptions = KeyboardOptions.Default.copy(imeAction = if (imeActionDone) ImeAction.Done else ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    confirm()
                }, onDone = {
                    confirm()
                })
            )
            // MAYBE: Change this color on focus instead of on text change
            val detailBackgroundColor by animateColorAsState(targetValue = if(currentDetailText != "") MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp) else MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
            Surface(
                color = detailBackgroundColor,
                shape = RoundedCornerShape(20),
                modifier = Modifier.height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Icon(painterResource(R.drawable.text), contentDescription = null)
                    BasicTextField(
                        value = currentDetailText,
                        onValueChange = { currentDetailText = it },
//                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
//                            .background(Color.Red.copy(alpha = 0.2f))
                            .focusRequester(detailFocusRequester),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (!isError) {
                                confirm()
                                scope.launch {
                                    focusRequester.requestFocus()
                                }
                            }
                        }),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = detailColor),
                        cursorBrush = SolidColor(detailColor),
                        decorationBox = { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = currentDetailText,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = interactionSource,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = TextFieldDefaults.colors().copy(
                                    unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent,
//                                    unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                    unfocusedPlaceholderColor = detailPlaceholderColor, focusedPlaceholderColor = detailPlaceholderColor,
                                ),
                                shape = RoundedCornerShape(20),
                                placeholder = { Text(stringResource(id = R.string.details), maxLines = 1, style = LocalTextStyle.current) }
                            )
                        }
                    )
                    AnimatedVisibility(currentDetailText != "") {
                        IconButton(
                            onClick = {
                                currentDetailText = ""
                            }
                        ) {
                            Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                        }
                    }

                }
            }
            val recipeBackgroundColor by animateColorAsState(targetValue = if(selectedRecipeId != null) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            BoxWithConstraints() {
                val width = maxWidth
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        shape = RoundedCornerShape(20),
                        onClick = {
                            showRecipeDialog = true
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp).widthIn(max = width - 68.dp)
                        ) {
                            Icon(painterResource(R.drawable.book), contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            if (selectedRecipeId != null) {
                                Text(getRecipeNameFromId(selectedRecipeId!!), overflow = TextOverflow.Ellipsis)
                            } else {
                                Text("Select Recipe...", color = detailPlaceholderColor, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                        }
                    }

                    AnimatedVisibility(selectedRecipeId != null) {
                        IconButton(
                            onClick = {
                                currentDetailText = ""
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                        }
                    }

                }
            }

            Spacer(Modifier.height(10.dp))

            Row() {

            }

        }

        if (showRecipeDialog) {
            DefaultDialog(
                title = "Select recipe",
                onDismissRequest = { showRecipeDialog = false}
            ) {
                RecipeGrid(
                    recipes = activeRecipes,
                    onClickRecipe = { index ->
                        val recipeId = activeRecipes[index].id
                        selectedRecipeId = recipeId
                        showRecipeDialog = false
                    },
                    recipeGridState = rememberLazyGridState(),
                    contentPadding = PaddingValues()
                )
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