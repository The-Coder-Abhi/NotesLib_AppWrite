package com.muktasapp.noteslibapp.data.repository

import android.content.Context
import android.net.Uri
import com.muktasapp.noteslibapp.data.AppwriteHelper
import com.muktasapp.noteslibapp.data.getFileFromUri
import com.muktasapp.noteslibapp.data.model.Chapter
import com.muktasapp.noteslibapp.data.model.Note
import com.muktasapp.noteslibapp.data.model.PdfFile
import com.muktasapp.noteslibapp.data.model.Subject
import com.muktasapp.noteslibapp.data.model.UnitItem
import com.muktasapp.noteslibapp.domain.repository.INotesRepository
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.extensions.toJson
import io.appwrite.models.InputFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NotesRepository(): INotesRepository{
    private val db = AppwriteHelper.databases
    private val storage = AppwriteHelper.storage
    private val account = AppwriteHelper.account
    private val dbId = AppwriteHelper.DATABASE_ID

    // --- Helper to safely extract lists from Appwrite's response ---
    private fun extractList(data: Any?): List<String> {
        return (data as? List<*>)?.map { it.toString() } ?: emptyList()
    }




    // ==========================================
    // 1. FOLDER CREATION
    // ==========================================

    override suspend fun createNote(name: String): Result<String> {
        return try{
            val user = account.get()
            val data = mapOf(
                "name" to name, "owner" to user.id, "createdAt" to System.currentTimeMillis(),
                "allowedUsers" to emptyList<String>(), "pendingRequests" to emptyList<String>()
            )
            val doc = db.createDocument(dbId, AppwriteHelper.NOTES_COLLECTION,ID.unique(),data)
            Result.success(doc.id)

        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun addSubject(


        noteId: String,
        subjectName: String
    ): Result<String> {
        return try {
            val data = mapOf("name" to subjectName, "noteId" to noteId)
            val doc = db.createDocument(dbId, AppwriteHelper.SUBJECTS_COLLECTION, ID.unique(), data)
            Result.success(doc.id)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun addUnit(
        noteId: String,
        subjecId: String,
        unitName: String
    ): Result<String> {
        return try{
            val data = mapOf("name" to unitName, "noteId" to noteId, "subId" to subjecId)
            val doc =db.createDocument(dbId,AppwriteHelper.UNITS_COLLECTION,ID.unique(),data)
            Result.success(doc.id)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun addChapter(
        noteId: String,
        subjecId: String,
        unitId: String,
        chapterName: String
    ): Result<String> {
        return try {
            val data = mapOf("name" to chapterName, "noteId" to noteId, "subId" to subjecId, "unitId" to unitId)
            val doc = db.createDocument(dbId, AppwriteHelper.CHAPTERS_COLLECTION, ID.unique(), data)
            Result.success(doc.id)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    // ==========================================
    // 2. DATA FETCHING (The Hierarchy)
    // ==========================================

    override suspend fun getNotes(userId: String): Result<List<Note>> {
        return try {
            val response = db.listDocuments(dbId, AppwriteHelper.NOTES_COLLECTION,listOf(Query.equal("owner",userId)))
            val notes = response.documents.map { doc ->
                Note(
                    id = doc.id,
                    name = doc.data["name"].toString(),
                    owner = doc.data["owner"].toString(),
                    allowedUsers = extractList(doc.data["allowedUsers"]),
                    pendingRequests = extractList(doc.data["pendingRequests"])
                )
            }
            Result.success(notes)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getSubjects(noteId: String): Result<List<Subject>> {
        return try {
            val response = db.listDocuments(dbId, AppwriteHelper.SUBJECTS_COLLECTION,listOf(Query.equal("noteId",noteId)))
            val subjects = response.documents.map {
                Subject(it.id, it.data["name"].toString(), noteId)
            }
            Result.success(subjects)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun getUnits(
        noteId: String,
        subjectId: String
    ): Result<List<UnitItem>> {
        return try {
            val response = db.listDocuments(dbId, AppwriteHelper.UNITS_COLLECTION,listOf(Query.equal("subId",subjectId)))
            val units = response.documents.map {
                UnitItem(it.id,it.data["name"].toString(),subjectId)
            }
            Result.success(units)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getChapters(
        noteId: String,
        subjectId: String,
        unitId: String
    ): Result<List<Chapter>> {
        return try {
            val response = db.listDocuments(dbId, AppwriteHelper.CHAPTERS_COLLECTION,listOf(Query.equal("unitId", unitId)))
            val chapters = response.documents.map {
                Chapter(it.id,it.data["name"].toString(),unitId)
            }
            Result.success(chapters)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    // ==========================================
    // 3. PDF UPLOAD, FETCH & DELETE
    // ==========================================

    override suspend fun uploadPdfToChapter(
        noteId: String,
        subjectId: String,
        unitId: String,
        chapterId: String,
        fileName: String,
        fileUri: Uri,
        context: Context
    ): Result<String> {
        return try {
            val file = getFileFromUri(context, fileUri) ?: throw Exception("Invalid file")
            val uploaded = storage.createFile(AppwriteHelper.BUCKET_ID,ID.unique(),InputFile.fromFile(file))
            val fileUrl = "${AppwriteHelper.ENDPOINT}/storage/buckets/${AppwriteHelper.BUCKET_ID}/files/${uploaded.id}/view?project=${AppwriteHelper.PROJECT_ID}"

            val data = mapOf("name" to fileName, "fileUrl" to fileUrl, "fileId" to uploaded.id, "chapterId" to chapterId)
            val doc = db.createDocument(dbId, AppwriteHelper.PDFS_COLLECTION, ID.unique(), data)

            Result.success(doc.id)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPdfs(
        noteId: String,
        subjectId: String,
        unitId: String,
        chapterId: String
    ): Result<List<PdfFile>> {
        return try {
            val response = db.listDocuments(dbId, AppwriteHelper.PDFS_COLLECTION,listOf(Query.equal("chapterId",chapterId)))
            val pdfs = response.documents.map{
                PdfFile(it.id,it.data["name"].toString(),it.data["fileUrl"].toString(),it.data["fileId"].toString(),chapterId)
            }
            Result.success(pdfs)
        }catch (e:Exception){
            Result.failure(e)
        }
    }


    override suspend fun deletePdf(
        noteId: String,
        subjectId: String,
        unitId: String,
        chapterId: String,
        pdfId: String,
        fileId: String
    ): Result<Boolean> {
        return try {
            //1. Deleting Physical file from AppWrite Storage
            storage.deleteFile(AppwriteHelper.BUCKET_ID, fileId)
            //2. Deleting the record from AppWrite Database
            db.deleteDocument(dbId, AppwriteHelper.PDFS_COLLECTION, pdfId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 4. SEARCH & ACCESS REQUESTS
    // ==========================================

    override suspend fun searchNotesBySubject(query: String): Result<List<Note>> {
        return try {
            // Find subjects matching query
            val subResponse = db.listDocuments(dbId, AppwriteHelper.SUBJECTS_COLLECTION, listOf(Query.search("name",query)))
            val notesIds = subResponse.documents.map { it.data["noteId"].toString() }.distinct()

            val notes = mutableListOf<Note>()
            for (id in notesIds){
                val doc = db.getDocument(dbId, AppwriteHelper.NOTES_COLLECTION, id)
                notes.add(Note(
                    id = doc.id, name = doc.data["name"].toString(), owner = doc.data["owner"].toString(),
                    allowedUsers = extractList(doc.data["allowedUsers"]),
                    pendingRequests = extractList(doc.data["pendingRequests"])
                ))
            }
            Result.success(notes)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun requestAccess(noteId: String): Result<Boolean> {
        return try {
            val userId = account.get().id
            val doc = db.getDocument(dbId, AppwriteHelper.NOTES_COLLECTION, noteId)
            val currentRequests = extractList(doc.data["pendingRequests"]).toMutableList()

            if (!currentRequests.contains(userId)){
                currentRequests.add(userId)
                db.updateDocument(dbId, AppwriteHelper.NOTES_COLLECTION, noteId, mapOf("pendingRequests" to currentRequests))
            }
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }
    }


    override suspend fun getPendingRequests(ownerId: String): Result<List<Note>> {
        return try {
            // Fetch owner's notes, then filter on device to find ones with pending requests
            val response = db.listDocuments(
                dbId,
                AppwriteHelper.NOTES_COLLECTION,
                listOf(Query.equal("owner", ownerId))
            )
            val requests = response.documents.mapNotNull { doc ->
                val pending = extractList(doc.data["pendingRequests"])
                if (pending.isNotEmpty()) {
                    Note(
                        doc.id,
                        doc.data["name"].toString(),
                        ownerId, pendingRequests = pending
                    )
                } else null
            }
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserName(userId: String): String {
        return try {
            // REPLACE "users" with your actual Collection ID if it is different!
            val doc = db.getDocument(dbId, "users", userId)
            doc.data["name"].toString()
        } catch (e: Exception) {
            android.util.Log.e("GetUserName", "Failed to find user: ${e.message}")
            "Unknown User" // Fallback just in case
        }
    }

    override suspend fun approveAccess(
        noteId: String,
        requesterId: String
    ): Result<Boolean> {
        return try{
            val doc = db.getDocument(dbId, AppwriteHelper.NOTES_COLLECTION, noteId)
            val pending = extractList(doc.data["pendingRequests"]).toMutableList()
            val allowed = extractList(doc.data["allowedUsers"]).toMutableList()

            pending.remove(requesterId)
            if (!allowed.contains(requesterId)) {
                allowed.add(requesterId)
            }
            db.updateDocument(dbId, AppwriteHelper.NOTES_COLLECTION, noteId, mapOf("pendingRequests" to pending, "allowedUsers" to allowed))
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
    override suspend fun getSharedNotes(userId: String): Result<List<Note>> {
        return try {
            // We use Query.contains to search inside the array of allowedUsers!
            val response = db.listDocuments(
                dbId,
                AppwriteHelper.NOTES_COLLECTION,
                listOf(Query.contains("allowedUsers", userId))
            )

            val notes = response.documents.map { doc ->
                Note(
                    id = doc.id,
                    name = doc.data["name"].toString(),
                    owner = doc.data["owner"].toString(),
                    allowedUsers = extractList(doc.data["allowedUsers"]),
                    pendingRequests = extractList(doc.data["pendingRequests"])
                )
            }
            Result.success(notes)
        } catch (e: Exception) {
            android.util.Log.e("SharedNotes", "Fetch failed: ${e.message}")
            Result.failure(e)
        }
    }
    // 1. Chapter Cleanup (Deletes the Chapter AND its physical PDF files)
    override suspend fun deleteChapter(noteId: String,subjectId: String,unitId: String,chapterId: String): Result<Boolean> {
        return try {
            // Find all PDFs inside this chapter
            val pdfs = db.listDocuments(dbId, AppwriteHelper.PDFS_COLLECTION, listOf(Query.equal("chapterId", chapterId))).documents
            for (pdf in pdfs) {
                // Delete the physical file from the storage bucket
                try { storage.deleteFile(AppwriteHelper.BUCKET_ID, pdf.data["fileId"].toString()) } catch (e: Exception) {}
                // Delete the database row
                db.deleteDocument(dbId, AppwriteHelper.PDFS_COLLECTION, pdf.id)
            }
            // Finally, delete the chapter itself
            db.deleteDocument(dbId, AppwriteHelper.CHAPTERS_COLLECTION, chapterId)
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    // 2. Unit Cleanup (Deletes the Unit AND its Chapters)
    override suspend fun deleteUnit(noteId: String,subjectId: String,unitId: String): Result<Boolean> {
        return try {
            val chapters = db.listDocuments(dbId, AppwriteHelper.CHAPTERS_COLLECTION, listOf(Query.equal("unitId", unitId))).documents
            // Trigger the Chapter Cleanup for every chapter found
            for (chapter in chapters) { deleteChapter(noteId,subjectId,unitId,chapter.id) }

            db.deleteDocument(dbId, AppwriteHelper.UNITS_COLLECTION, unitId)
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    // 3. Subject Cleanup (Deletes the Subject AND its Units)
    override suspend fun deleteSubject(noteId: String,subjectId: String): Result<Boolean> {
        return try {
            // (Note: Using "subId" here because your previous code used that exact column name!)
            val units = db.listDocuments(dbId, AppwriteHelper.UNITS_COLLECTION, listOf(Query.equal("subId", subjectId))).documents
            for (unit in units) { deleteUnit(noteId,subjectId,unit.id) }

            db.deleteDocument(dbId, AppwriteHelper.SUBJECTS_COLLECTION, subjectId)
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    // 4. Note Cleanup (Deletes the main Note AND its Subjects)
    override suspend fun deleteNote(noteId: String): Result<Boolean> {
        return try {
            val subjects = db.listDocuments(dbId, AppwriteHelper.SUBJECTS_COLLECTION, listOf(Query.equal("noteId", noteId))).documents
            for (subject in subjects) { deleteSubject(noteId,subject.id) }

            db.deleteDocument(dbId, AppwriteHelper.NOTES_COLLECTION, noteId)
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }
}
