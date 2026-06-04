package com.abhishek.noteslibapp.presentation.ui.pages

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhishek.noteslibapp.R
import com.abhishek.noteslibapp.data.model.ChapterInput
import com.abhishek.noteslibapp.data.model.SubjectInput
import com.abhishek.noteslibapp.data.model.UnitInput
import com.abhishek.noteslibapp.presentation.viewmodel.AuthViewModel
import com.abhishek.noteslibapp.presentation.viewmodel.NotesViewModel


@Composable
fun AddNotesPage(notesViewModel: NotesViewModel,navController: NavController,authViewModel: AuthViewModel){
    var showDialog by remember { mutableStateOf(false) }


    // 1. Observe your notes and user data
    val myNotes by notesViewModel.notes.collectAsState()
    val user by authViewModel.userData.collectAsState()

    // Load notes when this tab is opened
    LaunchedEffect(user?.id) {
        user?.id?.let { uid -> notesViewModel.loadNotes(uid) }
    }

    Scaffold(modifier = Modifier.fillMaxSize().padding(10.dp),
        floatingActionButton = {
            // 2. Wire up the Add button!
            FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier.padding(bottom = 80.dp),containerColor = colorResource(R.color.Primary_color),contentColor = colorResource(R.color.white)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }){innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text(text = "My Note Folders", fontSize = 24.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.antic_regular)),
                color=colorResource(R.color.Primary_color))
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Display the created notes here
            LazyColumn {
                items(myNotes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Navigate straight into the note to add PDFs!
                                navController.navigate("subjectscreen/${note.id}")
                            }
                    ) {
                        Text(text = note.name, modifier = Modifier.padding(16.dp), fontSize = 18.sp,fontFamily = FontFamily(Font(R.font.antic_regular)),
                            color=colorResource(R.color.Primary_color))
                    }
                }
            }


        }
        if (showDialog){
            AddNoteDialog(onDismiss = { showDialog = false },notesViewModel, userId = user?.id ?:"")
        }
    }

}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit,notesViewModel: NotesViewModel,userId: String) {

    var step by remember { mutableStateOf(1) }
    var noteName by remember { mutableStateOf("") }
    var subjectCount by remember { mutableStateOf(0) }
    var subjects by remember { mutableStateOf(mutableListOf<SubjectInput>()) }
    var isSaving by remember { mutableStateOf(false) } // To show a loading state
    val context = LocalContext.current

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(text = "Add Note : step $step of 5") },
        text = {
            if (isSaving) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Building your note hierarchy...", modifier = Modifier.padding(top = 16.dp))
                }
            }else{

                when (step) {
                    1 -> {
                        Column {
                            Text("Enter Note Name",fontFamily = FontFamily(Font(R.font.antic_regular)), color=colorResource(R.color.Primary_color))
                            OutlinedTextField(
                                value = noteName,
                                onValueChange = {noteName=it},
                                placeholder = { Text("Note Name")
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorResource(id = R.color.Secondary_color),
                                    unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                                    unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                                    focusedTextColor = colorResource(id = R.color.Primary_color)
                                )
                            )
                        }
                    }
                    2 -> {
                        Column {
                            Text("Enter Subject Details",fontFamily = FontFamily(Font(R.font.antic_regular)), color=colorResource(R.color.Primary_color))
                            OutlinedTextField(
                                value = if(subjectCount > 0) subjectCount.toString() else "",
                                onValueChange = {
                                    subjectCount = it.toIntOrNull() ?: 0
                                    if(subjectCount > 0){
                                        subjects = MutableList(subjectCount){ SubjectInput() }.toMutableList()
                                    }
                                },
                                placeholder = { Text("Subject Count") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorResource(id = R.color.Secondary_color),
                                    unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                                    unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                                    focusedTextColor = colorResource(id = R.color.Primary_color)
                                )
                            )
                        }
                    }
                    3 -> {
                        LazyColumn {
                            itemsIndexed(subjects){index,subject ->
                                Column(Modifier.padding(8.dp)){
                                    OutlinedTextField(
                                        value = subjects[index].name,
                                        onValueChange = {newName ->
                                            // 1. Copy the list
                                            val updatedList = subjects.toMutableList()
                                            // 2. Replace the item with a BRAND NEW copy containing the new text
                                            updatedList[index] = updatedList[index].copy(name = newName)
                                            // 3. Give the fresh list back to Compose
                                            subjects = updatedList },
                                        label = {Text("Subject ${index+1} Name")},
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = if(subjects[index].unitCount > 0) subjects[index].unitCount.toString() else "",
                                        onValueChange = { newCountStr ->
                                            val count = newCountStr.toIntOrNull() ?: 0

                                            // 1. Copy the list
                                            val updatedList = subjects.toMutableList()

                                            // 2. Prepare the new units list
                                            val newUnits = if (count > 0) {
                                                MutableList(count) { UnitInput() }.toMutableList()
                                            } else {
                                                mutableListOf()
                                            }

                                            // 3. Replace the item with a new copy containing the updated count AND units
                                            updatedList[index] = updatedList[index].copy(
                                                unitCount = count,
                                                units = newUnits
                                            )

                                            // 4. Give the fresh list back to Compose
                                            subjects = updatedList
                                        },
                                        label = {Text("Number of Units")},
                                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = colorResource(id = R.color.Secondary_color),
                                            unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                                            unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                                            focusedTextColor = colorResource(id = R.color.Primary_color)
                                        )
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        LazyColumn {
                            // CHANGED to forEachIndexed to get the sIndex (subject index)
                            subjects.forEachIndexed { sIndex, subject ->
                                item {
                                    Text(
                                        text = "Subject: ${subject.name}",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top=8.dp),
                                        fontFamily = FontFamily(Font(R.font.antic_regular)),
                                        color=colorResource(R.color.Primary_color)
                                    )
                                }
                                itemsIndexed(subject.units) { uIndex, unit ->
                                    Column(Modifier.padding(8.dp)) {
                                        OutlinedTextField(
                                            value = unit.name,
                                            onValueChange = { newName ->
                                                // 1. Copy the lists
                                                val updatedSubjects = subjects.toMutableList()
                                                val updatedUnits = subject.units.toMutableList()

                                                // 2. Deep copy the unit, then the subject
                                                updatedUnits[uIndex] = unit.copy(name = newName)
                                                updatedSubjects[sIndex] = subject.copy(units = updatedUnits)

                                                // 3. Update the state
                                                subjects = updatedSubjects
                                            },
                                            label = { Text("Unit ${uIndex + 1} Name") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                                                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                                                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                                                focusedTextColor = colorResource(id = R.color.Primary_color)
                                            )
                                        )
                                        OutlinedTextField(
                                            value = if (unit.chapterCount > 0) unit.chapterCount.toString() else "",
                                            onValueChange = { newCountStr ->
                                                val count = newCountStr.toIntOrNull() ?: 0

                                                // 1. Copy the lists
                                                val updatedSubjects = subjects.toMutableList()
                                                val updatedUnits = subject.units.toMutableList()

                                                // 2. Prepare the new chapters list
                                                val newChapters = if (count > 0) {
                                                    MutableList(count) { ChapterInput() }.toMutableList()
                                                } else {
                                                    mutableListOf()
                                                }

                                                // 3. Deep copy the unit, then the subject
                                                updatedUnits[uIndex] = unit.copy(
                                                    chapterCount = count,
                                                    chapters = newChapters
                                                )
                                                updatedSubjects[sIndex] = subject.copy(units = updatedUnits)

                                                // 4. Update the state
                                                subjects = updatedSubjects
                                            },
                                            label = { Text("Number of Chapters") },
                                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    5 -> {
                        LazyColumn {
                            // CHANGED to forEachIndexed to get both sIndex and uIndex
                            subjects.forEachIndexed { sIndex, subject ->
                                subject.units.forEachIndexed { uIndex, unit ->
                                    item {
                                        Text(
                                            text = "Unit: ${unit.name}",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top=8.dp),
                                            fontFamily = FontFamily(Font(R.font.antic_regular)),
                                            color=colorResource(R.color.Primary_color)
                                        )
                                    }
                                    itemsIndexed(unit.chapters) { cIndex, chapter ->
                                        OutlinedTextField(
                                            value = chapter.name,
                                            onValueChange = { newName ->
                                                // 1. Copy ALL the nested lists
                                                val updatedSubjects = subjects.toMutableList()
                                                val updatedUnits = subject.units.toMutableList()
                                                val updatedChapters = unit.chapters.toMutableList()

                                                // 2. Update the chapter -> unit -> subject like a ladder
                                                updatedChapters[cIndex] = chapter.copy(name = newName)
                                                updatedUnits[uIndex] = unit.copy(chapters = updatedChapters)
                                                updatedSubjects[sIndex] = subject.copy(units = updatedUnits)

                                                // 3. Give the completely fresh tree back to Compose
                                                subjects = updatedSubjects
                                            },
                                            label = { Text("Chapter ${cIndex + 1} Name") },
                                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                                                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                                                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                                                focusedTextColor = colorResource(id = R.color.Primary_color)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, confirmButton ={
            if (!isSaving) {
                Row {
                    if (step > 1) {
                        TextButton(onClick = { step-- }, colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.Primary_color)
                        )) { Text("Back",fontFamily = FontFamily(Font(R.font.antic_regular))) }
                    }
                    Button(onClick = {
                        if (step < 5) {
                            step++
                        } else {
                            isSaving = true
                            // Pass the 3 arguments, and handle the 2 callback variables
                            notesViewModel.createFullNoteHierarchy(noteName, subjects, userId) { success, errorMessage ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Note Created Successfully!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    // THIS WILL TELL YOU EXACTLY WHAT APPWRITE IS MISSING!
                                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.Primary_color))
                    ) {
                        Text(if (step == 5) "Create Note" else "Next")
                    }
                }
            }
        } )
}






/*@Preview(showBackground =true)
@Composable
fun AddNotesPagePreview(notesViewModel: NotesViewModel){
    AddNotesPage(
        notesViewModel = notesViewModel
    )
}*/
