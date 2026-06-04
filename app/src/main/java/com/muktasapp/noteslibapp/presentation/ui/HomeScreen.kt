package com.muktasapp.noteslibapp.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.muktasapp.noteslibapp.NavItem
import com.muktasapp.noteslibapp.R
import com.muktasapp.noteslibapp.presentation.ui.pages.AddNotesPage
import com.muktasapp.noteslibapp.presentation.ui.pages.HomePage
import com.muktasapp.noteslibapp.presentation.ui.pages.SearchNotesPage
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel
import com.muktasapp.noteslibapp.presentation.viewmodel.NotesViewModel

@Composable
fun HomeScreen(navController: NavController,authViewModel: AuthViewModel,notesViewModel: NotesViewModel){
    val navItems = listOf(
        NavItem(
            Label = "Home",
            Icon = Icons.Default.Home
        ),
        NavItem(
            Label = "Search Notes",
            Icon = Icons.Default.Search
        ),
        NavItem(
            Label = "Add Notes",
            iconRes = R.drawable.baseline_library_add_24
        )
    )
    var selectedIndex by remember{ mutableStateOf(0)}

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar (
                containerColor = colorResource(R.color.Secondary_color),
                contentColor = colorResource(R.color.Primary_color)
            ){
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = {
                            Text(text = item.Label)
                        },
                        icon = {
                            when{
                                item.Icon != null -> Icon(imageVector = item.Icon, contentDescription = item.Label)
                                item.iconRes != null -> Image(painter = painterResource(id = item.iconRes), contentDescription = item.Label)
                            }
                                //item.Icon?.let { Icon(imageVector = it, contentDescription = item.Label) }

                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colorResource(R.color.Primary_color),
                            unselectedIconColor = colorResource(R.color.Primary_color),
                            selectedTextColor = colorResource(R.color.Primary_color),
                            unselectedTextColor = colorResource(R.color.Primary_color),
                            indicatorColor = colorResource(R.color.white) // selection background
                        )
                        )

                }
            }
        }
        ) {innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding),selected = selectedIndex,authViewModel=authViewModel,notesViewModel,navController)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selected: Int,authViewModel: AuthViewModel,notesViewModel: NotesViewModel,navController: NavController){

    when(selected){
        0 -> HomePage(authViewModel,notesViewModel,navController)
        1 -> SearchNotesPage(notesViewModel,authViewModel,navController)
        2 -> AddNotesPage(notesViewModel,navController,authViewModel)
    }

}

/*
@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = NavController(context = LocalContext.current))
}*/