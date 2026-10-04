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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.BudgetProgressItem
import com.example.data.CategoryEntity
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.RecurringExpenseEntity
import com.example.ui.components.SectionTopSummaryStrip
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateUtils
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    viewModel: FinanceViewModel,
    budgetProgressList: List<BudgetProgressItem>,
    allCategories: List<CategoryEntity>,
    overallSummary: FinancialSummary
) {
    var selectedSection by remember { mutableStateOf(FinanceConstants.SECTION_OFFICE) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var limitAmountText by remember { mutableStateOf("") }
    var catMenuExpanded by remember { mutableStateOf(false) }

    val availableCategories = remember(selectedSection, allCategories) {
        listOf("ALL") + allCategories
            .filter { it.section == selectedSection && it.type == FinanceConstants.TYPE_EXPENSE }
            .map { it.name }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("budgets_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            SectionTopSummaryStrip(
                title = "Expense Budget Management",
                subtitle = "Set Monthly Office, Home & Category-wise budgets with real-time visual progress & alerts",
                income = overallSummary.combinedIncome,
                expense = overallSummary.combinedExpense,
                balance = overallSummary.combinedBalance
            )
        }

        // Add / Update Budget Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Create or Update Monthly Budget",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedSection == FinanceConstants.SECTION_OFFICE,
                            onClick = {
                                selectedSection = FinanceConstants.SECTION_OFFICE
                                selectedCategory = "ALL"
                            },
                            label = { Text("Office Budget") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedSection == FinanceConstants.SECTION_HOME,
                            onClick = {
                                selectedSection = FinanceConstants.SECTION_HOME
                                selectedCategory = "ALL"
                            },
                            label = { Text("Home Budget") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = catMenuExpanded,
                        onExpandedChange = { catMenuExpanded = !catMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedCategory == "ALL") "Overall Monthly $selectedSection Budget" else selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Budget Scope / Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catMenuExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = catMenuExpanded,
                            onDismissRequest = { catMenuExpanded = false }
                        ) {
                            availableCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (cat == "ALL") "Overall Monthly $selectedSection Budget" else cat
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = cat
                                        catMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = limitAmountText,
                            onValueChange = { limitAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Monthly Budget Limit (₹)") },
                            placeholder = { Text("e.g. 20000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_budget_limit")
                        )
                        Button(
                            onClick = {
                                val amt = limitAmountText.toDoubleOrNull()
                                if (amt != null && amt > 0) {
                                    val existing = budgetProgressList.find {
                                        it.budget.section == selectedSection && it.budget.category == selectedCategory
                                    }?.budget
                                    viewModel.saveBudget(
                                        existingId = existing?.id ?: 0L,
                                        section = selectedSection,
                                        category = selectedCategory,
                                        monthlyLimit = amt
                                    )
                                    limitAmountText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("btn_save_budget")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Budget", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Active Monthly Budgets (${budgetProgressList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        items(budgetProgressList, key = { it.budget.id }) { item ->
            val b = item.budget
            val statusColor = when {
                item.isExceeded -> BrandDarkRed
                item.isNearLimit -> BrandOrange
                else -> IncomeGreen
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = if (item.isExceeded) 1.5.dp else 1.dp,
                    color = if (item.isExceeded) BrandDarkRed else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandDarkBlue
                            ) {
                                Text(
                                    text = b.section,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (b.category == "ALL") "Monthly ${b.section} Overall Budget" else "${b.section} ${b.category} Budget",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                        IconButton(onClick = { viewModel.deleteBudget(b) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Budget",
                                tint = BrandDarkRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Budget Limit", style = MaterialTheme.typography.labelSmall)
                            Text(
                                CurrencyUtils.formatInr(b.monthlyLimit),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                        Column {
                            Text("Actual Expense", style = MaterialTheme.typography.labelSmall)
                            Text(
                                CurrencyUtils.formatInr(item.actualExpense),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = statusColor
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(if (item.isExceeded) "Exceeded By" else "Remaining", style = MaterialTheme.typography.labelSmall)
                            Text(
                                CurrencyUtils.formatInr(if (item.isExceeded) -item.remaining else item.remaining),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { item.progressRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = statusColor,
                        trackColor = statusColor.copy(alpha = 0.15f)
                    )

                    if (item.isExceeded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandDarkRed.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = BrandDarkRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ALERT: Expenses have exceeded this budget by ${CurrencyUtils.formatInr(-item.remaining)}!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BrandDarkRed
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecurringExpensesScreen(
    viewModel: FinanceViewModel,
    recurringExpenses: List<RecurringExpenseEntity>,
    allCategories: List<CategoryEntity>,
    overallSummary: FinancialSummary
) {
    var section by remember { mutableStateOf(FinanceConstants.SECTION_OFFICE) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Monthly") }
    var paymentMethod by remember { mutableStateOf("Bank Transfer") }
    var paidTo by remember { mutableStateOf("") }
    var catMenuExpanded by remember { mutableStateOf(false) }

    val availableCats = remember(section, allCategories) {
        val list = allCategories.filter { it.section == section && it.type == FinanceConstants.TYPE_EXPENSE }.map { it.name }
        if (list.isNotEmpty()) list else FinanceConstants.DEFAULT_OFFICE_EXPENSE_CATEGORIES
    }
    var selectedCategory by remember(availableCats) { mutableStateOf(availableCats.firstOrNull().orEmpty()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("recurring_expenses_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            SectionTopSummaryStrip(
                title = "Recurring Expenses & Scheduled Bills",
                subtitle = "Manage Office Rent, Salaries, Hosting, Internet, EMIs & Insurance with 1-click payment recording",
                income = overallSummary.combinedIncome,
                expense = overallSummary.combinedExpense,
                balance = overallSummary.combinedBalance
            )
        }

        // Add Recurring Expense Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Add Recurring Expense Reminder",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Expense Title *") },
                            placeholder = { Text("e.g. Office Rent / EMI") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Amount (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = catMenuExpanded,
                        onExpandedChange = { catMenuExpanded = !catMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catMenuExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = catMenuExpanded,
                            onDismissRequest = { catMenuExpanded = false }
                        ) {
                            availableCats.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        selectedCategory = c
                                        catMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = paidTo,
                        onValueChange = { paidTo = it },
                        label = { Text("Paid To (Vendor / Bank / Staff)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Frequency", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FinanceConstants.FREQUENCIES.forEach { freq ->
                            FilterChip(
                                selected = frequency == freq,
                                onClick = { frequency = freq },
                                label = { Text(freq) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt != null && amt > 0 && title.isNotBlank()) {
                                viewModel.saveRecurringExpense(
                                    section = section,
                                    title = title,
                                    category = selectedCategory,
                                    amount = amt,
                                    frequency = frequency,
                                    paymentMethod = paymentMethod,
                                    paidTo = paidTo.ifBlank { selectedCategory },
                                    nextDueDateMillis = System.currentTimeMillis() + 7 * 86400000L,
                                    notes = "Scheduled $frequency recurring expense"
                                )
                                title = ""
                                amountText = ""
                                paidTo = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Recurring Expense", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Scheduled Recurring Expenses (${recurringExpenses.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        items(recurringExpenses, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandDarkBlue
                                ) {
                                    Text(
                                        text = item.section,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandOrange.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = item.frequency,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = BrandOrange,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Paid To: ${item.paidTo}  •  Next Due: ${DateUtils.formatDate(item.nextDueDateMillis)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = CurrencyUtils.formatInr(item.amount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = BrandDarkRed
                            )
                            IconButton(onClick = { viewModel.deleteRecurringExpense(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandDarkRed)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.payRecurringExpenseNow(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Payment Now & Advance Due Date", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}
