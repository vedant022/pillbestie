package com.example.pillbestie.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Analytics", style = MaterialTheme.typography.headlineLarge)
            Text("Your 30-day summary", style = MaterialTheme.typography.bodyLarge)
        }

        item {
            AdherenceCard(analyticsData.adherence)
        }

        item {
            StreakCard(analyticsData.streak)
        }

        item {
            DoseStatsCard(analyticsData)
        }
    }
}

@Composable
fun AdherenceCard(adherence: Float) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Adherence Rate", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("%.1f%%".format(adherence), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("The percentage of doses you've taken on schedule.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Streak", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("$streak days", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("You've consistently taken your medicine for this many days in a row.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DoseStatsCard(data: AnalyticsData) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Dose Breakdown", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ListItem(
                headlineContent = { Text("Taken", fontWeight = FontWeight.Bold) },
                trailingContent = { Text("${data.takenCount}", style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Skipped", fontWeight = FontWeight.Bold) },
                trailingContent = { Text("${data.skippedCount}", style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Missed", fontWeight = FontWeight.Bold) },
                trailingContent = { Text("${data.missedCount}", style = MaterialTheme.typography.bodyLarge) }
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
