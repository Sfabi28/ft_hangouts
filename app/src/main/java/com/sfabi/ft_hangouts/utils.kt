package com.sfabi.ft_hangouts

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val fileName = "img_${System.currentTimeMillis()}.jpg"

        val file = File(context.filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}