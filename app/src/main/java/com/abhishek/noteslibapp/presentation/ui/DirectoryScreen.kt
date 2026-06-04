package com.abhishek.noteslibapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DirectoryScreen(
    title: String,
    items: List<Pair<String, String>>, // Pair<Id, Name>
    onItemClick: (String) -> Unit,
    onAddPdfClick: (() -> Unit)? = null // Only pass this when inside a Chapter
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onItemClick(item.first) }
                ) {
                    Text(text = item.second, modifier = Modifier.padding(16.dp))
                }
            }
        }

        // Only show Add PDF button if we are inside a Chapter
        if (onAddPdfClick != null) {
            Button(
                onClick = onAddPdfClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add PDF File")
            }
        }
    }
}