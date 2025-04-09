package com.stanga.nanit.ui.details

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.stanga.nanit.R
import com.stanga.nanit.domain.SaveGalleryImageUseCase
import com.stanga.nanit.domain.manager.ImagePickerManager
import com.stanga.nanit.ui.birthday.NanitRandomBackgroundImagesSet
import com.stanga.nanit.ui.theme.colorPrimary
import com.stanga.nanit.ui.theme.colorSecondary
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun AddBirthdayScreen(
    state: BirthdayDetailsState,
    onEvent: (BirthdayDetailsEvent) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var showDialog by remember { mutableStateOf(false) }

    val saveGalleryImageUseCase = remember { SaveGalleryImageUseCase() }

    val imagePickerManager = remember {
        ImagePickerManager(
            context = context,
            onImageSelected = { path ->
                onEvent(BirthdayDetailsEvent.PictureChanged(path))
            },
            saveGalleryImageUseCase = saveGalleryImageUseCase
        )
    }

    imagePickerManager.InitLaunchers()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = selectedTheme.backgroundRes),
            contentDescription = stringResource(R.string.background),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorPrimary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                state.pictureUri?.let { nonNullUri ->
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        var imageLoading by remember { mutableStateOf(true) }

                        if (imageLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = colorPrimary,
                                strokeWidth = 3.dp
                            )
                        }

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(nonNullUri)) // ✅ now it's NON-NULL
                                .crossfade(true)
                                .listener(
                                    onSuccess = { _, _ -> imageLoading = false },
                                    onError = { _, _ -> imageLoading = false }
                                )
                                .build(),
                            contentDescription = stringResource(R.string.baby_picture),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Image(
                        painter = painterResource(id = selectedTheme.addPhotoRes),
                        contentDescription = stringResource(R.string.change_picture),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(BirthdayDetailsEvent.NameChanged(it)) },
                label = { Text(stringResource(R.string.name_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val datePicker = DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            calendar.set(year, month, dayOfMonth)
                            onEvent(BirthdayDetailsEvent.BirthdayChanged(calendar.timeInMillis))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePicker.show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (state.birthday != null)
                        Calendar.getInstance().apply { timeInMillis = state.birthday }.let {
                            "${it.get(Calendar.DAY_OF_MONTH)}/${it.get(Calendar.MONTH) + 1}/${it.get(Calendar.YEAR)}"
                        }
                    else
                        stringResource(R.string.pick_birthday)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onEvent(BirthdayDetailsEvent.SaveBirthday)
                    navController.navigate("birthday_screen")
                },
                enabled = state.isButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.show_birthday_screen))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.choose_image_source)) },
            text = { Text(text = stringResource(R.string.pick_gallery_or_photo)) },
            confirmButton = {
                TextButton(onClick = {
                    imagePickerManager.launchGalleryPicker()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.gallery))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    imagePickerManager.launchCameraPicker()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.camera))
                }
            }
        )
    }
}