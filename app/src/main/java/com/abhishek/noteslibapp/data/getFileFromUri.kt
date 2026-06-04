package com.muktasapp.noteslibapp.data


import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

fun getFileFromUri(context: Context, uri: Uri): File?{
    var fileName = "temp_file.pdf"
    context.contentResolver.query(uri,null,null,null,null)?.use { cursor ->
        if (cursor.moveToFirst()){
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1){
                fileName = cursor.getString(index)
            }
        }
    }
    val tempFile = File(context.cacheDir,fileName)
    return try {
        context.contentResolver.openInputStream(uri)?.use{inputStream ->
            FileOutputStream(tempFile).use {outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    }catch (e: Exception){
        null
    }
}