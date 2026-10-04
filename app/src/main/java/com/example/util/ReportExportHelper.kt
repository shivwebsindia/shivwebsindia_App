package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.FinancialSummary
import com.example.data.TransactionEntity
import java.io.File
import java.io.FileOutputStream

object ReportExportHelper {

    fun exportCsvAndShare(
        context: Context,
        reportTitle: String,
        summary: FinancialSummary,
        transactions: List<TransactionEntity>,
        isExcelCompatible: Boolean = false
    ): Result<File> {
        return try {
            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
            val ext = if (isExcelCompatible) "xls.csv" else "csv"
            val sanitizedTitle = reportTitle.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val file = File(dir, "${sanitizedTitle}_${System.currentTimeMillis()}.$ext")

            val sb = StringBuilder()
            // UTF-8 BOM for Excel compatibility
            if (isExcelCompatible) {
                sb.append("\uFEFF")
            }
            sb.appendLine("SMART EXPENSE & INCOME MANAGER - FINANCIAL REPORT")
            sb.appendLine("Report Type,\"$reportTitle\"")
            sb.appendLine("Generated On,\"${DateUtils.formatDateTime(System.currentTimeMillis())}\"")
            sb.appendLine("Designed & Developed by,\"ShivWebsIndia (www.shivwebsindia.com)\"")
            sb.appendLine()
            sb.appendLine("SUMMARY METRICS,AMOUNT (INR)")
            sb.appendLine("Office Income,${summary.officeIncome}")
            sb.appendLine("Office Expense,${summary.officeExpense}")
            sb.appendLine("Office Balance,${summary.officeBalance}")
            sb.appendLine("Home Income,${summary.homeIncome}")
            sb.appendLine("Home Expense,${summary.homeExpense}")
            sb.appendLine("Home Balance,${summary.homeBalance}")
            sb.appendLine("Combined Total Income,${summary.combinedIncome}")
            sb.appendLine("Combined Total Expense,${summary.combinedExpense}")
            sb.appendLine("Combined Net Balance,${summary.combinedBalance}")
            sb.appendLine()
            sb.appendLine("DATE,SECTION,TYPE,CATEGORY,TITLE,CLIENT / PAID TO,DESCRIPTION,PAYMENT METHOD,REFERENCE NO,AMOUNT (INR),NOTES")

            transactions.forEach { tx ->
                val dateStr = DateUtils.formatIsoDate(tx.dateMillis)
                val cleanTitle = tx.title.replace("\"", "\"\"")
                val cleanClient = tx.clientOrPaidTo.replace("\"", "\"\"")
                val cleanDesc = tx.description.replace("\"", "\"\"")
                val cleanRef = tx.referenceNumber.replace("\"", "\"\"")
                val cleanNotes = tx.notes.replace("\"", "\"\"")
                sb.appendLine(
                    "$dateStr,${tx.section},${tx.type},\"${tx.category}\",\"$cleanTitle\",\"$cleanClient\",\"$cleanDesc\",\"${tx.paymentMethod}\",\"$cleanRef\",${tx.amount},\"$cleanNotes\""
                )
            }

            file.writeText(sb.toString(), Charsets.UTF_8)
            shareFile(context, file, "text/csv", "Export $reportTitle")
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportPdfAndShare(
        context: Context,
        reportTitle: String,
        dateRangeLabel: String,
        summary: FinancialSummary,
        transactions: List<TransactionEntity>
    ): Result<File> {
        return try {
            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
            val sanitizedTitle = reportTitle.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val file = File(dir, "${sanitizedTitle}_${System.currentTimeMillis()}.pdf")

            val document = createPdfDocument(reportTitle, dateRangeLabel, summary, transactions)
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            shareFile(context, file, "application/pdf", "Download / Share PDF Report")
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun printReport(
        context: Context,
        reportTitle: String,
        dateRangeLabel: String,
        summary: FinancialSummary,
        transactions: List<TransactionEntity>
    ): Result<Unit> {
        return try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                ?: return Result.failure(IllegalStateException("Print service unavailable"))

            val jobName = "SmartExpense_${reportTitle.replace(" ", "_")}"
            printManager.print(
                jobName,
                object : PrintDocumentAdapter() {
                    override fun onLayout(
                        oldAttributes: PrintAttributes?,
                        newAttributes: PrintAttributes?,
                        cancellationSignal: CancellationSignal?,
                        callback: LayoutResultCallback?,
                        extras: Bundle?
                    ) {
                        if (cancellationSignal?.isCanceled == true) {
                            callback?.onLayoutCancelled()
                            return
                        }
                        val info = PrintDocumentInfo.Builder("$jobName.pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                            .build()
                        callback?.onLayoutFinished(info, true)
                    }

                    override fun onWrite(
                        pages: Array<out PageRange>?,
                        destination: ParcelFileDescriptor?,
                        cancellationSignal: CancellationSignal?,
                        callback: WriteResultCallback?
                    ) {
                        if (destination == null) {
                            callback?.onWriteFailed("No destination file descriptor")
                            return
                        }
                        try {
                            val pdfDoc = createPdfDocument(reportTitle, dateRangeLabel, summary, transactions)
                            FileOutputStream(destination.fileDescriptor).use { out ->
                                pdfDoc.writeTo(out)
                            }
                            pdfDoc.close()
                            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                        } catch (e: Exception) {
                            callback?.onWriteFailed(e.message)
                        }
                    }
                },
                null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createPdfDocument(
        reportTitle: String,
        dateRangeLabel: String,
        summary: FinancialSummary,
        transactions: List<TransactionEntity>
    ): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val headerBgPaint = Paint().apply { color = Color.parseColor("#0A2540") }
        val orangeAccentPaint = Paint().apply { color = Color.parseColor("#FF6A00") }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF6A00")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val whiteSmallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 9.5f
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0A2540")
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0A2540")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#334155")
            textSize = 9f
        }
        val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0E7C4D")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val expensePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#8B1E1E")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val boxBgPaint = Paint().apply { color = Color.parseColor("#F5F7FA") }
        val linePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
        }

        fun drawHeaderAndFooter(c: Canvas, pNum: Int) {
            c.drawRect(0f, 0f, pageWidth.toFloat(), 78f, headerBgPaint)
            c.drawRect(0f, 75f, pageWidth.toFloat(), 78f, orangeAccentPaint)
            c.drawText("Smart Expense & Income Manager", 32f, 30f, titlePaint)
            c.drawText("$reportTitle  |  Period: $dateRangeLabel", 32f, 48f, subtitlePaint)
            c.drawText(
                "Designed & Developed by ShivWebsIndia (www.shivwebsindia.com)  •  Generated: ${DateUtils.formatDate(System.currentTimeMillis())}",
                32f,
                64f,
                whiteSmallPaint
            )

            // Footer
            c.drawLine(32f, (pageHeight - 36).toFloat(), (pageWidth - 32).toFloat(), (pageHeight - 36).toFloat(), linePaint)
            c.drawText(
                "© 2026 Smart Expense & Income Manager | Designed & Developed by ShivWebsIndia | www.shivwebsindia.com",
                32f,
                (pageHeight - 20).toFloat(),
                bodyPaint
            )
            c.drawText("Page $pNum", (pageWidth - 75).toFloat(), (pageHeight - 20).toFloat(), bodyBoldPaint)
        }

        drawHeaderAndFooter(canvas, pageNumber)

        var y = 100f
        // Draw Summary Cards
        canvas.drawRect(32f, y, (pageWidth - 32).toFloat(), y + 95f, boxBgPaint)
        canvas.drawText("EXECUTIVE FINANCIAL SUMMARY", 44f, y + 18f, headingPaint)

        // 3 Columns: Office, Home, Combined
        val col1 = 44f
        val col2 = 225f
        val col3 = 405f

        canvas.drawText("OFFICE FINANCES", col1, y + 38f, bodyBoldPaint)
        canvas.drawText("Income: ${CurrencyUtils.formatInr(summary.officeIncome)}", col1, y + 54f, incomePaint)
        canvas.drawText("Expense: ${CurrencyUtils.formatInr(summary.officeExpense)}", col1, y + 68f, expensePaint)
        canvas.drawText("Balance: ${CurrencyUtils.formatInr(summary.officeBalance)}", col1, y + 82f, bodyBoldPaint)

        canvas.drawText("HOME FINANCES", col2, y + 38f, bodyBoldPaint)
        canvas.drawText("Income: ${CurrencyUtils.formatInr(summary.homeIncome)}", col2, y + 54f, incomePaint)
        canvas.drawText("Expense: ${CurrencyUtils.formatInr(summary.homeExpense)}", col2, y + 68f, expensePaint)
        canvas.drawText("Balance: ${CurrencyUtils.formatInr(summary.homeBalance)}", col2, y + 82f, bodyBoldPaint)

        canvas.drawText("COMBINED TOTAL", col3, y + 38f, bodyBoldPaint)
        canvas.drawText("Income: ${CurrencyUtils.formatInr(summary.combinedIncome)}", col3, y + 54f, incomePaint)
        canvas.drawText("Expense: ${CurrencyUtils.formatInr(summary.combinedExpense)}", col3, y + 68f, expensePaint)
        canvas.drawText("Net Balance: ${CurrencyUtils.formatInr(summary.combinedBalance)}", col3, y + 82f, bodyBoldPaint)

        y += 116f
        canvas.drawText("TRANSACTION LEDGER (${transactions.size} Records)", 32f, y, headingPaint)
        y += 12f

        // Table Header
        canvas.drawRect(32f, y, (pageWidth - 32).toFloat(), y + 22f, headerBgPaint)
        canvas.drawText("Date", 38f, y + 15f, whiteSmallPaint)
        canvas.drawText("Sec/Type", 100f, y + 15f, whiteSmallPaint)
        canvas.drawText("Category", 165f, y + 15f, whiteSmallPaint)
        canvas.drawText("Title / Client / Paid To", 255f, y + 15f, whiteSmallPaint)
        canvas.drawText("Method", 425f, y + 15f, whiteSmallPaint)
        canvas.drawText("Amount", 495f, y + 15f, whiteSmallPaint)
        y += 28f

        transactions.forEach { tx ->
            if (y > pageHeight - 60) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                drawHeaderAndFooter(canvas, pageNumber)
                y = 100f
            }

            val dateStr = DateUtils.formatShortDate(tx.dateMillis)
            val secType = "${tx.section.take(3)}/${tx.type.take(3)}"
            val cat = tx.category.take(15)
            val desc = "${tx.title.ifEmpty { tx.description }} (${tx.clientOrPaidTo})".take(28)
            val method = tx.paymentMethod.take(10)
            val amtStr = CurrencyUtils.formatInr(tx.amount)

            canvas.drawText(dateStr, 38f, y + 10f, bodyPaint)
            canvas.drawText(secType, 100f, y + 10f, bodyBoldPaint)
            canvas.drawText(cat, 165f, y + 10f, bodyPaint)
            canvas.drawText(desc, 255f, y + 10f, bodyPaint)
            canvas.drawText(method, 425f, y + 10f, bodyPaint)
            canvas.drawText(
                amtStr,
                495f,
                y + 10f,
                if (tx.type == "INCOME") incomePaint else expensePaint
            )
            canvas.drawLine(32f, y + 15f, (pageWidth - 32).toFloat(), y + 15f, linePaint)
            y += 20f
        }

        pdfDocument.finishPage(page)
        return pdfDocument
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, file.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Ignored in headless test environments
        }
    }
}
