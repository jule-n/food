package com.jule.food.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jule.food.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    numberSelected: Int,
    onClearSelection: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(stringResource(R.string.n_selected_images, numberSelected)) },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(painterResource(R.drawable.clear), contentDescription = stringResource(R.string.clear_selection))
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}