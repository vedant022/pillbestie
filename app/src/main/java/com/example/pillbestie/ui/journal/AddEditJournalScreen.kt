package com.example.pillbestie.ui.journal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJournalScreen(navController: NavController, viewModel: AddEditJournalViewModel = viewModel(factory = com.example.pillbestie.ui.ViewModelFactory(LocalContext.current))) {
    var text by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Journal Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveJournalEntry(text, selectedMood, imageUri?.toString())
                navController.popBackStack()
            }) {
                Text("Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoodSelector(selectedMood) { selectedMood = it }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("How are you feeling?") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Spacer(Modifier.height(16.dp))
            imageUri?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(128.dp))
            }
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Add Image")
            }
        }
    }
}

@Composable
fun MoodSelector(selectedMood: String, onMoodSelected: (String) -> Unit) {
    val moods = listOf("😊", "😔", "😠", "😢", "🤣")
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        moods.forEach { mood ->
            TextButton(onClick = { onMoodSelected(mood) }) {
                Text(mood, style = MaterialTheme.typography.headlineLarge, color = if (mood == selectedMood) MaterialTheme.colorScheme.primary else LocalContentColor.current)
            }
        }
    }
}
