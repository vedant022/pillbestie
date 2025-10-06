package com.example.pillbestie.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.data.MoodEntry
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val moodEntries by viewModel.moodEntries.collectAsState()
    var showAddMoodDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal & Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMoodDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnalyticsSection()
            }
            item {
                Text("Daily Entries", style = MaterialTheme.typography.titleLarge)
            }
            items(moodEntries) { moodEntry ->
                MoodEntryItem(moodEntry)
                HorizontalDivider()
            }
        }
    }

    if (showAddMoodDialog) {
        AddMoodDialog(
            onDismiss = { showAddMoodDialog = false },
            onConfirm = { mood ->
                viewModel.addMoodEntry(mood)
                showAddMoodDialog = false
            }
        )
    }
}

@Composable
fun AnalyticsSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Your Trends", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            // Placeholder for a graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Adherence vs. Mood graph would be here.")
            }
        }
    }
}

@Composable
fun MoodEntryItem(moodEntry: MoodEntry) {
    val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    val date = format.format(Date(moodEntry.timestamp))
    ListItem(
        headlineContent = { Text(moodEntry.mood, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(date) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoodDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var mood by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How are you feeling?") },
        text = {
            OutlinedTextField(
                value = mood,
                onValueChange = { mood = it },
                label = { Text("e.g., Happy, Tired, etc.") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(mood) },
                enabled = mood.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun JournalScreenPreview() {
    PillBestieTheme {
        JournalScreen(rememberNavController())
    }
}
