package com.example.pillbestie.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.notifications.NotificationScheduler
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme
import kotlinx.coroutines.launch
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
    var pillsRemaining by remember { mutableStateOf("") }
    var remindBeforeDays by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var timesPerDay by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()
    var interactions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Potential Drug Interactions") },
            text = { 
                LazyColumn {
                    items(interactions) { interaction ->
                        Text(interaction)
                    }
                }
             },
            confirmButton = {
                Button(
                    onClick = { 
                        showDialog = false 
                        coroutineScope.launch {
                            val medicine = Medicine(
                                name = medicineName, 
                                dosage = dosage, 
                                timeInMillis = selectedTime!!.timeInMillis, 
                                frequency = selectedFrequency,
                                timesPerDay = timesPerDay,
                                pillsRemaining = pillsRemaining.toIntOrNull(),
                                remindBeforeDays = remindBeforeDays.toIntOrNull()
                            )
                            viewModel.addMedicine(medicine) {
                                scheduler.scheduleNotification(medicineName, dosage, selectedTime!!.timeInMillis)
                                navController.popBackStack()
                            }
                        }
                    }
                ) {
                    Text("Proceed Anyway")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                MedicineDetailsSection(
                    medicineName, { medicineName = it }, 
                    dosage, { dosage = it },
                    pillsRemaining, { pillsRemaining = it },
                    remindBeforeDays, { remindBeforeDays = it }
                )
            }
            item {
                ScheduleSection(
                    selectedTime, { time -> selectedTime = time },
                    selectedFrequency, { frequency -> selectedFrequency = frequency },
                    timesPerDay, { times -> timesPerDay = times }
                )
            }
            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val interactionResult = viewModel.checkInteractions(medicineName)
                            if (interactionResult.isNotEmpty()) {
                                interactions = interactionResult
                                showDialog = true
                            } else {
                                selectedTime?.let { time ->
                                    val medicine = Medicine(
                                        name = medicineName, 
                                        dosage = dosage, 
                                        timeInMillis = time.timeInMillis, 
                                        frequency = selectedFrequency,
                                        timesPerDay = timesPerDay,
                                        pillsRemaining = pillsRemaining.toIntOrNull(),
                                        remindBeforeDays = remindBeforeDays.toIntOrNull()
                                    )
                                    viewModel.addMedicine(medicine) {
                                        scheduler.scheduleNotification(medicineName, dosage, time.timeInMillis)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
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
    onDosageChange: (String) -> Unit,
    pillsRemaining: String,
    onPillsRemainingChange: (String) -> Unit,
    remindBeforeDays: String,
    onRemindBeforeDaysChange: (String) -> Unit
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
            label = { Text("Dosage (e.g., 500)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pillsRemaining,
            onValueChange = onPillsRemainingChange,
            label = { Text("Pills Remaining (optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = remindBeforeDays,
            onValueChange = onRemindBeforeDaysChange,
            label = { Text("Remind before (days, optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSection(
    selectedTime: Calendar?,
    onTimeSelected: (Calendar) -> Unit,
    selectedFrequency: String,
    onFrequencySelected: (String) -> Unit,
    timesPerDay: Int,
    onTimesPerDayChanged: (Int) -> Unit
) {
    val frequencies = listOf("Daily", "Once a week", "Twice a week", "Once a month", "Twice a month", "Alternate days")
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    Column {
        Text("Schedule", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedFrequency,
                onValueChange = {},
                label = { Text("Frequency") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                frequencies.forEach { frequency ->
                    DropdownMenuItem(text = { Text(frequency) }, onClick = {
                        onFrequencySelected(frequency)
                        expanded = false
                    })
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Text("How many times per day?", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { times ->
                FilterChip(
                    selected = timesPerDay == times,
                    onClick = { onTimesPerDayChanged(times) },
                    label = { Text("$times x") }
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
                TimeInput(state = timePickerState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(Calendar.MINUTE, timePickerState.minute)

                        // Ensure the selected time is in the future
                        if (cal.before(Calendar.getInstance())) {
                            cal.add(Calendar.DATE, 1)
                        }

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
