package com.example.pillbestie.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val analyticsData by viewModel.analyticsData.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Medication Analytics", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            AdherenceChart()
        }
        item {
            KeyMetrics(analyticsData)
        }
    }
}

@Composable
fun AdherenceChart() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Adherence", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            // Placeholder for a chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Adherence chart would be here.")
            }
        }
    }
}

@Composable
fun KeyMetrics(data: AnalyticsData) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Key Metrics", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ListItem(
                headlineContent = { Text("Doses Taken") },
                trailingContent = { Text("${data.takenDoses}/${data.totalDoses}", style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Adherence") },
                trailingContent = { Text("%.1f%%".format(data.adherence), style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Current Streak") },
                trailingContent = { Text("${data.streak} days", style = MaterialTheme.typography.bodyLarge) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    PillBestieTheme {
        AnalyticsScreen()
    }
}
