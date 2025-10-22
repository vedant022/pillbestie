package com.example.pillbestie.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pillbestie.data.Personality
import com.example.pillbestie.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val personality by viewModel.personality.collectAsState(initial = Personality.CARING)
    val profileName by viewModel.profileName.collectAsState(initial = "")
    val reminderFrequency by viewModel.reminderFrequency.collectAsState(initial = 3)
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState(initial = true)
    val drugInteractionCheckEnabled by viewModel.drugInteractionCheckEnabled.collectAsState(initial = true)

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
        uri?.let { viewModel.backupData(it, context) }
    }

    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.restoreData(it, context) }
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        uri?.let { viewModel.exportMedsToCsv(it, context) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileSettings(profileName, { viewModel.setProfileName(it) })
            }
            item {
                PersonalitySettings(personality, { viewModel.setPersonality(it) })
            }
            item {
                NotificationSettings(
                    reminderFrequency = reminderFrequency,
                    onReminderFrequencyChange = { viewModel.setReminderFrequency(it) },
                    vibrationEnabled = vibrationEnabled,
                    onVibrationEnabledChange = { viewModel.setVibrationEnabled(it) }
                )
            }
            item {
                SafetyFeaturesSettings(
                    drugInteractionCheckEnabled = drugInteractionCheckEnabled,
                    onDrugInteractionCheckEnabledChange = { viewModel.setDrugInteractionCheckEnabled(it) }
                )
            }
            item {
                DataManagementSettings(
                    onBackup = { backupLauncher.launch("pillbestie_backup.zip") },
                    onRestore = { restoreLauncher.launch("application/zip") },
                    onExport = { exportLauncher.launch("medicines.csv") },
                    onClearData = { viewModel.clearAllData() }
                )
            }
        }
    }
}

@Composable
fun ProfileSettings(profileName: String, onProfileNameChange: (String) -> Unit) {
    var name by remember { mutableStateOf(profileName) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onProfileNameChange(name) }) {
                Text("Save Name")
            }
        }
    }
}

@Composable
fun PersonalitySettings(selectedPersonality: Personality, onPersonalitySelected: (Personality) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bestie Personality", style = MaterialTheme.typography.titleLarge)
            Personality.values().forEach { personality ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedPersonality == personality,
                        onClick = { onPersonalitySelected(personality) }
                    )
                    Text(personality.name)
                }
            }
        }
    }
}

@Composable
fun NotificationSettings(
    reminderFrequency: Int,
    onReminderFrequencyChange: (Int) -> Unit,
    vibrationEnabled: Boolean,
    onVibrationEnabledChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Reminder Frequency: $reminderFrequency")
            Slider(
                value = reminderFrequency.toFloat(),
                onValueChange = { onReminderFrequencyChange(it.toInt()) },
                valueRange = 0f..5f,
                steps = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vibration")
                Switch(checked = vibrationEnabled, onCheckedChange = onVibrationEnabledChange)
            }
        }
    }
}

@Composable
fun SafetyFeaturesSettings(
    drugInteractionCheckEnabled: Boolean,
    onDrugInteractionCheckEnabledChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Safety Features", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Drug Interaction Checker")
                    Text("Check for potential drug interactions when adding a new medicine", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = drugInteractionCheckEnabled, onCheckedChange = onDrugInteractionCheckEnabledChange)
            }
        }
    }
}


@Composable
fun DataManagementSettings(
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onExport: () -> Unit,
    onClearData: () -> Unit
) {
    var showClearDataDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Data Management", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onBackup, modifier = Modifier.fillMaxWidth()) {
                Text("Backup Data")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRestore, modifier = Modifier.fillMaxWidth()) {
                Text("Restore Data")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                Text("Export to CSV")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showClearDataDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Clear All Data")
            }
        }
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Data?") },
            text = { Text("This will permanently delete all your data. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { 
                        onClearData()
                        showClearDataDialog = false 
                    }
                ) {
                    Text("Clear Data")
                }
            },
            dismissButton = {
                Button(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
