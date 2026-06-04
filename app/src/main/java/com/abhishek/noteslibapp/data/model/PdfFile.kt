package com.abhishek.noteslibapp.data.model

data class PdfFile(
    val id: String = "",
    val name: String = "",
    val fileUrl: String = "",
    val fileId: String = "", // Appwrite Storage File ID (needed for deletion)
    val chapterId: String = "",
    val uploadedAt: Long = 0L
)
