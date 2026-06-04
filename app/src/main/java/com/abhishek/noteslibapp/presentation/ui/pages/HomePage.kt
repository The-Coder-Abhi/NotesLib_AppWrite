package com.abhishek.noteslibapp.presentation.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhishek.noteslibapp.data.model.Note
import com.abhishek.noteslibapp.presentation.viewmodel.AuthViewModel
import com.abhishek.noteslibapp.presentation.viewmodel.NotesViewModel
import com.abhishek.noteslibapp.R

@Composable
fun HomePage(authViewModel: AuthViewModel, notesViewModel: NotesViewModel,navController: NavController) {
    val user by authViewModel.userData.collectAsState()
    val pendingRequests by notesViewModel.pendingRequests.collectAsState()
    val myNotes by notesViewModel.notes.collectAsState()
    val sharedNotes by notesViewModel.sharedNotes.collectAsState()
    // State to track which Note the user is trying to delete
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    // Observe the names map
    val requesterNames by notesViewModel.requesterNames.collectAsState()

    // 1. Tell the ViewModel to fetch the user data when the screen opens
    LaunchedEffect(Unit) {
        authViewModel.loadUserData()
    }

    // 2. React to the user data!
    // This waits until 'user' is no longer null, then fetches their notes.
    LaunchedEffect(user?.id) {
        user?.id?.let { uid ->
            notesViewModel.loadNotes(uid)
            notesViewModel.loadPendingRequests(uid)
        }
    }
    LaunchedEffect(user?.id) {
        user?.id?.let { uid ->
            notesViewModel.loadNotes(uid)
            notesViewModel.loadPendingRequests(uid)
            notesViewModel.loadSharedNotes(uid) // ADD THIS: Fetch the shared notes!
        }
    }

    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Delete Note Folder") },
            text = { Text("Are you sure you want to delete '${noteToDelete?.name}'? This will permanently delete ALL Subjects, Units, Chapters, and PDFs inside it. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Trigger the delete and close the dialog
                        user?.id?.let { uid ->
                            notesViewModel.deleteNoteFolder(noteToDelete!!.id, uid)
                        }
                        noteToDelete = null
                    }
                ) {
                    Text("Delete Everything", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(text = "Welcome 👋", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = user?.name ?: "Loading...", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Access Requests Section
        if (pendingRequests.isNotEmpty()) {
            Text(text = "Pending Access Requests", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                items(pendingRequests) { note ->
                    note.pendingRequests.forEach { requesterId ->
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {
                            val displayName = requesterNames[requesterId] ?: "Loading..."
                            Text(text = "User $displayName wants to view ${note.name}",modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                user?.id?.let { notesViewModel.approveAccess(note.id, requesterId, it) }
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.Primary_color)
                            )) {
                                Text("Accept")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // My Folders/Notes Section
        Text(text = "My Notes Folders", fontSize = 18.sp)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(myNotes) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                    navController.navigate("subjectscreen/${note.id}")
                }, colors = cardColors(containerColor = colorResource(R.color.Secondary_color))) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // The Folder Name
                        Text(text = note.name, fontSize = 18.sp, modifier = Modifier.weight(1f))

                        // THE MAGIC CHECK: Only show the trash can if they are the owner!
                        if (user?.id == note.owner) {
                            IconButton(onClick = {
                                // CHANGED: Open the dialog instead of instantly deleting!
                                noteToDelete = note
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Shared Folders/Notes Section
        Text(text = "Shared With Me", fontSize = 18.sp)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sharedNotes) { note ->
                // Uses a different color so users can tell it's a shared folder!
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                        navController.navigate("subjectscreen/${note.id}")
                    }
                ) {
                    Text(text = note.name, modifier = Modifier.padding(16.dp), fontSize = 18.sp)
                }
            }
        }
    }
}