package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Scale
import com.jule.food.ui.theme.FoodTheme
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import java.io.File
import java.util.UUID

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
        topBar = {
            AnimatedContent(
                targetState = showAppBar,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { show ->
                if (show) {
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
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.4f), titleContentColor = Color.White)
                    )

                } else {
                    TopAppBar(title = {}, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
            }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black).padding(innerPadding).clickable(
                onClick = { showAppBar = !showAppBar },
                interactionSource = interactionSource,
                indication = null
            ),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { page ->
                val imageKey = if (page == 0) recipeId else "${recipeId}_${page}"
                with (LocalSharedTransitionScope.current!!) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(images[page]))
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = null,
//                    contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = imageKey),
                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                            )
                            .zoomable(
                                state = rememberZoomableState(
                                    zoomSpec = ZoomSpec(
                                        maxZoomFactor = 5f,
                                        preventOverOrUnderZoom = false
                                    )
                                ),
                                clipToBounds = false
                            )
                            .clickable(
                                onClick = { showAppBar = !showAppBar },
                                interactionSource = interactionSource,
                                indication = null
                            )
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