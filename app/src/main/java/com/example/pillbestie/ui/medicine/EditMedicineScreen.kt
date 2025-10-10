package com.example.pillbestie.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.pillbestie.notifications.NotificationScheduler
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

    // TODO: Fetch the medicine details using the medicineId

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
                            selectedTime?.let { time ->
                                val medicine = Medicine(
                                    id = medicineId,
                                    name = medicineName,
                                    dosage = dosage,
                                    timeInMillis = time.timeInMillis,
                                    frequency = selectedFrequency,
                                    timesPerDay = timesPerDay,
                                    pillsRemaining = pillsRemaining.toIntOrNull(),
                                    remindBeforeDays = remindBeforeDays.toIntOrNull()
                                )
                                viewModel.addMedicine(medicine) { // This should be update
                                    scheduler.scheduleNotification(medicineName, dosage, time.timeInMillis)
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medicineName.isNotBlank() && dosage.isNotBlank() && selectedTime != null
                ) {
                    Text("Update Medicine", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}