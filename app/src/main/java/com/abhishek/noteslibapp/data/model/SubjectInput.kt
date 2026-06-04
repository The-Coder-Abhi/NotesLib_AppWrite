package com.abhishek.noteslibapp.data.model

data class SubjectInput(
    var name: String = "",
    var unitCount: Int = 0,
    var units: MutableList<UnitInput> = mutableListOf()
)
