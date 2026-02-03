package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

enum class ImportGroceryCategoryOption { None, Merge, Replace, AddNew }
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
    importCategories: List<GroceryItemCategory>
) {
    val chosenImportCategoriesIds = remember { importCategories.map { it.id }.toMutableStateList() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = { }
    ) {
        Column (modifier = Modifier.padding(horizontal = 10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
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

            Text("Categories to Import: ", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                importCategories.forEach { importCategory ->
                    val selected = chosenImportCategoriesIds.contains(importCategory.id)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10),
                        modifier = Modifier.fillMaxWidth().animateContentSize(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
                        onClick = { if (selected) chosenImportCategoriesIds.remove(importCategory.id) else chosenImportCategoriesIds.add(importCategory.id)}
                    ) {
                        Column() {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(10)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                                    Text(importCategory.name)
                                    Spacer(Modifier.width(5.dp))
                                    CustomBadge(number = importCategory.items.count(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                                        textColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    CustomCheckbox(
                                        selectionOption = if (selected) SelectionOption.Yes else SelectionOption.No,
                                        selectedBackgroundColor = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            AnimatedVisibility(selected) {
                                Column {
                                    val hasSameName: Boolean = groceryCategories.any { it.name == importCategory.name }
                                    if (hasSameName) {
                                        var resolveConflictOption =
                                            ImportGroceryCategoryOption.Merge

                                        Surface (
                                            color = Color(red = 0.7f, green = 0.4f, blue = 0.1f, alpha = 0.5f),
                                            shape = RoundedCornerShape(10),
                                            modifier = Modifier.fillMaxWidth().padding(5.dp)
                                        ){
                                            Column(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                                                Text(
                                                    "List already exists. How do you want to proceed?",
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(horizontal = 5.dp)
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                    modifier = Modifier.fillMaxWidth(),
                                                ) {
                                                    ImportGroceryCategoryOption.entries.drop(1)
                                                        .forEachIndexed { index, option ->
                                                            val cornerLeft =
                                                                if (index == 0) 30 else 10
                                                            val cornerRight =
                                                                if (index == ImportGroceryCategoryOption.entries.count() - 2) 30 else 10
                                                            Surface(
                                                                shape = RoundedCornerShape(
                                                                    cornerLeft,
                                                                    cornerRight,
                                                                    cornerRight,
                                                                    cornerLeft
                                                                ),
                                                                onClick = {
                                                                    resolveConflictOption = option
                                                                },
                                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                                    4.dp
                                                                ),
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier.padding(5.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                ) {
                                                                    RadioButton(
                                                                        selected = resolveConflictOption == option,
                                                                        onClick = null
                                                                    )
                                                                    Text(
                                                                        stringResource(
                                                                            importGroceryCategoryOptionLabels[option]!!
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                    }
                                    LazyRow(
                                        modifier = Modifier.height(50.dp),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp)
                                    ) {
                                        items(importCategory.items) { item ->
                                            GroceryItemDisplay(
                                                item,
                                                onClick = { },
                                                onLongClick = null,
                                                clickingEnabled = false
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
        importCategories = newCategories
    )
}