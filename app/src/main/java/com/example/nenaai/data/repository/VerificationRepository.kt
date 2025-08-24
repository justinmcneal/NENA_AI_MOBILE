package com.example.nenaai.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.nenaai.data.model.AuthResponse
import com.example.nenaai.data.model.UserVerificationRequest
import com.example.nenaai.data.model.UserDocumentResponse
import com.example.nenaai.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class VerificationRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {

    suspend fun uploadDocument(documentType: String, bitmap: Bitmap) {
        val file = bitmapToFile(bitmap, "${documentType.replace(" ", "_")}.jpg")

        // Create RequestBody for the document type
        val documentTypeRequestBody = documentType.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create MultipartBody.Part for the file
        val fileRequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

        // Make the API call
        apiService.uploadDocument(documentTypeRequestBody, filePart)

        // Clean up the temporary file
        file.delete()
    }

    suspend fun getUserDocuments(token: String): Result<List<UserDocumentResponse>> {
        return try {
            val response = apiService.getUserDocuments(token)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(
                    Exception("API call failed: ${response.code()} - ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    suspend fun submitVerificationDetails(
        token: String,
        request: UserVerificationRequest
    ): Response<AuthResponse> {
        return apiService.submitVerificationDetails(token, request)
    }
}
