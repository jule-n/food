package com.jule.food

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.io.File
import java.util.UUID


@Composable
fun SpecificRecipeImages(
    recipe: Recipe,
    onDisplayImage: (imageIndex: Int) -> Unit,
    onAddImages: (List<String>) -> Unit,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    selectedImagesIndizes: List<Int>,
    onAddSelectedImageIndex: (Int) -> Unit,
    onRemoveSelectedImageIndex: (Int) -> Unit,
    anyImageSelected: Boolean,
    onChangeAnyImageSelected: (Boolean) -> Unit,
    fromRecipeSearch: Boolean,
    modifier: Modifier = Modifier
) {
    SpecificRecipeSection(
        modifier = modifier,
        icon = R.drawable.outline_image_24,
        title = stringResource(R.string.images),
        actionButtons = {
            SelectImagesIconButton(maxImages = 10, onSelectImages = onAddImages)
        }
    ) {
        RecipeImageGallery(recipeId = recipe.id, images = recipe.images, onDisplayImage = onDisplayImage, onChangeImageOrder = onChangeImageOrder,
            selectedImagesIndizes = selectedImagesIndizes, onAddSelectedImageIndex = onAddSelectedImageIndex, onRemoveSelectedImageIndex = onRemoveSelectedImageIndex,
            anyImageSelected = anyImageSelected, onChangeAnyImageSelected = onChangeAnyImageSelected, fromRecipeSearch = fromRecipeSearch)
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeImageGallery(
    recipeId: UUID,
    onDisplayImage: (imageIndex: Int) -> Unit,
    images: List<String>,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    selectedImagesIndizes: List<Int>,
    onAddSelectedImageIndex: (Int) -> Unit,
    onRemoveSelectedImageIndex: (Int) -> Unit,
    anyImageSelected: Boolean,
    onChangeAnyImageSelected: (Boolean) -> Unit,
    fromRecipeSearch: Boolean,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty())
        return

    val context = LocalContext.current

    val rowState = rememberLazyListState()
    val reorderableRowState = rememberReorderableLazyListState(lazyListState = rowState, onMove = { from, to ->
        onChangeImageOrder(from.index, to.index)
    })


    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 10.dp),
        state = rowState,
        modifier = modifier.height(200.dp)
    ) {
        itemsIndexed(images, key = { _, path -> path }) { index, path ->
            ReorderableItem(
                state = reorderableRowState,
                key = path
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val model = if (index == 0) {
                    ImageRequest.Builder(context)
                        .data(File(path))
                        .crossfade(true)
                        .placeholderMemoryCacheKey(path)
                        .memoryCacheKey(path)
                        .build()
                } else {
                    ImageRequest.Builder(context)
                        .data(File(path))
                        .crossfade(true)
                        .placeholderMemoryCacheKey(path)
                        .memoryCacheKey(path)
                        .build()
                }

                val selected = selectedImagesIndizes.contains(index)
                val onlySelected = selected && selectedImagesIndizes.size == 1

                val hapticFeedback = LocalHapticFeedback.current

                val mod = if (!anyImageSelected) {
                    Modifier.longPressDraggableHandle(interactionSource = interactionSource, onDragStarted = {
                        onAddSelectedImageIndex(index)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }, onDragStopped = {
                        onChangeAnyImageSelected(true)
                    })
                } else if (images.size != 1 && onlySelected) {
                    Modifier.draggableHandle(interactionSource = interactionSource)
                } else {
                    Modifier
                }

                key (anyImageSelected) {
                    Surface(
                        onClick = {
                            if (!anyImageSelected && selectedImagesIndizes.isEmpty()) {
                                onDisplayImage(index)
                            }
                            else if (anyImageSelected) {
                                if (selectedImagesIndizes.contains(index)) {
                                    onRemoveSelectedImageIndex(index)
                                    if (selectedImagesIndizes.isEmpty())
                                        onChangeAnyImageSelected(false)
                                } else {
                                    onAddSelectedImageIndex(index)
                                }
                            }
                        },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10),
                        modifier = mod
                    ) {
                        with(LocalSharedTransitionScope.current!!) {
                            AsyncImage(
                                model = model,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .conditional(index == 0) {
                                        Modifier.sharedElement(
                                            rememberSharedContentState(key = if (fromRecipeSearch) "${recipeId}_search" else "${recipeId}_grid"),
                                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                        )
                                    }
                                    .clip(RoundedCornerShape(10))
                                    .width(150.dp)
                                    .conditional(selected) {
                                        Modifier.border(
                                            width = 3.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(10)
                                        )
                                    }
                                //                                .clickable(onClick = { onSelectImage(index) }, interactionSource = interactionSource, indication = LocalIndication.current)
                            )
                        }
                    }
                }
            }
        }
    }
}