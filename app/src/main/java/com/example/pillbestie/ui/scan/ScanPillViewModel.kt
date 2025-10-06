package com.example.pillbestie.ui.scan

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillbestie.data.DoseLog
import com.example.pillbestie.data.Injection
import com.example.pillbestie.utils.ImageHashing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScanPillViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideMedicineRepository(application)
    private val _uiState = MutableStateFlow(ScanPillState())
    val uiState: StateFlow<ScanPillState> = _uiState

    fun onImageCaptured(bitmap: Bitmap, medicineId: Int) {
        viewModelScope.launch {
            _uiState.value = ScanPillState(isScanning = true)

            val hash = ImageHashing.hashBitmap(bitmap)
            val isUnique = repository.isImageHashUnique(hash)

            if (isUnique) {
                val doseLog = DoseLog(
                    medicineId = medicineId,
                    scheduledTime = System.currentTimeMillis(), // Or get the actual scheduled time
                    takenTime = System.currentTimeMillis(),
                    wasMissed = false,
                    imageHash = hash
                )
                repository.insert(doseLog)
                _uiState.value = ScanPillState(scanSuccess = true)
            } else {
                _uiState.value = ScanPillState(error = "This image has already been used to log a dose.")
            }
        }
    }
}
