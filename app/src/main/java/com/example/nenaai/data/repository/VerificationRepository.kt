package com.example.nenaai.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.nenaai.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class VerificationRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {

    suspend fun uploadDocument(label: String, bitmap: Bitmap) {
        val file = bitmapToFile(bitmap, "${label.replace(" ", "_")}.jpg")
        
        // Create RequestBody for the label
        val labelRequestBody = label.toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Create MultipartBody.Part for the file
        val fileRequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val documentPart = MultipartBody.Part.createFormData("document", file.name, fileRequestBody)

        // Make the API call
        apiService.uploadDocument(labelRequestBody, documentPart)
        
        // Clean up the temporary file
        file.delete()
    }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
    }
}
