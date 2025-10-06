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
import com.example.pillbestie.data.Injection
import com.example.pillbestie.data.Medicine
import com.example.pillbestie.data.Personality
import com.example.pillbestie.data.TakenAction
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
    val personality by viewModel.personality.collectAsState()
    val profileName by viewModel.profileName.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(personality, profileName)
        }
        item {
            ActionButtons(navController)
        }
        item {
            Text("Today's Pills", style = MaterialTheme.typography.headlineMedium)
        }
        items(medicines) { medicine ->
            MedicineCard(
                medicine = medicine,
                takenAction = takenAction,
                onMarkAsTaken = { viewModel.markDoseAsTaken(medicine) },
                onScanPill = { navController.navigate("${AppRoutes.SCAN_PILL}/${medicine.id}") }
            )
        }
    }
}

@Composable
fun Header(personality: Personality, name: String) {
    val greeting = when (personality) {
        Personality.CARING -> if (name.isNotBlank()) "Hey, $name! ✨" else "Hey, bestie! ✨"
        Personality.SARCASTIC -> if (name.isNotBlank()) "Oh, it's $name. Try not to forget your pills..." else "Oh, it's you. Try not to forget your pills..."
        Personality.CHAOTIC -> if (name.isNotBlank()) "Alright, $name, let's do this! PILL TIME!" else "Alright, bestie, let's do this! PILL TIME!"
    }
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
            text = "Scan Pill",
            icon = Icons.Default.PhotoCamera,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.SCAN_PILL) }
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
    medicine: Medicine,
    takenAction: TakenAction,
    onMarkAsTaken: () -> Unit,
    onScanPill: () -> Unit
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val time = timeFormat.format(Date(medicine.timeInMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(medicine.name, style = MaterialTheme.typography.titleLarge) },
            supportingContent = { Text("${medicine.dosage} - $time", style = MaterialTheme.typography.bodyLarge) },
            trailingContent = {
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
