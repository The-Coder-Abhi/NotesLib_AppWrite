package com.abhishek.noteslibapp.data.model

data class Note(
    val id: String = "",
    val name: String = "",
    val owner: String = "",
    val createdAt: Long = 0L,
    val allowedUsers: List<String> = emptyList(),
    val pendingRequests: List<String> = emptyList()
)