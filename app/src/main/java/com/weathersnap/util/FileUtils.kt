package com.weathersnap.util

import android.content.Context
import java.io.File
import java.util.UUID

object FileUtils {
    fun createTempFile(context: Context, extension: String = ".jpg"): File {
        val fileName = "weather_snap_${UUID.randomUUID()}$extension"
        return File(context.cacheDir, fileName)
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
}
