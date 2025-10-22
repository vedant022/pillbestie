package com.example.pillbestie.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    navController: NavController,
    medicineId: Int,
    editViewModel: EditMedicineViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    homeViewModel: HomeViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val medicine by editViewModel.getMedicine(medicineId).collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Medication") },
            text = { Text("Are you sure you want to delete this medication? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            medicine?.let { 
                                homeViewModel.deleteMedicine(it)
                                navController.popBackStack()
                            }
                            showDeleteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(medicine?.name ?: "Medication Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = { navController.navigate("edit_medicine/$medicineId") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        medicine?.let {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dosage: ${it.dosage}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Pills Remaining: ${it.pillsRemaining ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Remind before: ${it.remindBeforeDays?.toString() ?: "N/A"} days", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Scheduled Times", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        it.times.forEach { time ->
                            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val timeString = timeFormat.format(Date(time))
                            Text(timeString, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
