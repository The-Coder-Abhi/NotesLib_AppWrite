package com.muktasapp.noteslibapp.data

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteHelper {
    const val ENDPOINT = "https://nyc.cloud.appwrite.io/v1" // Or your self-hosted endpoint
    const val PROJECT_ID = "69a957cf0009b1572325"
    const val DATABASE_ID = "69a959590014f2872aa1"
    const val BUCKET_ID = "69a959b3003acb1015ef"
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