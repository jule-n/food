package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jule.food.ui.theme.FoodTheme
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

// Composable for viewing an image in full screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewer(
    bottomBar: @Composable () -> Unit,
    images: List<String>,
    startIndex: Int,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(startIndex, pageCount = { images.size })
    var showAppBar by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }

    FoodTheme(
        darkTheme = true
    ) {

        Scaffold(
            modifier = Modifier.background(color = Color.Black),
            bottomBar = bottomBar
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).clickable(
                    onClick = { showAppBar = !showAppBar },
                    interactionSource = interactionSource,
                    indication = null
                ),
            ) {
//            with (LocalSharedTransitionScope.current!!) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    state = pagerState,
                ) { page ->
//                    val imageKey = if (page == 0) recipeId.toString() else "${recipeId}_${page}"
                    var targetVal by remember { mutableFloatStateOf(0f) }
                    val offsetY by animateFloatAsState(targetVal)

                    val context = LocalContext.current
                    val displayMetrics = context.resources.displayMetrics
                    val height = displayMetrics.heightPixels

                    val zoomableImageState = rememberZoomableImageState()

                    ZoomableAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(images[page]))
                            .crossfade(true)
//                            .placeholderMemoryCacheKey(imageKey)
//                            .memoryCacheKey(imageKey)
                            .build(),
                        contentDescription = null,
                        state = zoomableImageState,
                        onClick = { showAppBar = !showAppBar },
                        modifier = Modifier
//                            .sharedElement(
//                                rememberSharedContentState(key = imageKey),
//                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
//                            )
                            .fillMaxSize()
                            .offset { IntOffset(0, offsetY.roundToInt()) }
                            .draggable(
                                enabled = (zoomableImageState.zoomableState.zoomFraction ?: 0f) < 0.1f,
                                state = rememberDraggableState { delta ->
                                    targetVal += delta
                                },
                                onDragStopped = {
                                    if (abs(offsetY) > height / 8) {
                                        onClose()
                                    } else {
                                        targetVal = 0f
                                    }
                                },
                                orientation = Orientation.Vertical
                            )
                    )
//                }
                }

                AnimatedVisibility(
                    visible = showAppBar,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TopAppBar(
                        title = { },
                        navigationIcon = { IconButtonWithTooltip(onClick = onClose, tooltipText = stringResource(R.string.back)) {
                            Icon(painter = painterResource(R.drawable.arrow_left), contentDescription = stringResource(R.string.back), tint = Color.White)
                        }},
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.8f))
                    )
                }
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