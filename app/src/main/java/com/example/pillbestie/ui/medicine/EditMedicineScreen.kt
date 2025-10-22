package com.example.pillbestie.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.ui.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineScreen(
    navController: NavController,
    medicineId: Int,
    viewModel: EditMedicineViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val medicine by viewModel.getMedicine(medicineId).collectAsState(initial = null)

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var pillsRemaining by remember { mutableStateOf("") }
    var remindBeforeDays by remember { mutableStateOf("") }
    var selectedTimes by remember { mutableStateOf<List<Calendar>>(emptyList()) }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var timesPerDay by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(medicine) {
        medicine?.let {
            medicineName = it.name
            dosage = it.dosage
            pillsRemaining = it.pillsRemaining?.toString() ?: ""
            remindBeforeDays = it.remindBeforeDays?.toString() ?: ""
            selectedFrequency = it.frequency
            timesPerDay = it.timesPerDay
            selectedTimes = it.times.map { timeInMillis ->
                Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Medicine") },
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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Medicine Details", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = medicineName,
                            onValueChange = { medicineName = it },
                            label = { Text("Medicine Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosage (e.g., 500)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pillsRemaining,
                            onValueChange = { pillsRemaining = it },
                            label = { Text("Pills Remaining (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = remindBeforeDays,
                            onValueChange = { remindBeforeDays = it },
                            label = { Text("Remind before (days, optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ScheduleSection(
                            selectedTimes,
                            { time -> selectedTimes = selectedTimes + time },
                            { time -> selectedTimes = selectedTimes - time },
                            selectedFrequency, { frequency -> selectedFrequency = frequency },
                            timesPerDay, { times -> timesPerDay = times }
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updatedMedicine = Medicine(
                                id = medicineId,
                                name = medicineName,
                                dosage = dosage,
                                times = selectedTimes.map { it.timeInMillis },
                                frequency = selectedFrequency,
                                timesPerDay = timesPerDay,
                                pillsRemaining = pillsRemaining.toIntOrNull(),
                                remindBeforeDays = remindBeforeDays.toIntOrNull()
                            )
                            viewModel.updateMedicine(updatedMedicine) {
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedTimes.isNotEmpty()
                ) {
                    Text("Update Medicine", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
