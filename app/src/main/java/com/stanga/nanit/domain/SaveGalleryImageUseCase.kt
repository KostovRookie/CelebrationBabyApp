package com.stanga.nanit.domain


import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SaveGalleryImageUseCase {

    fun execute(context: Context, uri: Uri): String? {
        return try {
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdir()
            }

            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(imagesDir, "${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}