package com.muktasapp.noteslibapp.data

import android.content.Context
import io.appwrite.BuildConfig
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteHelper {
    const val ENDPOINT = BuildConfig.APPWRITE_ENDPOINT
    const val PROJECT_ID = BuildConfig.APPWRITE_PROJECT_ID
    const val DATABASE_ID = BuildConfig.APPWRITE_DATABASE_ID
    const val BUCKET_ID = BuildConfig.APPWRITE_BUCKET_ID
//    const val APPWRITE_PROJECT_NAME = "Appwrite Project"

    // Collection IDs you will create in the Appwrite Console
    const val NOTES_COLLECTION = "notes"
    const val SUBJECTS_COLLECTION = "subjects"
    const val UNITS_COLLECTION = "units"
    const val CHAPTERS_COLLECTION = "chapters"
    const val PDFS_COLLECTION = "pdfs"

    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases
    lateinit var storage: Storage

    fun init(context: Context) {
        client = Client(context).setEndpoint(ENDPOINT).setProject(PROJECT_ID)

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }

}