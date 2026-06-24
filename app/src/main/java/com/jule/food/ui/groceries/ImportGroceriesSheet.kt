package com.jule.food.ui.groceries

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryViewModel
import com.jule.food.R
import com.jule.food.utils.CustomCheckbox
import com.jule.food.utils.SelectionOption
import java.util.UUID
import kotlin.collections.set


enum class ImportGroceryOption { AddToList, New }
val importGroceryOptionLabels = mapOf(
    ImportGroceryOption.AddToList to R.string.add_to_list,
    ImportGroceryOption.New to R.string.new_category
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportGroceriesSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    categories: List<GroceryItemCategory>,
    importGroceryItems: List<GroceryItem>,
    importingFileName: String?,
    getRecipeNameFromId: (UUID) -> String,
    onImport: (List<UUID>, UUID) -> Unit,
) {
    var chosenCategoryId by remember { mutableStateOf(categories[0].id) }
    val selectedGroceryItemIds = remember { importGroceryItems.map { it.id }.toMutableStateList() }

    LaunchedEffect(Unit) {
        Log.d("ImportGroceriesSheet", "First item ID: ${selectedGroceryItemIds[0]}")
        Log.d("ImportGroceriesSheet", "First item ID 2: ${importGroceryItems[0].id}")
        Log.d("ImportGroceriesSheet", "All Items IDs:: ${selectedGroceryItemIds.joinToString(separator = ",")}")
    }

    LaunchedEffect(selectedGroceryItemIds.size) {
        Log.d("ImportGroceriesSheet", "Selected Grocery Items Size: ${selectedGroceryItemIds.size}")
    }

    ModalBottomSheet(
        modifier = modifier.fillMaxSize(),
        onDismissRequest = onDismissRequest,
        dragHandle = { },
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            4.dp
                        )
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.import_groceries),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onDismissRequest) {
                            Icon(painterResource(R.drawable.clear), contentDescription = stringResource(R.string.clear))
                        }
                    }

                    if (importingFileName != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.zip_folder),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(importingFileName)
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                    }

//                    var importOptionsExpanded by remember { mutableStateOf(false) }
//                    Surface(
//                        onClick = { importOptionsExpanded = true },
//                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
//                        shape = RoundedCornerShape(20),
//                        modifier = Modifier.padding(start = 10.dp).height(30.dp)
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
//                            Text(stringResource(importGroceryOptionLabels[importOption]!!))
//                            Spacer(Modifier.width(5.dp))
//                            Icon(painterResource(R.drawable.arrow_right), contentDescription = null, modifier = Modifier.rotate(90f).size(14.dp))
//                        }
//                        DropdownMenu(
//                            expanded = importOptionsExpanded,
//                            onDismissRequest = { importOptionsExpanded = false }
//                        ) {
//                            ImportGroceryOption.entries.forEach { option ->
//                                DropdownMenuItem(
//                                    text = { Text(stringResource(importGroceryOptionLabels[option]!!)) },
//                                    onClick = {
//                                        importOption = option
//                                        importOptionsExpanded = false
//                                    }
//                                )
//                            }
//                        }
//                    }

                    Text(
                        stringResource(R.string.list),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    CategorySelectionButtons(
                        groceryCategories = categories,
                        selectedCategoryId = chosenCategoryId,
                        onChangeSelectedCategoryId = { chosenCategoryId = it },
                        showBadge = false,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.height(10.dp).fillMaxWidth().background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(50, 50, 0, 0)
                        )
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(13.3.dp),
                    verticalArrangement = Arrangement.spacedBy(13.3.dp),
                    contentPadding = PaddingValues(start = 13.3.dp, end = 13.3.dp, bottom = 100.dp),
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            stringResource(R.string.groceries_to_import),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                    items(importGroceryItems, key = { it.id }) { item ->
                        val isSelected = selectedGroceryItemIds.contains(item.id)
                        LaunchedEffect(Unit) {
                            Log.d("ImportGroceriesSheet", "Item ID: ${item.id}")
                            Log.d("ImportGroceriesSheet", "Selected: ${isSelected}, as ${item.id} is ${if (isSelected) "" else "not "}in List")
                        }
                        GroceryItemDisplay(
                            item,
                            onClick = {
                                if (isSelected)
                                    selectedGroceryItemIds.remove(item.id)
                                else
                                    selectedGroceryItemIds.add(item.id)
                            },
                            onLongClick = null,
                            showSelection = true,
                            isSelected = isSelected,
                            getRecipeNameFromId = getRecipeNameFromId
                        )
                    }
                }

