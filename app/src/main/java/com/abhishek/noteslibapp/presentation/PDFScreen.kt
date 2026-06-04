package com.muktasapp.noteslibapp.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.muktasapp.noteslibapp.data.model.PdfFile
import com.muktasapp.noteslibapp.presentation.ui.getFileName
import com.muktasapp.noteslibapp.presentation.viewmodel.NotesViewModel

@Composable
fun PDFScreen(
    noteId: String,
    subjectId: String,
    unitId: String,
    chapterId: String,
    navController: NavHostController,
    notesViewModel: NotesViewModel
) {
    val pdfs by notesViewModel.pdfs.collectAsState()
    val context = LocalContext.current

    // State to show a loading spinner while uploading
    var isUploading by remember { mutableStateOf(false) }

    // State to track which PDF the user is trying to delete
    var pdfToDelete by remember { mutableStateOf<PdfFile?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            isUploading = true
            val fileName = getFileName(context, selectedUri)

            notesViewModel.uploadPdfFile(
                noteId = noteId,
                subjectId = subjectId,
                unitId = unitId,
                chapterId = chapterId,
                fileName = fileName,
                uri = selectedUri,
                context = context
            ) { success ->
                isUploading = false
                if (success) {
                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // The Confirmation Dialog
    if (pdfToDelete != null) {
        AlertDialog(
            onDismissRequest = { pdfToDelete = null },
            title = { Text("Delete PDF") },
            text = { Text("Are you sure you want to delete '${pdfToDelete?.name}'? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        pdfToDelete?.let { pdf ->
                            notesViewModel.deletePdfFile(
                                noteId,
                                subjectId,
                                unitId,
                                chapterId,
                                pdf.id,
                                pdf.fileId
                            ) { success ->
                                isDeleting = false
                                pdfToDelete = null
                                if (success) {
                                    Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { pdfToDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    LaunchedEffect(chapterId) {
        notesViewModel.loadPdfs(noteId, subjectId, unitId, chapterId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Launch the picker looking specifically for PDFs
                pdfPickerLauncher.launch("application/pdf")

            },modifier = Modifier.padding(bottom = 80.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add PDF")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Text(text = "PDF Files", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Uploading PDF to Firebase...",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            LazyColumn {
                items(pdfs) { pdf ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                        //Create the Intent to view the PDF
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(pdf.fileUrl), "application/pdf")
                            flags =
                                Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "No PDF viewer found. Opening in browser...",
                                Toast.LENGTH_SHORT
                            ).show()
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf.fileUrl))
                            context.startActivity(browserIntent)
                        }

                    }) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = "PDF Icon",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = pdf.name, fontSize = 18.sp)
                        }

                        //Delete Button
                        IconButton(onClick = { pdfToDelete = pdf }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete PDF",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}