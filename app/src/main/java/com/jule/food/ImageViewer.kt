package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.maxBitmapSize
import coil3.request.transformations
import coil3.size.Scale
import com.jule.food.ui.theme.FoodTheme
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableContentLocation
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageViewer(
    recipeId: UUID,
    images: List<String>,
    startIndex: Int,
    onDeleteRecipeImage: (String) -> Unit,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(startIndex, pageCount = { images.size })
    var showAppBar by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold(
        modifier = Modifier.background(color = Color.Black),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black).clickable(
                onClick = { showAppBar = !showAppBar },
                interactionSource = interactionSource,
                indication = null
            ),
        ) {
            with (LocalSharedTransitionScope.current!!) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    state = pagerState,
                ) { page ->
                    val imageKey = if (page == 0) recipeId else "${recipeId}_${page}"
                    var targetVal by remember { mutableStateOf(0f) }
                    val offsetY by animateFloatAsState(targetVal)

                    val context = LocalContext.current
                    val displayMetrics = context.resources.displayMetrics
                    val height = displayMetrics.heightPixels

                    val zoomableImageState = rememberZoomableImageState()

                    ZoomableAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(images[page]))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        state = zoomableImageState,
                        clipToBounds = true,
                        onClick = { showAppBar = !showAppBar },
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = imageKey),
                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                            )
                            .fillMaxSize()
                            .offset { IntOffset(0, offsetY.roundToInt()) }
                            .draggable(
                                enabled = zoomableImageState.zoomableState.zoomFraction == 0f,
                                state = rememberDraggableState { delta ->
                                    targetVal += delta
                                },
                                onDragStopped = {
                                    if (Math.abs(offsetY) > height / 6) {
                                        onClose()
                                    } else {
                                        targetVal = 0f
                                    }
                                },
                                orientation = Orientation.Vertical
                            )
                    )
//                with (LocalSharedTransitionScope.current!!) {
//                    ZoomableAsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(File(images[page]))
////                            .scale(Scale.FILL)
//                            .build(),
//                        contentDescription = null,
//                        clipToBounds = false,
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier,
////                            .sharedElement(
////                                rememberSharedContentState(key = imageKey),
////                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
////                            ),
//                        onClick = { showAppBar = !showAppBar },
//                        state = rememberZoomableImageState(
//                            rememberZoomableState(
//                                zoomSpec = ZoomSpec(maxZoomFactor = 5f)
//                            )
//                        )
//                    )
//                }
                }
            }

            AnimatedVisibility(
                visible = showAppBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = { },
                    navigationIcon = { IconButton(onClick = onClose) {
                        Icon(painter = painterResource(R.drawable.arrow_left), contentDescription = null)
                    }},
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(painter = painterResource(R.drawable.delete), contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.8f), titleContentColor = Color.White)
                )
            }
        }
    }

}
//
//@Preview
//@Composable
//fun ImageViewerPreview() {
//    var dialogOpen by remember { mutableStateOf(false) }
//    val images = listOf(
//        R.drawable.cauliflower_wings_smaller,
//        R.drawable.lasagne_small,
//        R.drawable.mac_and_cheese_small
//    )
//    FoodTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            Column() {
//                Text("Regular content")
//                Button(onClick = { dialogOpen = true }) {
//                    Text("Open Dialog")
//                }
//            }
//        }
//        if (dialogOpen) {
//            ImageViewer(
//                startPage = 0,
//                onClose = { dialogOpen = false },
//                images = images
//            )
//        }
//    }
//}