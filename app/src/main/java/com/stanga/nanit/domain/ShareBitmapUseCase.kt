package com.stanga.nanit.domain

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class ShareBitmapUseCase(
    private val saveBitmapToGalleryUseCase: SaveBitmapToGalleryUseCase
) {
    fun execute(context: Context, bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, "birthday_shared.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share via"))

            Toast.makeText(context, "Shared Successfully!", Toast.LENGTH_SHORT).show()

            saveBitmapToGalleryUseCase.execute(context, bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show()
        }
    }
}