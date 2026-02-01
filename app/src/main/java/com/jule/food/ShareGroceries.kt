package com.jule.food

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID

enum class GroceryShareOption {
    Text, Zip
}
val groceryShareOptionLabel = mapOf(
    GroceryShareOption.Text to R.string.text,
    GroceryShareOption.Zip to R.string.importable_zip_file
)

fun getShareTextFromCategories(categories: List<GroceryItemCategory>) {

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
    val selectedCategories = remember { mutableStateListOf(currentCategoryId) }

    var selectedShareOption by remember { mutableStateOf(GroceryShareOption.Zip) }

    DefaultDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.share_groceries)
    ) {
//        Text(stringResource(R.string.share_groceries), style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(10.dp))
        SettingsScreenCategory(
            name = stringResource(R.string.category_or_categories),
            textStartPadding = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                groceryCategories.forEach { category ->
                    val selected = selectedCategories.contains(category.id)
                    CategoryButton(
                        name = category.name,
                        selected = selected,
                        selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { if (selected) selectedCategories.remove(category.id) else selectedCategories.add(category.id) }
                    )
                }
            }
        }
        SettingsScreenCategory(
            name = stringResource(R.string.share_as),
            textStartPadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            val labels = groceryShareOptionLabel.values.map { stringResource(it) }
            ConnectedButtonGroup(
                options = labels,
                selectedOptionIndex = selectedShareOption.toInt(),
                onSelectedOptionChange = { selectedShareOption = GroceryShareOption.entries[it] },
                checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
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
    val categories = listOf(
        GroceryItemCategory("Category 1"),
        GroceryItemCategory("Geschenke"),
        GroceryItemCategory("Bauarbeiten"),
        GroceryItemCategory("Snacks"),
    )


    FoodTheme {
        ShareGroceriesSheet(onDismissRequest = {}, groceryCategories = categories, currentCategoryId = categories[0].id, onShare = {_, _, -> })
    }
}
