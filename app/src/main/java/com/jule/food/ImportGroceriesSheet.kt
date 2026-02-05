package com.jule.food

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

enum class ImportGroceryCategoryOption { Merge, Replace, AddNew }
val importGroceryCategoryOptionLabels = mapOf(
    ImportGroceryCategoryOption.Merge to R.string.merge,
    ImportGroceryCategoryOption.Replace to R.string.replace,
    ImportGroceryCategoryOption.AddNew to R.string.add_new
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImportGroceriesSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    importCategories: List<GroceryItemCategory>,
    importingFileName: String?,
    onImport: (List<UUID>, List<ImportGroceryCategoryOption>) -> Unit,
) {
    LaunchedEffect(importingFileName) {
        Log.d("ImportGroceriesSheet", "Importing File Name: $importingFileName")
    }
    val chosenImportCategoriesIds = remember { importCategories.map { it.id }.toMutableStateList() }
    val importOptions = remember { chosenImportCategoriesIds.map { it to ImportGroceryCategoryOption.Merge }.toMutableStateMap() }

//    val importOptions = remember { importCategories.map { ImportGroceryCategoryOption.Merge }.toMutableStateList() }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        dragHandle = { },
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column (modifier = Modifier.padding(horizontal = 10.dp).verticalScroll(rememberScrollState()).fillMaxSize()) {
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
                    Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                }
            }

            if (importingFileName != null) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.zip_folder), contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(importingFileName)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Text(stringResource(R.string.lists_to_import), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                importCategories.forEach { importCategory ->
                    val selected = chosenImportCategoriesIds.contains(importCategory.id)
                    val categoryWithSameName = groceryCategories.firstOrNull { it.name == importCategory.name }
                    val hasSameName = categoryWithSameName != null

                    val newCategoryColorNew by animateColorAsState(if (selected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    val oldCategoryColorOld by animateColorAsState(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)

                    Surface(
                        color = if (hasSameName) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else Color.Transparent,
                        shape = RoundedCornerShape(10),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column() {
                            if (hasSameName)
                                Text(stringResource(R.string.this_list_already_exists), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 5.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.5f)) {
                                    if (hasSameName) {
                                        Surface(
                                            color = newCategoryColorNew,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                stringResource(R.string.from_file),
                                                modifier = Modifier.padding(horizontal = 10.dp)
                                            )
                                        }
                                    }
                                    CategorySelectionButton(
                                        category = importCategory,
                                        selected = selected,
                                        onClick = {
                                            if (selected) chosenImportCategoriesIds.remove(
                                                importCategory.id
                                            ) else chosenImportCategoriesIds.add(importCategory.id)
                                        },
                                        showBadge = true,
                                        showCheckbox = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        details = importCategory.items.joinToString(separator = ", ") { it.name }
                                    )
                                }
//                                Spacer(Modifier.weight(1f))
                                if (hasSameName) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.5f)) {
                                        Surface(
                                            color = oldCategoryColorOld,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                stringResource(R.string.current),
                                                modifier = Modifier.padding(horizontal = 10.dp)
                                            )
                                        }
                                        CategorySelectionButton(
                                            category = categoryWithSameName,
                                            selected = selected,
                                            onClick = { },
                                            showBadge = true,
                                            showCheckbox = false,
                                            backgroundColorSelected = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.fillMaxWidth(),
                                            details = categoryWithSameName.items.joinToString(separator = ", ") { it.name }
                                        )
                                    }
                                }
                            }
                                if (hasSameName) {
                                    val textColor by animateColorAsState(if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))


                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        ImportGroceryCategoryOption.entries.forEachIndexed { index, option ->
                                            val cornerLeft = if (index == 0) 30 else 10
                                            val cornerRight = if (index == ImportGroceryCategoryOption.entries.count() - 1) 30 else 10
                                            Surface(
                                                shape = RoundedCornerShape(
                                                    cornerLeft,
                                                    cornerRight,
                                                    cornerRight,
                                                    cornerLeft
                                                ),
                                                onClick = {
                                                    importOptions[importCategory.id] = option
                                                },
                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    16.dp
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    RadioButton(
                                                        selected = importOptions[importCategory.id] == option,
                                                        onClick = null,
                                                        enabled = selected
                                                    )
                                                    Text(
                                                        stringResource(
                                                            importGroceryCategoryOptionLabels[option]!!
                                                        ),
                                                        color = textColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val importOptionsForChosenCategories = chosenImportCategoriesIds.map { importOptions[it]!! }
                onImport(chosenImportCategoriesIds, importOptionsForChosenCategories)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).width(200.dp),
            enabled = chosenImportCategoriesIds.isNotEmpty()
        ) {
            Icon(painterResource(R.drawable.import_data), contentDescription = null)
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.import_))
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

    val geschenke2 = GroceryItemCategory("Geschenke", items = remember { mutableStateListOf(
        GroceryItem("Baumkuchen", "")) }
    )

    val newCat = GroceryItemCategory("Pizza",
        items = remember { mutableStateListOf(
            GroceryItem("Mehl", ""),
            GroceryItem("Hefe", ""),
            GroceryItem("Tomaten", "")
        ) }
    )

    val oldCategories = viewModel.groceryItemCategories
    val newCategories = listOf(newCat, geschenke2)


    ImportGroceriesSheet(
        onDismissRequest = {},
        groceryCategories = oldCategories,
        importCategories = newCategories,
        onImport = { _, _ -> },
        importingFileName = "File.json"
    )
}