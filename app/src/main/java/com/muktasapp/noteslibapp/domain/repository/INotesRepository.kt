package com.muktasapp.noteslibapp.domain.repository

import android.content.Context
import android.net.Uri
import com.muktasapp.noteslibapp.data.model.Chapter
import com.muktasapp.noteslibapp.data.model.Note
import com.muktasapp.noteslibapp.data.model.PdfFile
import com.muktasapp.noteslibapp.data.model.Subject
import com.muktasapp.noteslibapp.data.model.UnitItem

interface INotesRepository {
    suspend fun createNote(name: String): Result<String>
    suspend fun addSubject(noteId: String, subjectName: String): Result<String>
    suspend fun addUnit(noteId: String, subjecId: String, unitName: String): Result<String>
    suspend fun addChapter(
        noteId: String,
        subjecId: String,
        unitId: String,
        chapterName: String
    ): Result<String>

    suspend fun getNotes(userId: String): Result<List<Note>>
    suspend fun getSubjects(noteId: String): Result<List<Subject>>
    suspend fun getUnits(noteId: String, subjectId: String): Result<List<UnitItem>>
    suspend fun getChapters(
        noteId: String,
        subjectId: String,
        unitId: String
    ): Result<List<Chapter>>

    suspend fun uploadPdfToChapter(
        noteId: String, subjectId: String, unitId: String, chapterId: String,
        fileName: String, fileUri: Uri, context: Context
    ): Result<String>

    suspend fun getPdfs(
        noteId: String,
        subjectId: String,
        unitId: String,
        chapterId: String
    ): Result<List<PdfFile>>

    suspend fun deletePdf(
        noteId: String, subjectId: String, unitId: String, chapterId: String,
        pdfId: String, fileId: String
    ): Result<Boolean>

    suspend fun searchNotesBySubject(query: String): Result<List<Note>> // Search logic
    suspend fun requestAccess(noteId: String): Result<Boolean>
    suspend fun getPendingRequests(ownerId: String): Result<List<Note>>
    suspend fun approveAccess(noteId: String, requesterId: String): Result<Boolean>
    suspend fun getSharedNotes(userId: String): Result<List<Note>>
    suspend fun deleteChapter(noteId: String,subjectId: String,unitId: String,chapterId: String): Result<Boolean>
    suspend fun deleteUnit(noteId: String,subjectId: String,unitId: String): Result<Boolean>
    suspend fun deleteSubject(noteId: String,subjectId: String): Result<Boolean>
    suspend fun deleteNote(noteId: String): Result<Boolean>
}