package com.example.pillbestie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.AppRoutes
import com.example.pillbestie.ui.theme.PillBestieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hey there, bestie! 💊") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        // In a real app, you would have a BottomNavigationBar here
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { BestieModeSelector() }
            item { TodayProgressCard() }
            item { ActionButtons(navController) }
            item { YourMedicinesHeader() }
            // Medicine items would be added here, e.g., items(medicines) { ... }
        }
    }
}

@Composable
fun BestieModeSelector() {
    var selectedMode by remember { mutableStateOf("Caring") }
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Choose Your Bestie Mode", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // These would be more stylized in the final version
                Button(onClick = { selectedMode = "Caring" }) { Text("💖\nCaring") }
                Button(onClick = { selectedMode = "Sarcastic" }) { Text("😏\nSarcastic") }
                Button(onClick = { selectedMode = "Chaotic" }) { Text("👑\nChaotic") }
            }
        }
    }
}

@Composable
fun TodayProgressCard() {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProgressItem("1", "Doses Taken")
            ProgressItem("50%", "Adherence")
            ProgressItem("1", "Medicines")
        }
    }
}

@Composable
fun ProgressItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ActionButtons(navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionButton(
            text = "Add\nMedicine",
            icon = Icons.Default.Add,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.ADD_MEDICINE) }
        )
        ActionButton(
            text = "Scan\nPill",
            icon = Icons.Default.CameraAlt,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.SCAN_PILL) }
        )
        ActionButton(
            text = "Voice\nChat",
            icon = Icons.Default.Mic,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppRoutes.VOICE_CHAT) }
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(Modifier.height(4.dp))
            Text(text, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun YourMedicinesHeader() {
    Text(
        text = "Your Medicines",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PillBestieTheme {
        HomeScreen(navController = rememberNavController())
    }
}