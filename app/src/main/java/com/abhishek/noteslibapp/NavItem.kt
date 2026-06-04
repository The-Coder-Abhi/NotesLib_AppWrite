package com.abhishek.noteslibapp

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val Label:String,
    val Icon: ImageVector?=null,
    val iconRes:Int?=null
)