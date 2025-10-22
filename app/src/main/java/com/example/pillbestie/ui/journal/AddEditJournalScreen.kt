package com.example.pillbestie.ui.journal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJournalScreen(
    navController: NavController,
    viewModel: AddEditJournalViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    doseLogId: Int? = null
) {
    var text by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val moods = listOf("😀", "🙂", "😐", "😔", "😡")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (doseLogId == null) "Add Journal Entry" else "Edit Journal Entry") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val doseLog = DoseLog(
                        id = doseLogId ?: 0, 
                        notes = text,
                        medicineId = 0, // This will need to be updated
                        scheduledTime = 0, 
                        takenTime = System.currentTimeMillis(), 
                        wasMissed = false,
                        mood = selectedMood,
                        photoUri = imageUri?.toString()
                    )
                    viewModel.saveDoseLog(doseLog)
                    navController.popBackStack()
                }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxSize()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("How are you feeling?", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(moods) { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(mood) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Add Photo")
            }
            imageUri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Photo selected: ${it.path}")
            }
        }
    }
}
