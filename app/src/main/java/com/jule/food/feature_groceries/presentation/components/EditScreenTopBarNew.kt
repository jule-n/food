package com.jule.food.feature_groceries.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jule.food.R
import com.jule.food.utils.IconButtonWithTooltip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenTopBarNew(
    title: String,
    backgroundColor: Color,
    doneEnabled: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = backgroundColor,
        modifier = modifier.padding(10.dp),
        shape = SearchBarDefaults.inputFieldShape
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchBarDefaults.InputFieldHeight)
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            IconButtonWithTooltip(
                onClick = onDone,
                enabled = doneEnabled,
                tooltipText = stringResource(R.string.done)
            ) {
                Icon(painterResource(R.drawable.done), contentDescription = stringResource(R.string.done))
            }
        }
    }
}