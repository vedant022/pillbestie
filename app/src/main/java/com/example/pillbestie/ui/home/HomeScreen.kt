package com.example.pillbestie.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.AppRoutes
import com.example.pillbestie.data.*
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val medicines by viewModel.medicines.collectAsState()
    val takenAction by viewModel.takenAction.collectAsState()
    val greeting by viewModel.greeting.collectAsState()
    val affirmation by viewModel.affirmation.collectAsState()

    affirmation?.let {
        AlertDialog(
            onDismissRequest = { viewModel.clearAffirmation() },
            title = { Text("Pill Bestie says...") },
            text = { Text(it, style = MaterialTheme.typography.headlineMedium) },
            confirmButton = {
                Button(onClick = { viewModel.clearAffirmation() }) {
                    Text("OK")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(greeting)
        }
        item {
            ActionButtons(navController)
        }
        item {
            Text("Today's Pills", style = MaterialTheme.typography.headlineMedium)
        }
        items(medicines) { medicineData ->
            MedicineCard(
                medicineData = medicineData,
                takenAction = takenAction,
                onMarkAsTaken = { viewModel.markDoseAsTaken(medicineData.medicine) },
                onScanPill = { navController.navigate("${AppRoutes.SCAN_PILL}/${medicineData.medicine.id}") }
            )
        }
    }
}

@Composable
fun Header(greeting: String) {
    Text(greeting, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun ActionButtons(navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionButton(
            text = "Add Meds",
            icon = Icons.Default.Add,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.ADD_MEDICINE) }
        )
        ActionButton(
            text = "Voice Chat",
            icon = Icons.Default.Mic,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.VOICE_CHAT) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(Modifier.height(8.dp))
            Text(text, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun MedicineCard(
    medicineData: MedicineUIData,
    takenAction: TakenAction,
    onMarkAsTaken: () -> Unit,
    onScanPill: () -> Unit
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val time = timeFormat.format(Date(medicineData.medicine.timeInMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(medicineData.medicine.name, style = MaterialTheme.typography.titleLarge) },
            supportingContent = { Text("${medicineData.medicine.dosage} - $time", style = MaterialTheme.typography.bodyLarge) },
            trailingContent = {
                if (medicineData.isTakenToday) {
                    Text("✓ Taken", color = MaterialTheme.colorScheme.primary)
                } else {
                    when (takenAction) {
                        TakenAction.QUICK_TAP -> {
                            Button(
                                onClick = onMarkAsTaken,
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Mark as taken")
                                Spacer(Modifier.width(4.dp))
                                Text("Taken")
                            }
                        }
                        TakenAction.PHOTO_MODE -> {
                            IconButton(onClick = onScanPill) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Scan pill", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PillBestieTheme {
        HomeScreen(rememberNavController())
    }
}
