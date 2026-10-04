package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ApkExportHelper {
    /**
     * Extracts the currently installed APK from the Android runtime (applicationInfo.sourceDir)
     * into a clean "Smart_Expense_Income_Manager.apk" file and triggers the Android Share / Save dialog.
     */
    fun shareInstalledApk(context: Context): Result<File> {
        return try {
            val sourceApk = File(context.applicationInfo.sourceDir)
            val exportDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val targetApk = File(exportDir, "Smart_Expense_Income_Manager.apk")
            sourceApk.copyTo(targetApk, overwrite = true)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetApk
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Smart Expense & Income Manager APK")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Smart Expense & Income Manager (Designed & Developed by ShivWebsIndia - www.shivwebsindia.com)"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Save or Share APK File").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            Result.success(targetApk)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
