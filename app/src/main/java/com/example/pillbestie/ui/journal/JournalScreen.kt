package com.example.pillbestie.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pillbestie.AppRoutes
import com.example.pillbestie.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val allDoseLogs by viewModel.allDoseLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppRoutes.ADD_EDIT_JOURNAL) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allDoseLogs) { doseLog ->
                Card(
                    onClick = { navController.navigate("${AppRoutes.ADD_EDIT_JOURNAL}/${doseLog.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dose Log: ${doseLog.id}") // Placeholder
                        Text("Status: ${doseLog.status}")
                        doseLog.mood?.let { mood -> Text("Mood: $mood") }
                        doseLog.notes?.let { notes -> Text("Notes: $notes") }
                    }
                }
            }
        }
    }
}
