package com.jule.food

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

// Icon button for selecting images from users phone
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectImagesIconButton(
    maxImages: Int,
    onSelectImages : (List<String>) -> Unit, // selected / taken uri
) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val imageSaver = ImageSaver(context)
                val path = imageSaver.saveImageFromUri(uri)
                onSelectImages(listOf(path!!))
            }
        }
    )
    val multipleImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxImages),
        onResult = { uris ->
            // Save all uris to the phone as image files
            val imageSaver = ImageSaver(context)
            val paths = uris.mapNotNull { uri ->
                imageSaver.saveImageFromUri(uri)
            }
            onSelectImages(paths)
        }
    )

    // On clicking the button, launch the correct activity (depending on if max images is 1)
    FilledExpressiveIconButtonWithTooltip(
        shapes = IconButtonDefaults.shapes(),
        onClick = {
            if (maxImages == 1) {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                multipleImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        },
        tooltipText = stringResource(R.string.add_images)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_images))
    }
}