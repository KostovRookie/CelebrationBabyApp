package com.stanga.nanit.domain.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.stanga.nanit.domain.SaveGalleryImageUseCase
import java.io.File
import java.util.UUID

class ImagePickerManager(
    private val context: Context,
    private val onImageSelected: (String) -> Unit,
    private val saveGalleryImageUseCase: SaveGalleryImageUseCase
) {
    private lateinit var pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>
    private lateinit var takePictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>
    private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    private var tempCameraImageUri: Uri? = null

    @Composable
    fun InitLaunchers() {
        pickImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { selectedUri ->
                saveGalleryImageUseCase.execute(context, selectedUri)?.let { path ->
                    onImageSelected(path)
                }
            }
        }

        takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && tempCameraImageUri != null) {
                val path = saveGalleryImageUseCase.execute(context, tempCameraImageUri!!)
                path?.let { onImageSelected(it) }
            }
        }

        cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                launchTakePicture()
            } else {
                Toast.makeText(context, "Camera permission required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchGalleryPicker() {
        pickImageLauncher.launch("image/*")
    }

    fun launchCameraPicker() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchTakePicture()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchTakePicture() {
        val file = createImageFile(context)
        tempCameraImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        tempCameraImageUri?.let { safeUri ->
            takePictureLauncher.launch(safeUri)
        }
    }

    private fun createImageFile(context: Context): File {
        val imagesDir = File(context.cacheDir, "camera_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        return File(imagesDir, "${UUID.randomUUID()}.jpg")
    }
}