package com.example.pillbestie.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.notifications.NotificationScheduler
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    navController: NavController,
    viewModel: AddMedicineViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val scheduler = remember { NotificationScheduler(context) }
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Medicine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MedicineDetailsSection(medicineName, { medicineName = it }, dosage, { dosage = it })
            }
            item {
                ScheduleSection(selectedTime) { time -> selectedTime = time }
            }
            item {
                Button(
                    onClick = {
                        selectedTime?.let {
                            val medicine = Medicine(name = medicineName, dosage = dosage, timeInMillis = it.timeInMillis)
                            viewModel.addMedicine(medicine)
                            scheduler.scheduleNotification(medicineName, dosage, it.timeInMillis)
                        }
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedTime != null
                ) {
                    Text("Add Medicine", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun MedicineDetailsSection(
    medicineName: String,
    onMedicineNameChange: (String) -> Unit,
    dosage: String,
    onDosageChange: (String) -> Unit
) {
    Column {
        Text("Medicine Details", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = medicineName,
            onValueChange = onMedicineNameChange,
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dosage,
            onValueChange = onDosageChange,
            label = { Text("Dosage (e.g., 500mg, 1 tablet)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSection(selectedTime: Calendar?, onTimeSelected: (Calendar) -> Unit) {
    var selectedTimesPerDay by remember { mutableStateOf("1x") }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    Column {
        Text("Schedule", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("How many times per day?", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("1x", "2x", "3x", "4x").forEach { time ->
                FilterChip(
                    selected = selectedTimesPerDay == time,
                    onClick = { selectedTimesPerDay = time },
                    label = { Text(time) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { showTimePicker = true }) {
                Text("Select Time")
            }
            Spacer(Modifier.width(16.dp))
            selectedTime?.let {
                val format = SimpleDateFormat("h:mm a", Locale.getDefault())
                Text(format.format(it.time), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(Calendar.MINUTE, timePickerState.minute)
                        onTimeSelected(cal)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    PillBestieTheme {
        AddMedicineScreen(rememberNavController())
    }
}
