package com.muktasapp.noteslibapp.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel
import com.muktasapp.noteslibapp.presentation.viewmodel.NotesViewModel

@Composable
fun ChapterScreen(
    noteId: String,
    subjectId: String,
    unitId: String,
    navController: NavHostController,
    notesViewModel: NotesViewModel,
    authViewModel: AuthViewModel
) {
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

    val chapters by notesViewModel.chapters.collectAsState()
    LaunchedEffect(unitId) { notesViewModel.loadChapters(noteId, subjectId, unitId) }

    FolderScreenLayout(
        title = "Chapters",
        items = chapters.map { Pair(it.id, it.name) },
        onItemClick = { chapterId -> navController.navigate("pdfscreen/$noteId/$subjectId/$unitId/$chapterId")
        },
        isOwner = isOwner,
        onDeleteClick = { chapterId ->
            notesViewModel.deleteChapterFolder(chapterId, noteId, subjectId, unitId)
        }

    )
}