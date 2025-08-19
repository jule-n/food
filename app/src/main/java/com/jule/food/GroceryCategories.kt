package com.jule.food

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.UUID


// Connected buttons for the grocery category selection
@Composable
fun CategoriesConnectedButtons(
    allCategories: List<GroceryItemCategory>,
    selectedCategory: GroceryItemCategory,
    onChangeSelectedCategoryId: (UUID) -> Unit,
    onEditCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val index = allCategories.indexOf(selectedCategory)
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ConnectedButtonGroup(
            options = allCategories.map { it.name },
            selectedOptionIndex = index,
            onSelectedOptionChange = { newIndex -> onChangeSelectedCategoryId(allCategories[newIndex].id)},
            checkedContainerColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        )
        Spacer(Modifier.width(10.dp))
        IconButton(onClick = onEditCategories) {
            Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "Edit Categories")
        }
    }
}

// Dialog for editing the grocery categories
@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun CategoriesDialog(
    allCategories: List<GroceryItemCategory>,
    onAddCategory: (GroceryItemCategory) -> Unit,
    onDeleteCategory: (UUID) -> Unit,
    onDismissRequest: () -> Unit,
    onChangeCategoryName: (String, id: UUID) -> Unit,
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current

    var isEditingIndex: Int? by remember { mutableStateOf(null) }

    DefaultDialog(
        title = stringResource(id = R.string.categories),
        onDismissRequest = {
            if (isEditingIndex == null)
                onDismissRequest()
            else
                focusManager.clearFocus(true)
        },
//        showOverlay = showAddCategoryDialog,
        onClickDialogEnabled = isEditingIndex != null,
        onClickDialog = {
            focusManager.clearFocus(true)
        }
    ) {
        focusManager = LocalFocusManager.current
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            allCategories.forEachIndexed { index, category ->
                var textFieldValue by remember { mutableStateOf(TextFieldValue(category.name)) }
                var lastValueWithoutError by remember { mutableStateOf(category.name) }

                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(isEditingIndex) {
                    if (isEditingIndex == index) {
                        focusRequester.requestFocus()
                        textFieldValue = textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
//                        submitted = false
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    shape = RoundedCornerShape(20)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth()) {
                        EditableText(
                            editable = isEditingIndex == index,
                            textAlign = TextAlign.Left,
                            textState = textFieldValue,
                            onTextChange = { newValue ->
                                if (!isCategoryError(newValue.text)) {
                                    lastValueWithoutError = newValue.text
                                }
                                textFieldValue = newValue
                            },
                            onSubmit = {
                                isEditingIndex = null
                                textFieldValue = textFieldValue.copy(text = lastValueWithoutError)
                                onChangeCategoryName(lastValueWithoutError.trim(), category.id)
                            },
                            focusRequester = focusRequester,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        val image = AnimatedImageVector.animatedVectorResource(R.drawable.edit_to_done)
                        IconButton(onClick = {
                            if (isEditingIndex == null)
                                isEditingIndex = index
                            else {
                                isEditingIndex = null
                                textFieldValue = textFieldValue.copy(text = lastValueWithoutError)
                                onChangeCategoryName(lastValueWithoutError.trim(), category.id)
                            }
                        }) {
                            Icon(rememberAnimatedVectorPainter(image, isEditingIndex == index), contentDescription = "Edit/Done")
                        }
                        val deleteEnabled = allCategories.size > 1 && isEditingIndex != index
                        IconButton(onClick = { onDeleteCategory(category.id) }, enabled = deleteEnabled) {
                            Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Delete", tint = if (deleteEnabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                        }
                    }
                }
            }
            val enabled = allCategories.size < 10
//            val enabled = true
            if (enabled) {
                FloatingActionButton(
                    onClick = { showAddCategoryDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
                }
            } else {
                Surface(
                    color = ButtonDefaults.buttonColors().disabledContainerColor,
                    shape = FloatingActionButtonDefaults.shape,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(55.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(imageVector = Icons.Default.Add, modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center), contentDescription = null, tint = ButtonDefaults.buttonColors().disabledContentColor)
                    }
                }
            }
        }
    }

    val newCategoryFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showAddCategoryDialog) {
        if (showAddCategoryDialog) {
            newCategoryFocusRequester.requestFocus()
        }
    }
    if (showAddCategoryDialog) {
        EnterTextDialog(
            title = stringResource(id = R.string.new_category),
            onDismissRequest = { showAddCategoryDialog = false },
            onConfirm = { name ->
                onAddCategory(GroceryItemCategory(name.trim()))
                showAddCategoryDialog = false
            },
            confirmWithKeyboard = true,
            isError = { isCategoryError(it) },
            focusRequester = newCategoryFocusRequester
        )
    }
}