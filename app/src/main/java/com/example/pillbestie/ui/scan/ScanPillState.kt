package com.example.pillbestie.ui.scan

data class ScanPillState(
    val isScanning: Boolean = false,
    val scanSuccess: Boolean = false,
    val error: String? = null
)
