package com.abhishek.noteslibapp.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.noteslibapp.data.model.Chapter
import com.abhishek.noteslibapp.data.model.Note
import com.abhishek.noteslibapp.data.model.PdfFile
import com.abhishek.noteslibapp.data.model.Subject
import com.abhishek.noteslibapp.data.model.SubjectInput
import com.abhishek.noteslibapp.data.model.UnitItem
import com.abhishek.noteslibapp.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    private val repo = NotesRepository()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects

    private val _units = MutableStateFlow<List<UnitItem>>(emptyList())
    val units: StateFlow<List<UnitItem>> = _units

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters: StateFlow<List<Chapter>> = _chapters

    private val _pdfs = MutableStateFlow<List<PdfFile>>(emptyList())
    val pdfs: StateFlow<List<PdfFile>> = _pdfs

    private val _searchResults = MutableStateFlow<List<Note>>(emptyList())
    val searchResults: StateFlow<List<Note>> = _searchResults

    private val _pendingRequests = MutableStateFlow<List<Note>>(emptyList())
    val pendingRequests: StateFlow<List<Note>> = _pendingRequests

    private val _sharedNotes = MutableStateFlow<List<Note>>(emptyList())
    val sharedNotes: StateFlow<List<Note>> = _sharedNotes.asStateFlow()

    private val _requesterNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val requesterNames: StateFlow<Map<String, String>> = _requesterNames.asStateFlow()

    // --- Loading Hierarchy ---
    fun loadNotes(ownerId: String) {
        viewModelScope.launch {
            val result = repo.getNotes(ownerId)
            if (result.isSuccess) {
                _notes.value = result.getOrThrow()
            }
        }
    }

    fun loadSubjects(noteId: String){
        viewModelScope.launch {
            val result = repo.getSubjects(noteId)
            if (result.isSuccess){
                _subjects.value = result.getOrThrow()
            }
        }
    }

    fun loadUnits(noteId: String, subjectId: String){
        viewModelScope.launch {
            val result = repo.getUnits(noteId, subjectId)
            if (result.isSuccess){
                _units.value = result.getOrThrow()
            }
        }
    }

    fun loadChapters(noteId: String, subjectId: String, unitId: String) {
        viewModelScope.launch {
            val result = repo.getChapters(noteId, subjectId, unitId)
            if (result.isSuccess) {
                _chapters.value = result.getOrThrow()
            }
        }
    }

    fun loadPdfs(noteId: String, subjectId: String, unitId: String, chapterId: String) {
        viewModelScope.launch {
            val result = repo.getPdfs(noteId, subjectId, unitId, chapterId)
            if (result.isSuccess) {
                _pdfs.value = result.getOrThrow()
            }
        }
    }

    // --- PDF Operations ---
    fun uploadPdfFile(
        noteId: String, subjectId: String, unitId: String, chapterId: String,
        fileName: String, uri: Uri, context: Context, onResult: (Boolean)->Unit) {
        viewModelScope.launch {
            val result = repo.uploadPdfToChapter(noteId, subjectId, unitId, chapterId, fileName, uri, context)
            if (result.isSuccess){
                loadPdfs(noteId, subjectId, unitId, chapterId)// Refresh the list automatically
                onResult(true)
            }else{
                onResult(false)
            }
        }
    }

    fun deletePdfFile(noteId: String, subjectId: String, unitId: String, chapterId: String,
                      pdfId: String, fileId: String, onResult: (Boolean)->Unit){
        viewModelScope.launch {
            // Notice we pass fileId to delete it from Appwrite Storage
            val result = repo.deletePdf(noteId, subjectId, unitId, chapterId, pdfId, fileId)
            if (result.isSuccess){
                loadPdfs(noteId, subjectId, unitId, chapterId)// Refresh the list automatically
                onResult(true)
            }else{
                onResult(false)
            }
        }
    }

    // --- Search and Request Access ---
    fun searchNotesBySubject(query: String) {
        viewModelScope.launch {
            val result = repo.searchNotesBySubject(query)
            if (result.isSuccess){
                _searchResults.value = result.getOrThrow()
            }
        }
    }

    fun requestNoteAccess(noteId: String, onComplete: (Boolean)->Unit) {
        viewModelScope.launch {
            val result = repo.requestAccess(noteId)
            onComplete(result.isSuccess)
        }
    }

    fun loadPendingRequests(ownerId: String) {
        viewModelScope.launch {
            val result = repo.getPendingRequests(ownerId)
            if (result.isSuccess) {
                val notes = result.getOrThrow()
                _pendingRequests.value = notes
                // 2. Grab all the unique requester IDs
                val allRequesterIds = notes.flatMap { it.pendingRequests }.distinct()
                val currentNames = _requesterNames.value.toMutableMap()

                for (id in allRequesterIds) {
                    // 3. Look up the name in your database if we haven't already!
                    if (!currentNames.containsKey(id)) {
                        currentNames[id] = repo.getUserName(id)
                    }
                }
                _requesterNames.value = currentNames

            }
        }
    }

    fun approveAccess(noteId: String, requesterId: String, ownerId: String) {
        viewModelScope.launch {
            val result = repo.approveAccess(noteId, requesterId)
            if (result.isSuccess) {
                loadPendingRequests(ownerId) //refresh list to remove approved users
            }
        }
    }

    // In NotesViewModel.kt
    fun createFullNoteHierarchy(
        noteName: String,
        subjects: List<SubjectInput>,
        userId: String,
        onComplete: (Boolean, String?) -> Unit // <--- ADDED String? for the error message
    ) {
        viewModelScope.launch {
            try {
                // 1. Create the main Note folder
                val noteResult = repo.createNote(noteName) // Make sure you are passing userId if needed!

                if (noteResult.isSuccess) {
                    val noteId = noteResult.getOrThrow()

                    // 2. Loop through and create Subjects
                    for (subject in subjects) {
                        val subResult = repo.addSubject(noteId, subject.name)

                        if (subResult.isSuccess) {
                            val subId = subResult.getOrThrow()

                            // 3. Loop through and create Units
                            for (unit in subject.units) {
                                val unitResult = repo.addUnit(noteId, subId, unit.name)

                                if (unitResult.isSuccess) {
                                    val unitId = unitResult.getOrThrow()

                                    // 4. Loop through and create Chapters
                                    for (chapter in unit.chapters) {
                                        repo.addChapter(noteId, subId, unitId, chapter.name)
                                    }
                                }
                            }
                        }
                    }
                    // 5. Refresh the UI automatically by fetching the new list!
                    loadNotes(userId)
                    onComplete(true, null) // Success! No error.
                } else {
                    val realError = noteResult.exceptionOrNull()?.message ?: "Unknown error"
                    onComplete(false, realError)
                }
            } catch (e: Exception) {
                // CATCH THE APPWRITE CRASH AND SEND IT TO THE UI!
                onComplete(false, e.message)
            }
        }
    }
    fun loadSharedNotes(userId: String) {
        viewModelScope.launch {
            val result = repo.getSharedNotes(userId)
            if (result.isSuccess) {
                _sharedNotes.value = result.getOrThrow()
            }
        }
    }
    fun deleteSubjectFolder(subjectId: String, noteId: String) {
        viewModelScope.launch {
            if (repo.deleteSubject(noteId,subjectId).isSuccess) {
                loadSubjects(noteId) // Refresh the list
            }
        }
    }

    fun deleteUnitFolder(unitId: String, subjectId: String,noteId: String) {
        viewModelScope.launch {
            if (repo.deleteUnit(noteId,subjectId,unitId).isSuccess) {
                loadUnits(noteId,subjectId) // Refresh the list
            }
        }
    }

    fun deleteChapterFolder(chapterId: String, unitId: String,subjectId: String,noteId: String) {
        viewModelScope.launch {
            if (repo.deleteChapter(noteId,subjectId,unitId,chapterId).isSuccess) {
                loadChapters(noteId,subjectId,unitId) // Refresh the list
            }
        }
    }
    fun deleteNoteFolder(noteId: String, ownerId: String) {
        viewModelScope.launch {
            val result = repo.deleteNote(noteId)
            if (result.isSuccess) {
                // Refresh the list so the deleted note vanishes from the screen!
                loadNotes(ownerId)
            }
        }
    }
}


