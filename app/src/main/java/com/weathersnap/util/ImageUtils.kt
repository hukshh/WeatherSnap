package com.weathersnap.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun compressImage(inputPath: String, outputPath: String, quality: Int = 60): File {
        val bitmap = BitmapFactory.decodeFile(inputPath)
        val outputFile = File(outputPath)
        FileOutputStream(outputFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        return outputFile
    }

    fun getFileSizeInKb(filePath: String): Long {
        return File(filePath).length() / 1024
    }
}
