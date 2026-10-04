package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.CategoryEntity
import com.example.data.FinanceConstants
import com.example.data.TransactionEntity
import com.example.data.TransactionFormPreset
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionFormDialog(
    preset: TransactionFormPreset,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (
        existingId: Long,
        section: String,
        type: String,
        dateMillis: Long,
        category: String,
        title: String,
        clientOrPaidTo: String,
        description: String,
        amount: Double,
        paymentMethod: String,
        referenceNumber: String,
        notes: String,
        attachmentUri: String?,
        attachmentName: String?
    ) -> Unit
) {
    val existing = preset.existingTransaction
    var section by remember(preset) { mutableStateOf(existing?.section ?: preset.section) }
    var type by remember(preset) { mutableStateOf(existing?.type ?: preset.type) }
    var dateMillis by remember(preset) {
        mutableLongStateOf(existing?.dateMillis ?: System.currentTimeMillis())
    }

    val availableCategories = remember(section, type, categories) {
        val list = categories.filter { it.section == section && it.type == type }.map { it.name }
        if (list.isNotEmpty()) list else {
            when {
                section == FinanceConstants.SECTION_OFFICE && type == FinanceConstants.TYPE_EXPENSE ->
                    FinanceConstants.DEFAULT_OFFICE_EXPENSE_CATEGORIES
                section == FinanceConstants.SECTION_OFFICE && type == FinanceConstants.TYPE_INCOME ->
                    FinanceConstants.DEFAULT_OFFICE_INCOME_SOURCES
                section == FinanceConstants.SECTION_HOME && type == FinanceConstants.TYPE_EXPENSE ->
                    FinanceConstants.DEFAULT_HOME_EXPENSE_CATEGORIES
                else -> FinanceConstants.DEFAULT_HOME_INCOME_SOURCES
            }
        }
    }

    var selectedCategory by remember(section, type, availableCategories) {
        mutableStateOf(
            existing?.category?.takeIf { availableCategories.contains(it) }
                ?: availableCategories.firstOrNull().orEmpty()
        )
    }

    var title by remember(preset) { mutableStateOf(existing?.title.orEmpty()) }
    var clientOrPaidTo by remember(preset) { mutableStateOf(existing?.clientOrPaidTo.orEmpty()) }
    var description by remember(preset) { mutableStateOf(existing?.description.orEmpty()) }
    var amountText by remember(preset) {
        mutableStateOf(if (existing != null) existing.amount.toLong().toString() else "")
    }
    var paymentMethod by remember(preset) {
        mutableStateOf(existing?.paymentMethod ?: FinanceConstants.PAYMENT_METHODS.first())
    }
    var referenceNumber by remember(preset) { mutableStateOf(existing?.referenceNumber.orEmpty()) }
    var notes by remember(preset) { mutableStateOf(existing?.notes.orEmpty()) }
    var attachmentUri by remember(preset) { mutableStateOf(existing?.attachmentUri) }
    var attachmentName by remember(preset) { mutableStateOf(existing?.attachmentName) }
    var validationError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    // Zero-permission Photo Picker for JPG/PNG receipts
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachmentUri = uri.toString()
            attachmentName = "Receipt_Image_${System.currentTimeMillis() % 10000}.jpg"
        }
    }

    // Document picker for PDF receipts
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachmentUri = uri.toString()
            attachmentName = "Receipt_Document_${System.currentTimeMillis() % 10000}.pdf"
        }
    }

    val isOffice = section == FinanceConstants.SECTION_OFFICE
    val isIncome = type == FinanceConstants.TYPE_INCOME
    val headerTitle = buildString {
        append(if (existing == null) "Add " else "Edit ")
        append(if (isOffice) "Office " else "Home ")
        append(if (isIncome) "Income" else "Expense")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isIncome) BrandDarkBlue else BrandDarkRed)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                            Text(
                                text = "Fast entry (< 30 seconds) • Auto recalculates dashboard",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Form Scroll Body
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section & Type Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = section == FinanceConstants.SECTION_OFFICE,
                            onClick = { section = FinanceConstants.SECTION_OFFICE },
                            label = { Text("Office") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = section == FinanceConstants.SECTION_HOME,
                            onClick = { section = FinanceConstants.SECTION_HOME },
                            label = { Text("Home") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = type == FinanceConstants.TYPE_INCOME,
                            onClick = { type = FinanceConstants.TYPE_INCOME },
                            label = { Text("Income") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = type == FinanceConstants.TYPE_EXPENSE,
                            onClick = { type = FinanceConstants.TYPE_EXPENSE },
                            label = { Text("Expense") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Date & Amount Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select Date",
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = DateUtils.formatDate(dateMillis),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it.filter { ch -> ch.isDigit() || ch == '.' }
                                validationError = null
                            },
                            label = { Text("Amount (₹) *") },
                            placeholder = { Text("e.g. 5000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_tx_amount")
                        )
                    }

                    // Category / Income Source Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { selectedCategory = it },
                            readOnly = true,
                            label = {
                                Text(
                                    if (isIncome) "Income Source *" else "Expense Category *"
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("dropdown_tx_category")
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            availableCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Expense Title / Income Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                if (isIncome) "Income Title / Project Name" else "Expense Title *"
                            )
                        },
                        placeholder = {
                            Text(
                                if (isOffice && !isIncome) "e.g. Google Ads Campaign / Office Suite Rent"
                                else if (isOffice) "e.g. Website Design Milestone"
                                else "e.g. Weekly Supermarket Grocery"
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tx_title")
                    )

                    // Client Name (Office Income) or Paid To (Expense)
                    if (isOffice && isIncome) {
                        OutlinedTextField(
                            value = clientOrPaidTo,
                            onValueChange = { clientOrPaidTo = it },
                            label = { Text("Client Name") },
                            placeholder = { Text("e.g. Apex Global Tech Pvt Ltd") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tx_client")
                        )
                    } else if (!isIncome) {
                        OutlinedTextField(
                            value = clientOrPaidTo,
                            onValueChange = { clientOrPaidTo = it },
                            label = { Text("Paid To (Vendor / Payee)") },
                            placeholder = { Text("e.g. Google India / Reliance Smart") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tx_paid_to")
                        )
                    }

                    // Payment Method Chips
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FinanceConstants.PAYMENT_METHODS.forEach { method ->
                            val selected = paymentMethod == method
                            FilterChip(
                                selected = selected,
                                onClick = { paymentMethod = method },
                                label = { Text(method) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Brief transaction description") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tx_description")
                    )

                    // Reference Number / Bill/Invoice Number
                    if (isOffice || !isIncome) {
                        OutlinedTextField(
                            value = referenceNumber,
                            onValueChange = { referenceNumber = it },
                            label = {
                                Text(
                                    if (isIncome) "Reference Number / UTR" else "Bill / Invoice Number"
                                )
                            },
                            placeholder = { Text("e.g. INV-2026-104 or UTR998123") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tx_reference")
                        )
                    }

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("Additional remarks or tax notes") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tx_notes")
                    )

                    // Receipt / Attachment Upload (JPG, PNG, PDF)
                    if (!isIncome || isOffice) {
                        Text(
                            text = "Receipt / Invoice Attachment (JPG, PNG, PDF)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Attach JPG/PNG",
                                    tint = BrandOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("JPG / PNG Photo", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { pdfPickerLauncher.launch("application/pdf") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Attach PDF",
                                    tint = BrandDarkRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Attach PDF", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        if (!attachmentName.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrandOrange.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Attached",
                                            tint = IncomeGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = attachmentName ?: "Attached Receipt",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    TextButton(
                                        onClick = {
                                            attachmentUri = null
                                            attachmentName = null
                                        }
                                    ) {
                                        Text("Remove", color = BrandDarkRed)
                                    }
                                }
                            }
                        }
                    }

                    if (validationError != null) {
                        Text(
                            text = validationError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandDarkRed
                        )
                    }
                }

                HorizontalDivider()

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.trim().toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                validationError = "Please enter a valid positive financial amount in ₹."
                                return@Button
                            }
                            if (selectedCategory.isBlank()) {
                                validationError = "Please select a category or source."
                                return@Button
                            }
                            onSave(
                                existing?.id ?: 0L,
                                section,
                                type,
                                dateMillis,
                                selectedCategory,
                                title.ifBlank { selectedCategory },
                                clientOrPaidTo,
                                description,
                                amt,
                                paymentMethod,
                                referenceNumber,
                                notes,
                                attachmentUri,
                                attachmentName
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_save_transaction")
                    ) {
                        Text(
                            text = if (existing == null) "Save Transaction" else "Update Transaction",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { dateMillis = it }
                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
fun TransactionDetailsDialog(
    tx: TransactionEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = tx.type == FinanceConstants.TYPE_INCOME
    val accent = if (isIncome) IncomeGreen else BrandDarkRed

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrandDarkBlue
                    ) {
                        Text(
                            text = "${tx.section} ${tx.type}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = DateUtils.formatDate(tx.dateMillis),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tx.title.ifEmpty { tx.category },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accent.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "TRANSACTION AMOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent
                        )
                        Text(
                            text = CurrencyUtils.formatInr(tx.amount),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = accent
                        )
                    }
                }

                DetailRow(label = "Category / Source", value = tx.category)
                DetailRow(label = "Payment Method", value = tx.paymentMethod)
                if (tx.clientOrPaidTo.isNotBlank()) {
                    DetailRow(
                        label = if (isIncome) "Client / Received From" else "Paid To",
                        value = tx.clientOrPaidTo
                    )
                }
                if (tx.referenceNumber.isNotBlank()) {
                    DetailRow(label = "Reference / Invoice No.", value = tx.referenceNumber)
                }
                if (tx.description.isNotBlank()) {
                    DetailRow(label = "Description", value = tx.description)
                }
                if (tx.notes.isNotBlank()) {
                    DetailRow(label = "Notes", value = tx.notes)
                }

                if (!tx.attachmentUri.isNullOrBlank()) {
                    HorizontalDivider()
                    Text(
                        text = "Attached Receipt / Document",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandOrange
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Receipt",
                                    tint = BrandOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tx.attachmentName ?: "Receipt Attachment",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            if (tx.attachmentName?.endsWith(".pdf", ignoreCase = true) != true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = tx.attachmentUri,
                                    contentDescription = "Receipt Image Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDarkRed)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = BrandDarkRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Confirm Delete Transaction",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        },
        text = {
            Text(
                text = "Are you sure you want to permanently delete '${transaction.title}' (${CurrencyUtils.formatInr(transaction.amount)}) from ${transaction.section} ${transaction.type}? All dashboard balances and reports will recalculate immediately."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = BrandDarkRed),
                modifier = Modifier.testTag("btn_confirm_delete")
            ) {
                Text("Yes, Delete Record", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
