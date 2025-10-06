package com.example.pillbestie.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.security.MessageDigest

object ImageHashing {
    fun hashBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(byteArray)

        return hashBytes.fold("") { str, it -> str + "%02x".format(it) }
    }
}