//            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
//                importCategories.forEach { importCategory ->
//                    val isSelected = chosenImportCategoriesIds.contains(importCategory.id)
//                    val selectedImportOption = importOptions[importCategory.id]!!
//                    val chosenCategoryId = addToCategoryIds[importCategory.id]!!
//                    var importOptionsExpanded by remember { mutableStateOf(false) }
//
//                    Surface(
//                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(10)
//                    ) {
//                        Column() {
//                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
//                                    Text(importCategory.name, style = MaterialTheme.typography.titleMedium)
//                                    Spacer(Modifier.width(5.dp))
//                                    CustomBadge(
//                                        number = importCategory.items.count()
//                                    )
//                                    Spacer(Modifier.weight(1f))
//                                    CustomCheckbox(
//                                        selectionOption = if (isSelected) SelectionOption.Yes else SelectionOption.No,
//                                        selectedBackgroundColor = MaterialTheme.colorScheme.secondary,
//                                        iconTint = MaterialTheme.colorScheme.onSecondary,
//                                        modifier = Modifier.clickable {
//                                            if (isSelected)
//                                                chosenImportCategoriesIds.remove(importCategory.id)
//                                            else
//                                                chosenImportCategoriesIds.add(importCategory.id)
//                                        }
//                                    )
//                                }
//
//                            AnimatedVisibility(isSelected) {
//                                Column {
//                                    Spacer(Modifier.height(10.dp))
//                                    LazyRow (
//                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
//                                        modifier = Modifier.fillMaxWidth().height(50.dp),
//                                        contentPadding = PaddingValues(horizontal = 5.dp)
//                                    ) {
//                                        items(importCategory.items.sortedBy { it.name }) { groceryItem ->
//                                            GroceryItemDisplay(
//                                                item = groceryItem,
//                                                onClick = { },
//                                                onLongClick = null,
//                                                clickingEnabled = false,
//                                                center = true
//                                            )
//                                        }
//                                    }
//
//                                    Spacer(Modifier.height(10.dp))
//
//                                    Surface(
//                                        onClick = { importOptionsExpanded = true },
//                                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
//                                        shape = RoundedCornerShape(20),
//                                        modifier = Modifier.padding(start = 5.dp).height(40.dp)
//                                    ) {
//                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
//                                            Text(stringResource(importGroceryOptionLabels[selectedImportOption]!!))
//                                            Spacer(Modifier.width(5.dp))
//                                            Icon(painterResource(R.drawable.arrow_right), contentDescription = null, modifier = Modifier.rotate(90f).size(14.dp))
//                                        }
//                                        DropdownMenu(
//                                            expanded = importOptionsExpanded,
//                                            onDismissRequest = { importOptionsExpanded = false }
//                                        ) {
//                                            ImportGroceryOption.entries.forEach { option ->
//                                                DropdownMenuItem(
//                                                    text = { Text(stringResource(importGroceryOptionLabels[option]!!)) },
//                                                    onClick = {
//                                                        importOptions[importCategory.id] = option
//                                                        importOptionsExpanded = false
//                                                    }
//                                                )
//                                            }
//                                        }
//                                    }
//                                    AnimatedVisibility(selectedImportOption == ImportGroceryOption.AddToList) {
//                                        CategorySelectionButtons(
//                                            groceryCategories = groceryCategories,
//                                            selectedCategoryId = chosenCategoryId,
//                                            onChangeSelectedCategoryId = { addToCategoryIds[importCategory.id] = it },
//                                            showBadge = false,
//                                            modifier = Modifier.padding(horizontal = 5.dp)
//                                        )
//                                    }
//                                    Spacer(Modifier.height(5.dp))
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
            }
            Button(
                onClick = {
                    onImport(selectedGroceryItemIds, chosenCategoryId)
//                val importOptionsForChosenCategories = chosenImportCategoriesIds.map { importOptions[it]!! }
//                val categoryIdsForChosenCategories = chosenImportCategoriesIds.map { addToCategoryIds[it]!! }
//                onImport(chosenImportCategoriesIds, importOptionsForChosenCategories, categoryIdsForChosenCategories)
                },
                modifier = Modifier.align(Alignment.BottomCenter).width(200.dp).offset(y = (-20).dp),
//            enabled = chosenImportCategoriesIds.isNotEmpty()
            ) {
                Text(stringResource(R.string.import_))
            }
        }
    }
}

