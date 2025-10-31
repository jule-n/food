package com.jule.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun SelectCategoryDialog(
    categories: List<GroceryItemCategory>,
    selectedCategory: UUID,
    onSelectCategory: (UUID) -> Unit,
    onDismissRequest: () -> Unit
) {
    DefaultDialog(
        title = stringResource(R.string.category),
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categories) { category ->
                val selected = category.id == selectedCategory
                val color = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                Surface(
                    onClick = { if (!selected) onSelectCategory(category.id) },
                    enabled = true,
                    color = color,
                    shape = RoundedCornerShape(20)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        ).fillMaxWidth().height(40.dp)
                    ) {
                        Text(
                            text = category.name,
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
