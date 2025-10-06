package com.example.pillbestie.ui.scan

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPillScreen(navController: NavController, medicineId: Int, viewModel: ScanPillViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.onImageCaptured(it, medicineId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Pill") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isScanning) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Scanning...")
            } else if (uiState.scanSuccess) {
                Text("Dose logged successfully!")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Done")
                }
            } else {
                Button(onClick = { cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan Pill")
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Pill")
                }
                uiState.error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanPillScreenPreview() {
    PillBestieTheme {
        ScanPillScreen(rememberNavController(), 1)
    }
}
