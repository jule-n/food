package com.jule.food.ui.groceries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.R
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryViewModel
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.CustomCheckbox
import com.jule.food.utils.SelectionOption
import java.util.UUID

enum class GroceryShareOption {
    Text, Json
}
val groceryShareOptionLabel = mapOf(
    GroceryShareOption.Text to R.string.text,
    GroceryShareOption.Json to R.string.importable_json_file
)

val groceryShareOptionDescription = mapOf(
    GroceryShareOption.Text to R.string.text_share_description,
    GroceryShareOption.Json to R.string.importable_json_file_share_description
)

fun getShareTextFromGroceryItems(items: List<GroceryItem>): String {
    return items.joinToString(separator = "\n") { item ->
        val details = if (item.details.isNotEmpty()) " (${item.details})" else ""
        "- ${item.name}$details"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareGroceriesSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    selectedGroceriesNumber: Int,
    onShare: (shareOption: GroceryShareOption) -> Unit
) {
    var selectedShareOption by remember { mutableStateOf(GroceryShareOption.Json) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { },
        sheetGesturesEnabled = true
//        title = stringResource(R.string.share_groceries)
    ) {
        Column (modifier = Modifier.padding(horizontal = 10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(text = stringResource(R.string.share_groceries), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDismissRequest) {
                    Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                }
            }
            Text(stringResource(R.string.n_selected_groceries, selectedGroceriesNumber), style = MaterialTheme.typography.bodyLarge)
            Column() {
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
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel))}
                Spacer(Modifier.width(40.dp))
                Button(
                    onClick = {
                        onShare(selectedShareOption)
                    }
                ) {
                    Icon(painterResource(R.drawable.share), contentDescription = null)
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.share))
                }
            }
        }
    }
}

@Preview
@Composable
fun GroceryShareSheetPreview() {
    val viewModel: GroceryViewModel = viewModel()
    val geschenke = viewModel.addCategory("Geschenke")
    val pizza = viewModel.addCategory("Pizza")
    val snacks = viewModel.addCategory("Snacks")

    viewModel.addToGroceries(GroceryItem("Geschenkpapier", "2 Rollen"), geschenke)
    viewModel.addToGroceries(GroceryItem("Ring", ""), geschenke)
    viewModel.addToGroceries(GroceryItem("Kuchen", ""), geschenke)

    viewModel.addToGroceries(GroceryItem("Chips", "300g"), snacks)

    FoodTheme {
        ShareGroceriesSheet(onDismissRequest = {}, selectedGroceriesNumber = 3, onShare = { _, -> })
    }
}
