package com.muktasapp.noteslibapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.muktasapp.noteslibapp.data.AppwriteHelper
import com.muktasapp.noteslibapp.presentation.PDFScreen
import com.muktasapp.noteslibapp.presentation.ui.ChapterScreen
import com.muktasapp.noteslibapp.presentation.ui.HomeScreen
import com.muktasapp.noteslibapp.presentation.ui.LoginScreen
import com.muktasapp.noteslibapp.presentation.ui.SignUpScreen
import com.muktasapp.noteslibapp.presentation.ui.SubjectScreen
import com.muktasapp.noteslibapp.presentation.ui.UnitScreen
import com.muktasapp.noteslibapp.presentation.ui.VerifyScreen
import com.muktasapp.noteslibapp.ui.theme.NotesLibAppTheme
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel
import com.muktasapp.noteslibapp.presentation.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppwriteHelper.init(this.applicationContext)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel= viewModel()
            val notesViewModel: NotesViewModel = viewModel()

            NotesLibAppTheme {
                MyNotesLib(authViewModel,notesViewModel)
            }
            /*
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                }
        }*/
        }
    }
}

@Composable
fun MyNotesLib(authViewModel: AuthViewModel,notesViewModel: NotesViewModel){

    val navController = rememberNavController()
    // State to hold our dynamic start destination
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Check Appwrite session as soon as the app starts
    LaunchedEffect(Unit) {
        authViewModel.checkInitialDestination { route ->
            startDestination = route
        }
    }

    // Wait until Appwrite tells us where to go
    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // A nice loading spinner for the user
        }
    } else {
        // Appwrite responded! Build the NavHost with the correct destination.
        NavHost(navController = navController, startDestination = startDestination!!) {

            composable(route = "loginscreen"){
                LoginScreen(navController, authViewModel)
            }

            composable(route = "signupscreen"){
                SignUpScreen(authViewModel, navController)
            }

            composable(route = "homescreen"){
                HomeScreen(navController, authViewModel, notesViewModel)
            }

            composable(route = "verifyscreen",deepLinks = listOf(
                navDeepLink { uriPattern = "noteslib://verify" }
            )){
                VerifyScreen(authViewModel, navController)
            }

            // 1. Subject Level
            composable(
                route = "subjectscreen/{noteId}",
                arguments = listOf(navArgument("noteId"){ type = NavType.StringType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                SubjectScreen(noteId, navController, notesViewModel,authViewModel)
            }

            // 2. Unit Level
            composable(
                route = "unitscreen/{noteId}/{subjectId}",
                arguments = listOf(
                    navArgument("noteId"){ type = NavType.StringType },
                    navArgument("subjectId"){ type = NavType.StringType }
                )
            ) { backstackEntry ->
                val noteId = backstackEntry.arguments?.getString("noteId") ?: ""
                val subjectId = backstackEntry.arguments?.getString("subjectId") ?: ""
                UnitScreen(noteId, subjectId, navController, notesViewModel,authViewModel)
            }

            // 3. Chapter Level
            composable(
                route = "chapterscreen/{noteId}/{subjectId}/{unitId}",
                arguments = listOf(
                    navArgument("noteId"){ type = NavType.StringType },
                    navArgument("subjectId"){ type = NavType.StringType },
                    navArgument("unitId"){ type = NavType.StringType }
                )
            ) { backstackEntry ->
                val noteId = backstackEntry.arguments?.getString("noteId") ?: ""
                val subjectId = backstackEntry.arguments?.getString("subjectId") ?: ""
                val unitId = backstackEntry.arguments?.getString("unitId") ?: ""
                ChapterScreen(noteId, subjectId, unitId, navController, notesViewModel,authViewModel)
            }

            // 4. PDF Level (Inside the Chapter)
            composable(
                route = "pdfscreen/{noteId}/{subjectId}/{unitId}/{chapterId}",
                arguments = listOf(
                    navArgument("noteId"){ type = NavType.StringType },
                    navArgument("subjectId"){ type = NavType.StringType },
                    navArgument("unitId"){ type = NavType.StringType },
                    navArgument("chapterId"){ type = NavType.StringType }
                )
            ) { backstackEntry ->
                val noteId = backstackEntry.arguments?.getString("noteId") ?: ""
                val subjectId = backstackEntry.arguments?.getString("subjectId") ?: ""
                val unitId = backstackEntry.arguments?.getString("unitId") ?: ""
                val chapterId = backstackEntry.arguments?.getString("chapterId") ?: ""
                PDFScreen(noteId, subjectId, unitId, chapterId, navController, notesViewModel)
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesLibAppTheme {
        LoginScreen({})
    }
}*/