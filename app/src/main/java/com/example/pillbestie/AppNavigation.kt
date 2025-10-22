package com.example.pillbestie

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pillbestie.ui.analytics.AnalyticsScreen
import com.example.pillbestie.ui.home.HomeScreen
import com.example.pillbestie.ui.journal.AddEditJournalScreen
import com.example.pillbestie.ui.journal.JournalScreen
import com.example.pillbestie.ui.medicine.AddMedicineScreen
import com.example.pillbestie.ui.medicine.EditMedicineScreen
import com.example.pillbestie.ui.medicine.MedicineDetailScreen
import com.example.pillbestie.ui.scan.ScanPillScreen
import com.example.pillbestie.ui.settings.SettingsScreen
import com.example.pillbestie.ui.voice.VoiceChatScreen

object AppRoutes {
    const val HOME = "home"
    const val ADD_MEDICINE = "add_medicine"
    const val SCAN_PILL = "scan_pill"
    const val VOICE_CHAT = "voice_chat"
    const val JOURNAL = "journal"
    const val ADD_EDIT_JOURNAL = "add_edit_journal"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
    const val MEDICINE_DETAIL = "medicine_detail"
    const val EDIT_MEDICINE = "edit_medicine"
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = AppRoutes.HOME, modifier = modifier) {
        composable(AppRoutes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(AppRoutes.ADD_MEDICINE) {
            AddMedicineScreen(navController = navController)
        }
        composable(
            route = "${AppRoutes.SCAN_PILL}/{medicineId}",
            arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
            ScanPillScreen(navController = navController, medicineId = medicineId)
        }
        composable(AppRoutes.VOICE_CHAT) {
            VoiceChatScreen(navController = navController)
        }
        composable(AppRoutes.JOURNAL) {
            JournalScreen(navController = navController)
        }
        composable(AppRoutes.ADD_EDIT_JOURNAL) {
            AddEditJournalScreen(navController = navController)
        }
        composable(AppRoutes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(AppRoutes.ANALYTICS) {
            AnalyticsScreen()
        }
        composable(
            route = "${AppRoutes.MEDICINE_DETAIL}/{medicineId}",
            arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
            MedicineDetailScreen(navController = navController, medicineId = medicineId)
        }
        composable(
            route = "${AppRoutes.EDIT_MEDICINE}/{medicineId}",
            arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
            EditMedicineScreen(navController = navController, medicineId = medicineId)
        }
    }
}
