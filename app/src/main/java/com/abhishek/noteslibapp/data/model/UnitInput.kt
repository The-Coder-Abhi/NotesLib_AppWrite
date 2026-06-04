package com.abhishek.noteslibapp.data.model

data class UnitInput(
    var name: String = "",
    var chapterCount: Int = 0,
    var chapters: MutableList<ChapterInput> = mutableListOf()
)
