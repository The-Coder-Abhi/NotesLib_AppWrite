package com.abhishek.noteslibapp.presentation.ui

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhishek.noteslibapp.R

@Composable
fun FolderScreenLayout(
    title: String,
    items: List<Pair<String, String>>,
    onItemClick: (String) -> Unit,
    isOwner: Boolean,
    onDeleteClick: (String) -> Unit={}
){
    var itemToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    // 2. THE DIALOG: Only shows up if itemToDelete is NOT null
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null }, // Closes if they tap outside
            title = { Text("Delete Folder") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.second}'? Everything inside this folder will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 3. THE CONFIRMATION: Pass the ID to the ViewModel and close the dialog
                        itemToDelete?.first?.let { id -> onDeleteClick(id) }
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { // Just close the dialog
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onItemClick(item.first) }
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = "Folder Icon", tint = colorResource(R.color.Primary_color))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = item.second, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        //Conditional Delete Button
                        if (isOwner) {
                            IconButton(onClick = { itemToDelete = item }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Folder",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}