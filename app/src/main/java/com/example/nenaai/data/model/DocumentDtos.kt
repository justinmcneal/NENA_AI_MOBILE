package com.example.nenaai.data.model

data class UserDocumentResponse(
    val id: Int,
    val document_type: String,
    val file: String, // URL to the file
    val uploaded_at: String,
    val analysis_status: String
)
