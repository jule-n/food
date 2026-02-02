package com.jule.food

import android.widget.RadioGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID

enum class GroceryShareOption {
    Text, Zip
}
val groceryShareOptionLabel = mapOf(
    GroceryShareOption.Text to R.string.text,
    GroceryShareOption.Zip to R.string.importable_zip_file
)

val groceryShareOptionDescription = mapOf(
    GroceryShareOption.Text to R.string.text_share_description,
    GroceryShareOption.Zip to R.string.importable_zip_file_share_description
)

fun getShareTextFromCategories(categories: List<GroceryItemCategory>): String {
    return categories.joinToString(separator = "\n\n") { category ->
        val items = category.items.joinToString(separator = "\n") { item ->
            val details = if (item.details.isNotEmpty()) " (${item.details})" else ""
            "- ${item.name}$details"
        }
        "${category.name}:\n$items"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareGroceriesSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    currentCategoryId: UUID,
    onShare: (categoryIds: List<UUID>, shareOption: GroceryShareOption) -> Unit
) {
    val selectedCategories = remember { groceryCategories.map { it.id }.toMutableStateList() }
    val selectionOption = if (selectedCategories.count() == groceryCategories.count()) SelectedOption.Yes else (
        if (selectedCategories.isEmpty()) SelectedOption.No else SelectedOption.Half
    )

    var selectedShareOption by remember { mutableStateOf(GroceryShareOption.Zip) }

    DefaultDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.share_groceries)
    ) {
//        Text(stringResource(R.string.share_groceries), style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(10.dp))
        Column{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.category_or_categories), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                CustomCheckbox(
                    selectionOption = selectionOption,
                    topStartRadius = 20,
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    iconTint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.clickable {
                        if (selectedCategories.isEmpty())
                            selectedCategories.addAll( groceryCategories.map { it.id })
                        else {
                            selectedCategories.clear()
                        }
                    }
                )
            }
            Spacer(Modifier.height(5.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                groceryCategories.forEach { category ->
                    val selected = selectedCategories.contains(category.id)
                    Surface(
                        color = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { if (selected) selectedCategories.remove(category.id) else selectedCategories.add(category.id) },
                        shape = RoundedCornerShape(20)
                    ) {
                        CustomCheckbox(
                            selectionOption = if (selected) SelectedOption.Yes else SelectedOption.No
                        )
                        Text(category.name, modifier = Modifier.padding(10.dp))
                    }
//                    CategoryButton(
//                        name = category.name,
//                        selected = selected,
//                        selectedColor = MaterialTheme.colorScheme.tertiary,
//                        selectCheckbox = true,
//                        onClick = { if (selected) selectedCategories.remove(category.id) else selectedCategories.add(category.id) }
//                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.share_as),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(5.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.selectableGroup()) {
                GroceryShareOption.entries.forEachIndexed { index, entry ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        shape = if (index == 0) RoundedCornerShape(30, 30, 10, 10) else (
                                if (index == GroceryShareOption.entries.count() - 1) RoundedCornerShape(
                                    10,
                                    10,
                                    30,
                                    30
                                ) else RoundedCornerShape(10)
                                ),
                        onClick = { selectedShareOption = entry }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        ) {
                            RadioButton(
                                selected = selectedShareOption == entry,
                                onClick = null)
                            Column {

                                Text(stringResource(groceryShareOptionLabel[entry]!!))
                                Text(stringResource(groceryShareOptionDescription[entry]!!), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
//            }
//            val labels = groceryShareOptionLabel.values.map { stringResource(it) }
//            ConnectedButtonGroup(
//                options = labels,
//                selectedOptionIndex = selectedShareOption.toInt(),
//                onSelectedOptionChange = { selectedShareOption = GroceryShareOption.entries[it] },
//                checkedContainerColor = MaterialTheme.colorScheme.secondary
//            )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel))}
            Button(
                onClick = {
                    onShare(selectedCategories, selectedShareOption)
                },
                enabled = selectedCategories.isNotEmpty()
            ) {
                Icon(painterResource(R.drawable.share), contentDescription = null)
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.share))
            }
        }
    }
}

@Preview
@Composable
fun GroceryShareSheetPreview() {
    val viewModel: GroceryViewModel = viewModel()
    val geschenke = viewModel.addCategory("Geschenke")
    val snacks = viewModel.addCategory("Snacks")

    viewModel.addToGroceries(GroceryItem("Geschenkpapier", "2 Rollen"), geschenke)
    viewModel.addToGroceries(GroceryItem("Ring", ""), geschenke)
    viewModel.addToGroceries(GroceryItem("Kuchen", ""), geschenke)

    viewModel.addToGroceries(GroceryItem("Chips", "300g"), snacks)

    FoodTheme {
        ShareGroceriesSheet(onDismissRequest = {}, groceryCategories = viewModel.groceryItemCategories, currentCategoryId = geschenke, onShare = {_, _, -> })
    }
}
