package com.example.pillbestie.ui.medicine

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
    val uiState by viewModel.uiState.collectAsState()

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var quantityPerDose by remember { mutableStateOf("1") }
    var pillsRemaining by remember { mutableStateOf("") }
    var remindBeforeDays by remember { mutableStateOf("") }
    var durationValue by remember { mutableStateOf("") }
    var durationUnit by remember { mutableStateOf("Days") }
    var selectedTimes by remember { mutableStateOf<List<Calendar>>(emptyList()) }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var timesPerDay by remember { mutableStateOf(1) }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { /* User has returned from settings. You might want to re-check permission here */ }
    )

    LaunchedEffect(uiState) {
        if (uiState is AddMedicineUiState.Success) {
            navController.popBackStack()
        }
    }

    if (uiState is AddMedicineUiState.ShowInteractionWarning) {
        val interactions = (uiState as AddMedicineUiState.ShowInteractionWarning).interactions
        AlertDialog(
            onDismissRequest = { viewModel.onDialogDismissed() },
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
                        val medicine = Medicine(name = medicineName, dosage = dosage, quantityPerDose = quantityPerDose.toIntOrNull() ?: 1, times = selectedTimes.map { it.timeInMillis }, frequency = selectedFrequency, timesPerDay = timesPerDay, pillsRemaining = pillsRemaining.toIntOrNull(), remindBeforeDays = remindBeforeDays.toIntOrNull(), durationValue = durationValue.toIntOrNull(), durationUnit = durationUnit)
                        viewModel.addMedicineAndFinish(medicine)
                    }
                ) {
                    Text("Proceed Anyway")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.onDialogDismissed() }) {
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
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            MedicineDetailsSection(medicineName, { medicineName = it }, dosage, { dosage = it }, quantityPerDose, { quantityPerDose = it }, pillsRemaining, { pillsRemaining = it }, remindBeforeDays, { remindBeforeDays = it }, durationValue, { durationValue = it }, durationUnit, { durationUnit = it })
                        }
                    }
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ScheduleSection(selectedTimes, { time -> selectedTimes = selectedTimes + time }, { time -> selectedTimes = selectedTimes - time }, selectedFrequency, { frequency -> selectedFrequency = frequency }, timesPerDay, { times -> timesPerDay = times })
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                                    settingsLauncher.launch(it)
                                }
                            } else {
                                if (selectedTimes.isNotEmpty()) {
                                    val medicine = Medicine(name = medicineName, dosage = dosage, quantityPerDose = quantityPerDose.toIntOrNull() ?: 1, times = selectedTimes.map { it.timeInMillis }, frequency = selectedFrequency, timesPerDay = timesPerDay, pillsRemaining = pillsRemaining.toIntOrNull(), remindBeforeDays = remindBeforeDays.toIntOrNull(), durationValue = durationValue.toIntOrNull(), durationUnit = durationUnit)
                                    viewModel.saveMedicine(medicine)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedTimes.isNotEmpty() && uiState !is AddMedicineUiState.Saving
                    ) {
                        if (uiState is AddMedicineUiState.Saving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Add Medicine", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailsSection(
    medicineName: String,
    onMedicineNameChange: (String) -> Unit,
    dosage: String,
    onDosageChange: (String) -> Unit,
    quantityPerDose: String,
    onQuantityPerDoseChange: (String) -> Unit,
    pillsRemaining: String,
    onPillsRemainingChange: (String) -> Unit,
    remindBeforeDays: String,
    onRemindBeforeDaysChange: (String) -> Unit,
    durationValue: String,
    onDurationValueChange: (String) -> Unit,
    durationUnit: String,
    onDurationUnitChange: (String) -> Unit
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
            label = { Text("Dosage (e.g., 500mg)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = quantityPerDose,
            onValueChange = onQuantityPerDoseChange,
            label = { Text("Quantity per dose (e.g., 1)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pillsRemaining,
            onValueChange = onPillsRemainingChange,
            label = { Text("Total pills in bottle (optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = remindBeforeDays,
            onValueChange = onRemindBeforeDaysChange,
            label = { Text("Remind to refill when X days are left") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(16.dp))
        Text("Treatment Duration", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = durationValue,
                onValueChange = onDurationValueChange,
                label = { Text("Duration") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.width(8.dp))
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = durationUnit,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Days") }, onClick = {
                        onDurationUnitChange("Days")
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Months") }, onClick = {
                        onDurationUnitChange("Months")
                        expanded = false
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSection(
    selectedTimes: List<Calendar>,
    onTimeSelected: (Calendar) -> Unit,
    onTimeRemoved: (Calendar) -> Unit,
    selectedFrequency: String,
    onFrequencySelected: (String) -> Unit,
    timesPerDay: Int,
    onTimesPerDayChanged: (Int) -> Unit
) {
    val frequencies = listOf("Daily", "Once a week", "Twice a week", "Once a month", "Twice a month", "Alternate days")
    var showTimePicker by remember { mutableStateOf(false) }

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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showTimePicker = true }) {
                Text("Add Time")
            }
            selectedTimes.forEach { time ->
                val format = SimpleDateFormat("h:mm a", Locale.getDefault())
                InputChip(
                    selected = false,
                    onClick = { onTimeRemoved(time) },
                    label = { Text(format.format(time.time)) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove time") }
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                if (cal.before(Calendar.getInstance())) {
                    cal.add(Calendar.DATE, 1)
                }

                onTimeSelected(cal)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
) {
    val timePickerState: TimePickerState = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            Button(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    PillBestieTheme {
        AddMedicineScreen(rememberNavController())
    }
}
