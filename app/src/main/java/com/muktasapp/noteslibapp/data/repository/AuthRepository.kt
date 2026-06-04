package com.muktasapp.noteslibapp.data.repository


import com.muktasapp.noteslibapp.Result
import com.muktasapp.noteslibapp.data.AppwriteHelper
import com.muktasapp.noteslibapp.data.model.Users
import io.appwrite.ID
import io.appwrite.models.User



class AuthRepository {
    private val account = AppwriteHelper.account

    // ADD THESE TWO LINES: Pulling the database instances from your Helper
    private val db = AppwriteHelper.databases
    private val dbId = AppwriteHelper.DATABASE_ID

    suspend fun signUp(email: String, password: String, name: String): Result<Boolean> {
        return try {
            // 1. Create the user in Appwrite Auth
            account.create(ID.unique(), email, password, name)

            // 2. Log them in (Required before sending emails or writing to the database)
            account.createEmailPasswordSession(email, password)

            // 3. Send verification link
            sendVerificationEmail()

            // 4. Add user to Appwrite Database (Firestore equivalent)
            val user = Users(name = name, email = email)
            saveUserToDatabase(user)

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun saveUserToDatabase(user: Users) {
        // Fetch the currently logged-in user to grab their unique ID
        val currentUser = account.get()

        val data = mapOf(
            "name" to user.name,
            "email" to user.email
        )

        // Using currentUser.id ensures their Auth ID and Database ID match exactly
        db.createDocument(
            databaseId = dbId,
            collectionId = "users", // Make sure this collection exists in Appwrite!
            documentId = currentUser.id,
            data = data
        )
    }

    // Consolidated the two verification functions into this single clean one
    suspend fun sendVerificationEmail(): Result<Boolean> {
        return try {
            // The user will be redirected to this Deep Link after clicking the email button
            val callbackUrl = "https://the-coder-abhi.github.io/noteslib-verify/"

            account.createVerification(url = callbackUrl)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Boolean> {
        return try {
            account.createEmailPasswordSession(email, password)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Throws(Exception::class)
    suspend fun getCurrentUser() = account.get()
    /*suspend fun getCurrentUser(): User<Map<String, Any>>? {
        return try {
            account.get()
        } catch (e: Exception) {
            null
        }
    }*/

    suspend fun isEmailVerified(): Boolean {
        return try {
            val user = account.get()
            // Appwrite's User model has a built-in boolean for this!
            user.emailVerification
        } catch (e: Exception) {
            false
        }
    }
}