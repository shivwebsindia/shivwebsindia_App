package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.unit.dp
import com.example.data.CategoryEntity
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.TransactionEntity
import com.example.ui.components.ExpenseBreakdownDonutCard
import com.example.ui.components.SectionTopSummaryStrip
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.ReportExportHelper
import com.example.viewmodel.FinanceViewModel

private enum class ModuleSubTab(val label: String) {
    OVERVIEW("All Records"),
    INCOME("Income"),
    EXPENSES("Expenses"),
    CATEGORIES("Categories"),
    REPORTS("Reports")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SectionFinanceScreen(
    section: String, // "OFFICE" or "HOME"
    viewModel: FinanceViewModel,
    allTransactions: List<TransactionEntity>,
    allCategories: List<CategoryEntity>
) {
    val context = LocalContext.current
    val isOffice = section == FinanceConstants.SECTION_OFFICE
    var selectedSubTab by remember(section) { mutableStateOf(ModuleSubTab.OVERVIEW) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryType by remember { mutableStateOf(FinanceConstants.TYPE_EXPENSE) }

    val sectionTransactions = remember(allTransactions, section) {
        allTransactions.filter { it.section == section }
    }
    val sectionIncome = remember(sectionTransactions) {
        sectionTransactions.filter { it.type == FinanceConstants.TYPE_INCOME }.sumOf { it.amount }
    }
    val sectionExpense = remember(sectionTransactions) {
        sectionTransactions.filter { it.type == FinanceConstants.TYPE_EXPENSE }.sumOf { it.amount }
    }
    val sectionBalance = sectionIncome - sectionExpense

    val displayedTransactions = remember(sectionTransactions, selectedSubTab) {
        when (selectedSubTab) {
            ModuleSubTab.INCOME -> sectionTransactions.filter { it.type == FinanceConstants.TYPE_INCOME }
            ModuleSubTab.EXPENSES -> sectionTransactions.filter { it.type == FinanceConstants.TYPE_EXPENSE }
            else -> sectionTransactions
        }
    }

    val sectionCategories = remember(allCategories, section) {
        allCategories.filter { it.section == section }
    }

    val expenseBreakdown = remember(sectionTransactions) {
        viewModel.getCategoryBreakdown(sectionTransactions, section, FinanceConstants.TYPE_EXPENSE)
    }

    val incomeBreakdown = remember(sectionTransactions) {
        viewModel.getCategoryBreakdown(sectionTransactions, section, FinanceConstants.TYPE_INCOME)
    }

    val paymentMethodBreakdown = remember(sectionTransactions) {
        val total = sectionTransactions.sumOf { it.amount }.coerceAtLeast(1.0)
        sectionTransactions.groupBy { it.paymentMethod }.map { (method, list) ->
            val sum = list.sumOf { it.amount }
            Triple(method, sum, ((sum / total) * 100.0).toFloat())
        }.sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("${section.lowercase()}_finance_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Financial Summary Strip (Income, Expense, Balance)
        item {
            SectionTopSummaryStrip(
                title = if (isOffice) "Office Finance Module" else "Home Finance Module",
                subtitle = if (isOffice) {
                    "Completely separated Office Income, Office Expenses, Categories & Profit Calculation"
                } else {
                    "Completely separated Household Income, Expenses, Categories & Remaining Balance"
                },
                income = sectionIncome,
                expense = sectionExpense,
                balance = sectionBalance
            )
        }

        // Action Buttons to Add Section Income or Expense
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.openTransactionModal(section, FinanceConstants.TYPE_INCOME)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_add_${section.lowercase()}_income"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOffice) "Add Office Income" else "Add Home Income",
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        viewModel.openTransactionModal(section, FinanceConstants.TYPE_EXPENSE)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_add_${section.lowercase()}_expense"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOffice) "Add Office Expense" else "Add Home Expense",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sub-section Navigation Chips (All, Income, Expenses, Categories, Reports)
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ModuleSubTab.entries.forEach { tab ->
                    val selected = selectedSubTab == tab
                    FilterChip(
                        selected = selected,
                        onClick = { selectedSubTab = tab },
                        label = {
                            Text(
                                text = if (isOffice) "Office ${tab.label}" else "Home ${tab.label}",
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandDarkBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        when (selectedSubTab) {
            ModuleSubTab.CATEGORIES -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Create Custom ${if (isOffice) "Office" else "Home"} Category",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = newCategoryType == FinanceConstants.TYPE_EXPENSE,
                                    onClick = { newCategoryType = FinanceConstants.TYPE_EXPENSE },
                                    label = { Text("Expense Category") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandDarkRed,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = newCategoryType == FinanceConstants.TYPE_INCOME,
                                    onClick = { newCategoryType = FinanceConstants.TYPE_INCOME },
                                    label = { Text("Income Source") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IncomeGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCategoryName,
                                    onValueChange = { newCategoryName = it },
                                    label = { Text("Category Name") },
                                    placeholder = { Text("e.g. Cloud AI Tools / Organic Milk") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (newCategoryName.isNotBlank()) {
                                            viewModel.addCategory(section, newCategoryType, newCategoryName)
                                            newCategoryName = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                    modifier = Modifier.height(54.dp)
                                ) {
                                    Text("Add", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${if (isOffice) "Office" else "Home"} Categories (${sectionCategories.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sectionCategories.forEach { cat ->
                                    val isInc = cat.type == FinanceConstants.TYPE_INCOME
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isInc) IncomeGreen.copy(alpha = 0.1f) else BrandDarkBlue.copy(alpha = 0.08f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isInc) IncomeGreen.copy(alpha = 0.3f) else BrandDarkBlue.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${cat.name} (${if (isInc) "Inc" else "Exp"})",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                            if (!cat.isDefault) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { viewModel.deleteCategory(cat) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Remove",
                                                        tint = BrandDarkRed,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ModuleSubTab.REPORTS -> {
                item {
                    ExpenseBreakdownDonutCard(
                        title = "${if (isOffice) "Office" else "Home"} Expense Breakdown",
                        subtitle = "Category-wise expenditure distribution",
                        items = expenseBreakdown
                    )
                }

                item {
                    ExpenseBreakdownDonutCard(
                        title = "${if (isOffice) "Office" else "Home"} Income Sources Breakdown",
                        subtitle = "Category-wise inflow distribution",
                        items = incomeBreakdown
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Payment Method Analysis",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            paymentMethodBreakdown.forEach { (method, amt, pct) ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = method,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${CurrencyUtils.formatInr(amt)} (${pct.toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = BrandOrange
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (pct / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = BrandOrange,
                                        trackColor = BrandOrange.copy(alpha = 0.15f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))
                            val sectionSummary = FinancialSummary.fromTransactions(sectionTransactions)
                            val reportTitle = if (isOffice) "Office Finance Report" else "Home Finance Report"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        ReportExportHelper.exportPdfAndShare(
                                            context = context,
                                            reportTitle = reportTitle,
                                            dateRangeLabel = "All Records",
                                            summary = sectionSummary,
                                            transactions = sectionTransactions
                                        ).onSuccess {
                                            viewModel.showToast("PDF Report generated: ${it.name}")
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BrandDarkRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PDF")
                                }
                                OutlinedButton(
                                    onClick = {
                                        ReportExportHelper.exportCsvAndShare(
                                            context = context,
                                            reportTitle = reportTitle,
                                            summary = sectionSummary,
                                            transactions = sectionTransactions
                                        ).onSuccess {
                                            viewModel.showToast("CSV exported: ${it.name}")
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.TableChart, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("CSV")
                                }
                                OutlinedButton(
                                    onClick = {
                                        ReportExportHelper.printReport(
                                            context = context,
                                            reportTitle = reportTitle,
                                            dateRangeLabel = "All Records",
                                            summary = sectionSummary,
                                            transactions = sectionTransactions
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = BrandDarkBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Print")
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                item {
                    ExpenseBreakdownDonutCard(
                        title = "${if (isOffice) "Office" else "Home"} Expense Breakdown",
                        subtitle = "Visual breakdown of ${if (isOffice) "Office" else "Household"} expenditure",
                        items = expenseBreakdown
                    )
                }

                item {
                    Text(
                        text = "${if (isOffice) "Office" else "Home"} ${selectedSubTab.label} (${displayedTransactions.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }

                items(displayedTransactions, key = { it.id }) { tx ->
                    TransactionItemCard(
                        tx = tx,
                        onView = { viewModel.setViewingTransaction(tx) },
                        onEdit = { viewModel.openTransactionModal(tx.section, tx.type, tx) },
                        onDelete = { viewModel.requestDeleteTransaction(tx) },
                        onDuplicate = { viewModel.duplicateTransaction(tx) }
                    )
                }
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}
