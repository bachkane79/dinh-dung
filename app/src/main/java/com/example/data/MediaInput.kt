package com.example.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

data class MediaInput(val resolver: ContentResolver, val uri: Uri) {
    fun fileName(): String = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { if (it.moveToFirst()) it.getString(0) else null } ?: "Tệp đã chọn"
}
