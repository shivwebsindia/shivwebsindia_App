package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.ReportType
import com.example.data.SortOption
import com.example.data.TransactionEntity
import com.example.ui.components.DateFilterRow
import com.example.ui.components.ExpenseBreakdownDonutCard
import com.example.ui.components.IncomeVsExpenseBarChart
import com.example.ui.components.NineSummaryCardsGrid
import com.example.ui.components.SectionTopSummaryStrip
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateFilterOption
import com.example.util.DateUtils
import com.example.util.ReportExportHelper
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllTransactionsScreen(
    viewModel: FinanceViewModel,
    filteredTransactions: List<TransactionEntity>,
    searchQuery: String,
    filterSection: String,
    filterType: String,
    filterCategory: String,
    filterPaymentMethod: String,
    filterDateOption: DateFilterOption,
    minAmount: String,
    maxAmount: String,
    sortOption: SortOption
) {
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val summary = remember(filteredTransactions) {
        FinancialSummary.fromTransactions(filteredTransactions)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("all_transactions_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Summary Strip
        item {
            SectionTopSummaryStrip(
                title = "Central Transaction Management",
                subtitle = "Search, filter, sort, add, edit, duplicate & manage all Office & Home records",
                income = summary.combinedIncome,
                expense = summary.combinedExpense,
                balance = summary.combinedBalance
            )
        }

        // Global Search Bar + Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = { Text("Search Description, Client, Paid To, Category, Ref, Amount") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_transactions_input")
                )
                Button(
                    onClick = {
                        viewModel.openTransactionModal(
                            FinanceConstants.SECTION_OFFICE,
                            FinanceConstants.TYPE_EXPENSE
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Filter Chips (Section & Type) + Advanced Filter Toggle
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("ALL" to "All Sections", "OFFICE" to "Office", "HOME" to "Home").forEach { (key, label) ->
                        FilterChip(
                            selected = filterSection == key,
                            onClick = { viewModel.setFilterSection(key) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    listOf("ALL" to "All Types", "INCOME" to "Income", "EXPENSE" to "Expense").forEach { (key, label) ->
                        FilterChip(
                            selected = filterType == key,
                            onClick = { viewModel.setFilterType(key) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    OutlinedButton(
                        onClick = { showAdvancedFilters = !showAdvancedFilters }
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAdvancedFilters) "Hide Filters" else "More Filters & Sort")
                    }
                }

                if (showAdvancedFilters) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Sort Order",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SortOption.entries.forEach { s ->
                                    FilterChip(
                                        selected = sortOption == s,
                                        onClick = { viewModel.setSortOption(s) },
                                        label = { Text(s.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandOrange,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Payment Method Filter",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (listOf("ALL") + FinanceConstants.PAYMENT_METHODS).forEach { pm ->
                                    FilterChip(
                                        selected = filterPaymentMethod == pm,
                                        onClick = { viewModel.setFilterPaymentMethod(pm) },
                                        label = { Text(if (pm == "ALL") "All Methods" else pm) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandDarkBlue,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Date Period Filter",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            DateFilterRow(
                                selectedOption = filterDateOption,
                                onSelectOption = { viewModel.setFilterDateOption(it) },
                                onOpenCustomPicker = { viewModel.setFilterDateOption(DateFilterOption.ALL_TIME) }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = minAmount,
                                    onValueChange = { viewModel.setMinAmountFilter(it) },
                                    label = { Text("Min Amount (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = maxAmount,
                                    onValueChange = { viewModel.setMaxAmountFilter(it) },
                                    label = { Text("Max Amount (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.resetAllFilters() }) {
                                    Text("Reset", color = BrandDarkRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Matching Transactions (${filteredTransactions.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "Sorted by: ${sortOption.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(filteredTransactions, key = { it.id }) { tx ->
            TransactionItemCard(
                tx = tx,
                onView = { viewModel.setViewingTransaction(tx) },
                onEdit = { viewModel.openTransactionModal(tx.section, tx.type, tx) },
                onDelete = { viewModel.requestDeleteTransaction(tx) },
                onDuplicate = { viewModel.duplicateTransaction(tx) }
            )
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportsScreen(
    viewModel: FinanceViewModel,
    selectedReportType: ReportType,
    reportDateFilter: DateFilterOption,
    allTransactions: List<TransactionEntity>
) {
    val context = LocalContext.current

    val periodFilteredTransactions = remember(allTransactions, selectedReportType, reportDateFilter) {
        val effectiveFilter = when (selectedReportType) {
            ReportType.DAILY -> DateFilterOption.TODAY
            ReportType.WEEKLY -> DateFilterOption.THIS_WEEK
            ReportType.MONTHLY -> DateFilterOption.THIS_MONTH
            else -> reportDateFilter
        }
        val (minD, maxD) = DateUtils.getRangeForFilter(effectiveFilter)
        val inDate = allTransactions.filter { it.dateMillis in minD..maxD }
        when (selectedReportType) {
            ReportType.OFFICE -> inDate.filter { it.section == FinanceConstants.SECTION_OFFICE }
            ReportType.HOME -> inDate.filter { it.section == FinanceConstants.SECTION_HOME }
            ReportType.INCOME -> inDate.filter { it.type == FinanceConstants.TYPE_INCOME }
            ReportType.EXPENSE -> inDate.filter { it.type == FinanceConstants.TYPE_EXPENSE }
            else -> inDate
        }
    }

    val reportSummary = remember(periodFilteredTransactions) {
        FinancialSummary.fromTransactions(periodFilteredTransactions)
    }

    val categoryBreakdown = remember(periodFilteredTransactions) {
        viewModel.getCategoryBreakdown(
            periodFilteredTransactions,
            null,
            if (selectedReportType == ReportType.INCOME) FinanceConstants.TYPE_INCOME else FinanceConstants.TYPE_EXPENSE
        )
    }

    val paymentBreakdown = remember(periodFilteredTransactions) {
        val total = periodFilteredTransactions.sumOf { it.amount }.coerceAtLeast(1.0)
        periodFilteredTransactions.groupBy { it.paymentMethod }.map { (pm, list) ->
            Triple(pm, list.sumOf { it.amount }, ((list.sumOf { it.amount } / total) * 100.0).toFloat())
        }.sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("reports_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Executive Report Export Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Financial Intelligence & Reports Center",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Text(
                        text = "${selectedReportType.label}: ${selectedReportType.subtitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Export Actions: Print, PDF, Excel, CSV
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                ReportExportHelper.exportPdfAndShare(
                                    context = context,
                                    reportTitle = selectedReportType.label,
                                    dateRangeLabel = reportDateFilter.label,
                                    summary = reportSummary,
                                    transactions = periodFilteredTransactions
                                ).onSuccess {
                                    viewModel.showToast("PDF Report ready: ${it.name}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_export_pdf")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download PDF", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                ReportExportHelper.exportCsvAndShare(
                                    context = context,
                                    reportTitle = selectedReportType.label,
                                    summary = reportSummary,
                                    transactions = periodFilteredTransactions,
                                    isExcelCompatible = true
                                ).onSuccess {
                                    viewModel.showToast("Excel sheet exported: ${it.name}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_export_excel")
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Excel", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                ReportExportHelper.exportCsvAndShare(
                                    context = context,
                                    reportTitle = selectedReportType.label,
                                    summary = reportSummary,
                                    transactions = periodFilteredTransactions,
                                    isExcelCompatible = false
                                ).onSuccess {
                                    viewModel.showToast("CSV exported: ${it.name}")
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_export_csv")
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV")
                        }

                        OutlinedButton(
                            onClick = {
                                ReportExportHelper.printReport(
                                    context = context,
                                    reportTitle = selectedReportType.label,
                                    dateRangeLabel = reportDateFilter.label,
                                    summary = reportSummary,
                                    transactions = periodFilteredTransactions
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_print_report")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print Report")
                        }
                    }
                }
            }
        }

        // 10 Report Type Selector Chips
        item {
            Column {
                Text(
                    text = "Select Report Type (10 Specialized Reports)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReportType.entries.forEach { rType ->
                        val selected = selectedReportType == rType
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setSelectedReportType(rType) },
                            label = {
                                Text(
                                    text = rType.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Date Range Filter for Report
        item {
            DateFilterRow(
                selectedOption = reportDateFilter,
                onSelectOption = { viewModel.setReportDateFilter(it) },
                onOpenCustomPicker = { viewModel.setReportDateFilter(DateFilterOption.ALL_TIME) }
            )
        }

        // 9 Summary Cards for the Active Report
        item {
            NineSummaryCardsGrid(summary = reportSummary)
        }

        // Report Visuals
        item {
            IncomeVsExpenseBarChart(summary = reportSummary)
        }

        item {
            ExpenseBreakdownDonutCard(
                title = "${selectedReportType.label} — Category Breakdown",
                subtitle = "Distribution across categories",
                items = categoryBreakdown
            )
        }

        // Payment Method Report Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Method Report Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    paymentBreakdown.forEach { (pm, amt, pct) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(pm, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${CurrencyUtils.formatInr(amt)} (${pct.toInt()}%)",
                                fontWeight = FontWeight.Bold,
                                color = BrandDarkBlue
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    }
                }
            }
        }

        // Detailed Transactions in Report
        item {
            Text(
                text = "Report Ledger (${periodFilteredTransactions.size} Records)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        items(periodFilteredTransactions, key = { it.id }) { tx ->
            TransactionItemCard(
                tx = tx,
                onView = { viewModel.setViewingTransaction(tx) },
                onEdit = { viewModel.openTransactionModal(tx.section, tx.type, tx) },
                onDelete = { viewModel.requestDeleteTransaction(tx) }
            )
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}