@Preview()
@Composable
fun ImportGroceriesSheetPreview() {
    val viewModel: GroceryViewModel = viewModel()
    val geschenke = viewModel.addCategory("Geschenke")
    val snacks = viewModel.addCategory("Snacks")

    viewModel.addToGroceries(GroceryItem("Geschenkpapier", "2 Rollen"), geschenke)
    viewModel.addToGroceries(GroceryItem("Ring", ""), geschenke)
    viewModel.addToGroceries(GroceryItem("Kuchen", ""), geschenke)

    viewModel.addToGroceries(GroceryItem("Chips", "300g"), snacks)

    val geschenke2 = GroceryItemCategory("Geschenke", items = remember {
        mutableStateListOf(
            GroceryItem("Baumkuchen", "")
        )
    }
    )

    val newCat = GroceryItemCategory(
        "Pizza",
        items = remember {
            mutableStateListOf(
                GroceryItem("Mehl", ""),
                GroceryItem("Hefe", ""),
                GroceryItem("Tomaten", "")
            )
        }
    )

    val oldCategories = viewModel.groceryItemCategories

    val items = listOf(GroceryItem("Mehl", ""),
    GroceryItem("Hefe", ""),
    GroceryItem("Tomaten", ""))


    ImportGroceriesSheet(
        onDismissRequest = {},
        categories = oldCategories,
        importGroceryItems = items,
        onImport = { _, _ -> },
        importingFileName = "File.json",
        getRecipeNameFromId = { it.toString() }
    )
}
//
//val newCategoryColorNew by animateColorAsState(if (selected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant)
//val oldCategoryColorOld by animateColorAsState(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
//
//Surface(
//color = if (hasSameName) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else Color.Transparent,
//shape = RoundedCornerShape(10),
//modifier = Modifier.fillMaxWidth()
//) {
//    Column() {
//        if (hasSameName)
//            Text(stringResource(R.string.this_list_already_exists), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 5.dp))
//
//        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.5f)) {
//                if (hasSameName) {
//                    Surface(
//                        color = newCategoryColorNew,
//                        shape = RoundedCornerShape(50)
//                    ) {
//                        Text(
//                            stringResource(R.string.from_file),
//                            modifier = Modifier.padding(horizontal = 10.dp)
//                        )
//                    }
//                }
//                CategorySelectionButton(
//                    category = importCategory,
//                    selected = selected,
//                    onClick = {
//                        if (selected) chosenImportCategoriesIds.remove(
//                            importCategory.id
//                        ) else chosenImportCategoriesIds.add(importCategory.id)
//                    },
//                    showBadge = true,
//                    showCheckbox = true,
//                    modifier = Modifier.fillMaxWidth(),
//                    details = importCategory.items.joinToString(separator = ", ") { it.name }
//                )
//            }
////                                Spacer(Modifier.weight(1f))
//            if (hasSameName) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.5f)) {
//                    Surface(
//                        color = oldCategoryColorOld,
//                        shape = RoundedCornerShape(50)
//                    ) {
//                        Text(
//                            stringResource(R.string.current),
//                            modifier = Modifier.padding(horizontal = 10.dp)
//                        )
//                    }
//                    CategorySelectionButton(
//                        category = categoryWithSameName,
//                        selected = selected,
//                        onClick = { },
//                        showBadge = true,
//                        showCheckbox = false,
//                        backgroundColorSelected = MaterialTheme.colorScheme.secondary,
//                        modifier = Modifier.fillMaxWidth(),
//                        details = categoryWithSameName.items.joinToString(separator = ", ") { it.name }
//                    )
//                }
//            }
//        }
//        if (hasSameName) {
//            val textColor by animateColorAsState(if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
//
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(3.dp),
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                ImportGroceryCategoryOption.entries.forEachIndexed { index, option ->
//                    val cornerLeft = if (index == 0) 30 else 10
//                    val cornerRight = if (index == ImportGroceryCategoryOption.entries.count() - 1) 30 else 10
//                    Surface(
//                        shape = RoundedCornerShape(
//                            cornerLeft,
//                            cornerRight,
//                            cornerRight,
//                            cornerLeft
//                        ),
//                        onClick = {
//                            importOptions[importCategory.id] = option
//                        },
//                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(
//                            16.dp
//                        ),
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(10.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                        ) {
//                            RadioButton(
//                                selected = importOptions[importCategory.id] == option,
//                                onClick = null,
//                                enabled = selected
//                            )
//                            Text(
//                                stringResource(
//                                    importGroceryCategoryOptionLabels[option]!!
//                                ),
//                                color = textColor
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//}