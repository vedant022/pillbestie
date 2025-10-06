package com.example.pillbestie.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pillbestie.AppRoutes

// Represents the items in the bottom navigation bar
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(AppRoutes.HOME, Icons.Default.Home, "Home")
    object Journal : BottomNavItem(
        AppRoutes.JOURNAL,
        Icons.Default.Add,
        "Journal"
    ) // Using Add icon as placeholder

    object Analytics : BottomNavItem(AppRoutes.ANALYTICS, Icons.Default.Analytics, "Analytics")

    object Settings : BottomNavItem(AppRoutes.SETTINGS, Icons.Default.Settings, "Settings")
    // Add other main navigation items here if needed
}
