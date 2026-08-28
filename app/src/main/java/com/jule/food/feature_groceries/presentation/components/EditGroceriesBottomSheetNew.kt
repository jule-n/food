package com.jule.food.feature_groceries.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryLocation
import com.jule.food.data.GroceryViewModel
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.ui.groceries.GroceryBottomSheetInputs
import com.jule.food.ui.groceries.GroceryBottomSheetSelectionField
import com.jule.food.ui.groceries.SelectCategoryDialog
import com.jule.food.ui.groceries.SelectRecipeDialog
import com.jule.food.ui.groceries_recipes.SelectEditLocationButtons
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditGroceriesBottomSheetNew(
    modifier: Modifier = Modifier,
    editingGroceryItemIds: List<Int>,
    groceryNameState: TextFieldState,
    groceryDetailState: TextFieldState,
    onOpenLocationSelectionDialog: () -> Unit,
    onOpenRecipeSelectionDialog: () -> Unit,
    onOpenListSelectionDialog: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }

    val singleItem = editingGroceryItemIds.size == 1

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)


    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = singleItem,
            transitionSpec = {
                fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically() }
        ) { single ->
            if (single) {
                Column {
                    GroceryBottomSheetInputs(
                        groceryNameState = groceryNameState,
                        groceryDetailState = groceryDetailState,
                        focusRequester = focusRequester,
                        onConfirm = {
                            focusManager.clearFocus(true)
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            } else {
                Surface {
                    Text(
                        text = stringResource(R.string.n_items_selected, editingGroceryItemIds.size),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EditSheetMoveButton(
                onClick = onOpenRecipeSelectionDialog,
                text = stringResource(R.string.move_to_recipe),
                icon = R.drawable.book
            )
            EditSheetMoveButton(
                onClick = onOpenLocationSelectionDialog,
                text = stringResource(R.string.move_to_location),
                icon = R.drawable.location
            )
            EditSheetMoveButton(
                onClick = onOpenListSelectionDialog,
                text = stringResource(R.string.move_to_list),
                icon = R.drawable.group_groceries
            )
        }
    }
}