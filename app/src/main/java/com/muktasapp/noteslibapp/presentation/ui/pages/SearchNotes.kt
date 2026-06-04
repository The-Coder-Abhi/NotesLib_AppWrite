package com.muktasapp.noteslibapp.presentation.ui.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import com.muktasapp.noteslibapp.R
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel
import com.muktasapp.noteslibapp.presentation.viewmodel.NotesViewModel

@Composable
fun SearchNotesPage(notesViewModel: NotesViewModel,authViewModel: AuthViewModel,navController: NavController) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by notesViewModel.searchResults.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val user by authViewModel.userData.collectAsState()
    val currentUserId = user?.id ?: ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.length > 2) notesViewModel.searchNotesBySubject(it)
            },
            label = { Text("Search by Subject Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true, // Forces it to be one line
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search // Changes the 'Enter' key to a 'Search' magnifying glass
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Tell your ViewModel to run the search function here!
                    // notesViewModel.searchSubjects(searchQuery)

                    keyboardController?.hide() // Hides the keyboard so you can see the results
                }
            ), colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            )

        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = note.name, fontWeight = FontWeight.Bold)
                            Text(text = "Owner ID: ${note.owner}", fontSize = 12.sp)
                        }

                        // 1. Check if they are NOT the owner AND NOT in the allowed list
                        if (currentUserId != note.owner && !note.allowedUsers.contains(currentUserId)) {

                            // 2. Check if they already sent a request so they don't spam it!
                            if (note.pendingRequests.contains(currentUserId)) {
                                Button(onClick = { }, enabled = false,colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.Primary_color)
                                )) { // Disabled button
                                    Text("Requested")
                                }
                            } else {
                                // 3. Show the normal active button
                                Button(onClick = {
                                    // Drop the currentUserId, and add the onComplete curly braces!
                                    notesViewModel.requestNoteAccess(note.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.Primary_color)
                                )) {
                                    Text("Request Access")
                                }
                            }

                        } else if (currentUserId == note.owner || note.allowedUsers.contains(currentUserId)) {
                            // Optional: You can show an "Open" button here if they already have access!
                            Button(onClick = {
                                navController.navigate("subjectscreen/${note.id}")
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.Primary_color)
                            )) {
                                Text("Open")
                            }
                        }
                    }
                }
            }
        }
    }
}