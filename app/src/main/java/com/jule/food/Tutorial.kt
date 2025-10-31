package com.jule.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jule.food.ui.theme.FoodTheme

@Composable
fun TutorialOverlay(
    targetBounds: Rect,
    description: String,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val overlayColor = Color.Black.copy(0.4f)

                drawRect(
                    color = overlayColor, size = size
                )

                drawRoundRect(
                    color = Color.White,
                    topLeft = targetBounds.topLeft,
                    size = targetBounds.size,
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Clear
                )
//                drawRoundRect(
//                    color = Color.White,
//                    topLeft = targetBounds.topLeft,
//                    size = targetBounds.size,
//                    cornerRadius = CornerRadius(8.dp.toPx()),
//                    style = Stroke(width = 2.dp.toPx())
//                )
            }
    ) {
        TutorialDescriptionCard(
            description = description,
            onNext = onNext,
            onPrevious = onPrevious,
            targetBounds = targetBounds
        )
    }

}

@Composable
fun TutorialDescriptionCard(
    description: String,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    targetBounds: Rect,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerSize.height
    val screenWidth = windowInfo.containerSize.width

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 300.dp)
                // Position the card based on target location
                .offset {
                    with(density) {
                        val cardY = if (targetBounds.bottom < screenHeight / 2) {
                            // Target is in upper half, show card below
                            (targetBounds.bottom + 16.dp.toPx()).toInt()
                        } else {
                            // Target is in lower half, show card above
                            (targetBounds.top - 200.dp.toPx()).toInt()
                        }

                        IntOffset(
                            x = ((screenWidth - 300.dp.toPx()) / 2).toInt(),
                            y = cardY.coerceIn(0, (screenHeight - 200.dp.toPx()).toInt())
                        )
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onPrevious) {
                        Text("Previous")
                    }

                    Button(onClick = onNext) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPreview() {
    FoodTheme {

        Scaffold() { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                Text("This is a text")
                Button(onClick = { /*TODO*/ }) {
                    Text("This is a button")
                }
                Spacer(Modifier.height(200.dp))
                Text("Soooome more text")
            }
        }

        TutorialOverlay(
            targetBounds = Rect(Offset(10f, 0f), size = Size(100f, 100f)),
            description = "This is a tutorial",
            onNext = {},
            onPrevious = {}
        )
    }
}