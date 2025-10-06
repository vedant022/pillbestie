package com.example.pillbestie.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PillBestieShapes = Shapes(
    small = RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(16.dp),
    large = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)
)
