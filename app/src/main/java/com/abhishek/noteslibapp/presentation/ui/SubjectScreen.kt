package com.abhishek.noteslibapp.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.abhishek.noteslibapp.presentation.viewmodel.AuthViewModel
import com.abhishek.noteslibapp.presentation.viewmodel.NotesViewModel

@Composable
fun SubjectScreen(noteId: String, navController: NavHostController,notesViewModel: NotesViewModel,authViewModel: AuthViewModel) {
    val subjects by notesViewModel.subjects.collectAsState()
    // 2. Get the currently logged-in User's ID
    val user by authViewModel.userData.collectAsState()
    val currentUserId = user?.id ?: ""
    // 3. Look through the ViewModel's lists to find this specific Note
    val myNotes by notesViewModel.notes.collectAsState()
    val sharedNotes by notesViewModel.sharedNotes.collectAsState()

    // Find the note in either list, then grab its owner ID
    val currentNote = myNotes.find { it.id == noteId } ?: sharedNotes.find { it.id == noteId }
    val noteOwnerId = currentNote?.owner ?: ""

    // 4. Create the final boolean to pass into the Layout!
    val isOwner = (currentUserId == noteOwnerId) && currentUserId.isNotEmpty()
    LaunchedEffect(noteId) { notesViewModel.loadSubjects(noteId) }

    FolderScreenLayout(
        title = "Subjects",
        items = subjects.map { Pair(it.id, it.name) },
        onItemClick = { subjectId -> navController.navigate("unitscreen/$noteId/$subjectId") },
        isOwner = isOwner,
        onDeleteClick = { subjectId ->
            notesViewModel.deleteSubjectFolder(subjectId, noteId)
        }
    )
}