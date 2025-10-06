package com.example.pillbestie.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillbestie.data.Personality
import com.example.pillbestie.ui.ViewModelFactory
import com.example.pillbestie.ui.theme.PillBestieTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val personality by viewModel.personality.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val reminderFrequency by viewModel.reminderFrequency.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineMedium) },
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
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfileSection(profileName, viewModel::setProfileName) }
            item { PersonalitySection(personality, viewModel::setPersonality) }
            item { NotificationSettingsSection(reminderFrequency, viewModel::setReminderFrequency, vibrationEnabled, viewModel::setVibrationEnabled) }
        }
    }
}

@Composable
fun ProfileSection(name: String, onNameChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun PersonalitySection(selectedPersonality: Personality, onPersonalityChange: (Personality) -> Unit) {
    val personalities = listOf(
        "💖 Caring" to Personality.CARING,
        "😏 Sarcastic" to Personality.SARCASTIC,
        "👑 Chaotic" to Personality.CHAOTIC
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bestie Personality", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Column(Modifier.selectableGroup()) {
                personalities.forEach { (name, personality) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (personality == selectedPersonality),
                                onClick = { onPersonalityChange(personality) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (personality == selectedPersonality),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsSection(
    reminderFrequency: Int,
    onReminderFrequencyChange: (Int) -> Unit,
    vibrationEnabled: Boolean,
    onVibrationEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ListItem(
                headlineContent = { Text("Reminder Frequency") },
                supportingContent = { Text("Get up to $reminderFrequency extra reminders if you miss a dose") },
                trailingContent = {
                    Text(reminderFrequency.toString(), style = MaterialTheme.typography.headlineSmall)
                }
            )
            Slider(
                value = reminderFrequency.toFloat(),
                onValueChange = { onReminderFrequencyChange(it.roundToInt()) },
                valueRange = 1f..3f,
                steps = 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ListItem(
                headlineContent = { Text("Vibration") },
                trailingContent = {
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = onVibrationEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PillBestieTheme {
        SettingsScreen(rememberNavController())
    }
}
