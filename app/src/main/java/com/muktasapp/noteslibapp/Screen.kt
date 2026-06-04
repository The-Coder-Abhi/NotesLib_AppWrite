package com.muktasapp.noteslibapp

sealed class Screen(val routes: String) {
    object LoginScreen: Screen("loginscreen")
    object SignUpScreen: Screen("signupscreen")
    object HomeScreen: Screen("homescreen")
}