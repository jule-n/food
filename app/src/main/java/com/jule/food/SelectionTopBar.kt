package com.jule.food

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    numberSelected: Int,
    onClearSelection: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(stringResource(R.string.n_selected_images, numberSelected)) },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(painterResource(R.drawable.clear), contentDescription = "Clear Selection")
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}