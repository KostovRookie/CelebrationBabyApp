package com.stanga.nanit.ui.birthday

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.stanga.nanit.R
import com.stanga.nanit.domain.CalculateAgeTextUseCase
import com.stanga.nanit.domain.GetNumberDrawableUseCase
import com.stanga.nanit.domain.SaveBitmapToGalleryUseCase
import com.stanga.nanit.domain.SaveGalleryImageUseCase
import com.stanga.nanit.domain.ShareBitmapUseCase
import com.stanga.nanit.domain.manager.ImagePickerManager
import com.stanga.nanit.ui.animations.BouncingBabyLoader
import com.stanga.nanit.ui.details.BirthdayDetailsEvent
import com.stanga.nanit.ui.details.BirthdayDetailsState
import com.stanga.nanit.ui.theme.colorPrimary
import com.stanga.nanit.ui.theme.colorSecondary
import com.stanga.nanit.ui.theme.colorThird
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
fun NanitCelebrityScreen(
    state: BirthdayDetailsState,
    onEvent: (BirthdayDetailsEvent) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    val uiScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showDialog by remember { mutableStateOf(false) }
    var hideButtons by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val saveBitmapToGalleryUseCase = remember { SaveBitmapToGalleryUseCase() }
    val shareBitmapUseCase = remember { ShareBitmapUseCase(saveBitmapToGalleryUseCase) }
    val saveGalleryImageUseCase = remember { SaveGalleryImageUseCase() }
    val calculateAgeTextUseCase = remember { CalculateAgeTextUseCase() }
    val getNumberDrawableUseCase = remember { GetNumberDrawableUseCase() }

    val imagePickerManager = remember {
        ImagePickerManager(
            context = context,
            onImageSelected = { path ->
                onEvent(BirthdayDetailsEvent.PictureChanged(path))
                isLoading = false
            },
            saveGalleryImageUseCase = saveGalleryImageUseCase
        )
    }

    imagePickerManager.InitLaunchers()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .capturable(captureController)
            .background(Color.White)
    ) {
        val birthdayThemes = listOf(
            NanitRandomBackgroundImagesSet(
                backgroundRes = R.drawable.bg_elephant,
                placeholderBabyRes = R.drawable.ic_placeholder_baby_yellow,
                addPhotoRes = R.drawable.add_image_yellow
            ),
            NanitRandomBackgroundImagesSet(
                backgroundRes = R.drawable.bg_fox,
                placeholderBabyRes = R.drawable.ic_placeholder_baby_green,
                addPhotoRes = R.drawable.add_image_green
            ),
            NanitRandomBackgroundImagesSet(
                backgroundRes = R.drawable.bg_pelican,
                placeholderBabyRes = R.drawable.ic_placeholder_baby_blue,
                addPhotoRes = R.drawable.add_image_blue
            )
        )

        val selectedTheme = remember { birthdayThemes.random() }
        val ageText = remember(state.birthday) {
            calculateAgeTextUseCase.execute(state.birthday)
        }

        val paddingForOrientation = if (isLandscape) screenWidth / 5 else 0.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 20.dp,
                    end = paddingForOrientation, bottom =
                        if (isLandscape) 80.dp else 0.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Box {
                val profileImageSize = when {
                    booleanResource(R.bool.isTablet) && isLandscape -> 300.dp
                    booleanResource(R.bool.isTablet) -> 280.dp
                    isLandscape -> 140.dp
                    else -> 240.dp
                }
                if (state.pictureUri != null) {
                    AsyncImage(
                        model = state.pictureUri,
                        contentDescription = stringResource(R.string.baby_picture),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(profileImageSize)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                } else {
                    Image(
                        painter = painterResource(id = selectedTheme.placeholderBabyRes),
                        contentDescription = stringResource(R.string.baby_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(profileImageSize)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }

                if (!hideButtons) {
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-20).dp, y = (22).dp)
                            .size(36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = selectedTheme.addPhotoRes),
                            contentDescription = stringResource(R.string.change_picture),
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(selectedTheme.backgroundRes),
            contentDescription = stringResource(R.string.background),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingForOrientation
                )
                .padding(bottom = 40.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = stringResource(
                    id = R.string.today_is,
                    state.name.uppercase(Locale.getDefault())
                ),
                color = colorPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                softWrap = true
            )

            val ageNumber = ageText.first.toIntOrNull()?.coerceIn(1, 12) ?: 1

            Row {

                Image(
                    painter = painterResource(id = R.drawable.ic_wave_left),
                    contentDescription = stringResource(R.string.baby_picture),
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                Image(
                    painter = painterResource(id = getNumberDrawableUseCase.execute(ageNumber)),
                    contentDescription = stringResource(R.string.baby_picture),
                    modifier = Modifier.padding(vertical = 14.dp)
                )


                Image(
                    painter = painterResource(id = R.drawable.ic_wave_right),
                    contentDescription = stringResource(R.string.baby_picture),
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            }

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = ageText.second,
                color = colorPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isLandscape.not()) screenHeight / 4 + 10.dp else screenHeight / 4 + 20.dp),
            painter = painterResource(id = R.drawable.bg_app_title),
            contentDescription = stringResource(R.string.app_name)
        )


        if (!hideButtons) {
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 56.dp),
                onClick = {
                    uiScope.launch {
                        hideButtons = true
                        isLoading = true
                        try {
                            val bitmap = captureController.captureAsync().await()
                            if (true) {
                                shareBitmapUseCase.execute(context, bitmap.asAndroidBitmap())
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.failed_capture_screen,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                R.string.error_capturing_screen,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } finally {
                            hideButtons = false
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorSecondary,
                    contentColor = Color.White
                ),

                ) {
                Text(text = stringResource(R.string.share_the_news))
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share)
                )
            }
        }

        if (!hideButtons) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.close),
                    tint = colorThird
                )
            }
        }

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                BouncingBabyLoader()
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.choose_image_source)) },
            text = { Text(stringResource(R.string.pick_gallery_or_photo)) },
            confirmButton = {
                TextButton(onClick = {
                    isLoading = true
                    imagePickerManager.launchGalleryPicker()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.gallery))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isLoading = true
                    imagePickerManager.launchCameraPicker()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.camera))
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BirthdayScreenPreview() {
    val fakeState = BirthdayDetailsState(
        name = "Very Long Name Georgi Kostov",
        birthday = System.currentTimeMillis() - (5L * 30 * 24 * 60 * 60 * 1000), // 5 months ago
        pictureUri = null
    )

    NanitCelebrityScreen(
        state = fakeState,
        onEvent = {},
        navController = rememberNavController()
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=891dp,height=411dp")
@Composable
fun BirthdayScreenTabletPreview() {
    val fakeState = BirthdayDetailsState(
        name = "Baby Georgi",
        birthday = System.currentTimeMillis() - (5L * 30 * 24 * 60 * 60 * 1000), // 5 months ago
        pictureUri = null
    )

    NanitCelebrityScreen(
        state = fakeState,
        onEvent = {},
        navController = rememberNavController()
    )
}