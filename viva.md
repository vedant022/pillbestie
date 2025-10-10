
# PillBestie Project: Viva Questions Study Guide

This document provides a detailed breakdown of the PillBestie application, focusing on its architecture, key technologies, and the role of each significant file.

## 1. Project Overview & Architecture

**Q: What is the core purpose of the PillBestie application?**

PillBestie is a modern Android application designed as a personal medication management assistant. Its primary goal is to help users track their medications, log when they take them, and receive timely notifications to ensure they adhere to their medication schedule.

**Q: What is the architecture of the PillBestie project?**

The project follows a modern Android architecture, specifically **MVVM (Model-View-ViewModel)**, and is built entirely with **Jetpack Compose** for the UI.

*   **UI Layer (View):** The entire user interface is built with Jetpack Compose. The UI is composed of multiple Composable screens, managed by `navigation-compose`.
*   **ViewModel Layer:** The project uses `ViewModel`s to hold and manage UI-related data. These ViewModels interact with the Data layer to fetch and save information, providing it to the UI in an observable way.
*   **Data Layer:** The data layer is implemented using **Room**, a local database persistence library. The app stores medication and journal entry data locally on the device.
*   **Dependency Management:** The project uses the **Gradle Version Catalog** (`libs.versions.toml`) for organized and consistent dependency management.

## 2. Key Technologies & Libraries

*   **Kotlin:** The application is written entirely in Kotlin.
*   **Jetpack Compose:** The UI is built with Jetpack Compose.
*   **Room Database:** For local data persistence.
*   **Navigation Compose:** For handling navigation between screens.
*   **Coroutines & Flow:** For managing background tasks and asynchronous data streams.
*   **Coil:** For loading and displaying images asynchronously.
*   **Accompanist Permissions:** To simplify requesting runtime permissions.
*   **WorkManager:** The `work-runtime-ktx` dependency suggests the use of WorkManager for deferrable, guaranteed background work, such as analyzing missed doses.

## 3. File-by-File Breakdown

### Core Application & Setup

*   `PillBestieApplication.kt`: The entry point of the application. It initializes the Room database and the `MedicineRepository`, ensuring a single instance is shared across the app. This is a best practice for managing global resources.
*   `MainActivity.kt`: The main and only `Activity` in the app. It sets up the Jetpack Compose content and hosts the `AppNavigation` composable, which manages all the screens.
*   `AppNavigation.kt`: The heart of the navigation system. It defines the `NavHost` and all the possible navigation routes (e.g., to the home screen, settings, analytics, etc.). It is responsible for orchestrating the flow between different screens.

### Data Layer (Room Database)

*   `AppDatabase.kt`: The Room database class. It defines the database configuration, lists all the `Entity` classes (like `Medicine`, `JournalEntry`, etc.), and provides access to the DAOs.
*   `Medicine.kt`, `JournalEntry.kt`, `DoseLog.kt`, `Injection.kt`: These are the **Entity** classes. Each class defines a table in the Room database, with properties corresponding to the columns of the table.
*   `MedicineDao.kt`, `JournalEntryDao.kt`, `DoseLogDao.kt`: These are the **Data Access Objects (DAOs)**. They define the SQL queries for interacting with the database tables (e.g., `INSERT`, `UPDATE`, `DELETE`, `SELECT`).
*   `MedicineRepository.kt`: A repository that provides a clean API for the rest of the app to interact with the data layer. It abstracts the data source (the DAOs) from the ViewModels.

### UI Layer (Screens & ViewModels)

The project follows a consistent pattern for each feature screen:

*   **`HomeScreen.kt` & `HomeViewModel.kt`**:
    *   `HomeScreen.kt`: The main landing screen of the app. It likely displays a list of the user's current medications and their schedule.
    *   `HomeViewModel.kt`: Fetches the medication list from the `MedicineRepository` and exposes it to the `HomeScreen`.
*   **`AnalyticsScreen.kt` & `AnalyticsViewModel.kt`**:
    *   `AnalyticsScreen.kt`: Displays medication adherence statistics, possibly with charts or graphs.
    *   `AnalyticsViewModel.kt`: Gathers and processes data from the repository to generate the analytics.
*   **`JournalScreen.kt` & `JournalViewModel.kt`**:
    *   `JournalScreen.kt`: Displays a list of the user's journal entries.
    *   `JournalViewModel.kt`: Fetches the journal entries from the repository.
*   **`AddMedicineScreen.kt` & `AddMedicineViewModel.kt`**:
    *   `AddMedicineScreen.kt`: A form for adding or editing a medication.
    *   `AddMedicineViewModel.kt`: Handles the business logic for saving a new medication to the repository.
*   **`ScanPillScreen.kt` & `ScanPillViewModel.kt`**:
    *   `ScanPillScreen.kt`: The UI for the pill scanning feature. It likely shows a camera preview.
    *   `ScanPillViewModel.kt`: Manages the state of the scanning process.
*   **`SettingsScreen.kt` & `SettingsViewModel.kt`**:
    *   `SettingsScreen.kt`: A screen for user-configurable settings.
    *   `SettingsViewModel.kt`: Manages the app's settings, likely using `DataStore` via the `SettingsRepository`.
*   **`VoiceChatScreen.kt` & `VoiceChatViewModel.kt`**:
    *   `VoiceChatScreen.kt`: UI for a voice-based interaction feature.
    *   `VoiceChatViewModel.kt`: Manages the state and logic for the voice chat.

### Notification System

*   `NotificationScheduler.kt`: A key class responsible for scheduling and canceling medication reminders using the Android `AlarmManager`.
*   `BootReceiver.kt`: A `BroadcastReceiver` that listens for the `BOOT_COMPLETED` system event. Its purpose is to re-schedule all active alarms if the device is restarted, ensuring that reminders are not lost.
*   `Notification.kt`: A `BroadcastReceiver` that is triggered by the `AlarmManager`. It is responsible for building and displaying the actual notification to the user.

### Background Services

*   `MissedDoseAnalysisWorker.kt`: A `WorkManager` worker. This is used for reliable, deferrable background tasks. It's likely used to periodically check if the user has missed any doses and, if so, to log this or trigger a follow-up notification.

This detailed breakdown should provide a solid foundation for your viva. Good luck!
