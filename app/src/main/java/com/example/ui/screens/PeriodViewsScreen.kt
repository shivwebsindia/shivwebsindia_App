package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.TransactionEntity
import com.example.ui.components.ExpenseTrendLineChartCard
import com.example.ui.components.IncomeVsExpenseBarChart
import com.example.ui.components.NineSummaryCardsGrid
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateUtils
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyViewScreen(
    viewModel: FinanceViewModel,
    selectedDateMillis: Long,
    allTransactions: List<TransactionEntity>
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val dayStart = DateUtils.startOfDay(selectedDateMillis)
    val dayEnd = DateUtils.endOfDay(selectedDateMillis)

    val dailyTransactions = remember(allTransactions, dayStart, dayEnd) {
        allTransactions
            .filter { it.dateMillis in dayStart..dayEnd }
            .sortedBy { it.dateMillis }
    }

    val dailySummary = remember(dailyTransactions) {
        FinancialSummary.fromTransactions(dailyTransactions)
    }

    // Compute running balance per transaction in chronological order
    val transactionsWithRunningBalance = remember(dailyTransactions) {
        var running = 0.0
        dailyTransactions.map { tx ->
            if (tx.type == FinanceConstants.TYPE_INCOME) running += tx.amount
            else running -= tx.amount
            tx to running
        }.reversed()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("daily_view_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Date Selector Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Finance View",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = "Date: ${DateUtils.formatDate(selectedDateMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandOrange
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setSelectedDailyDate(selectedDateMillis - 86400000L) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Day",
                                tint = Color.White
                            )
                        }
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, BrandOrange)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(DateUtils.formatShortDate(selectedDateMillis))
                        }
                        IconButton(
                            onClick = { viewModel.setSelectedDailyDate(selectedDateMillis + 86400000L) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Day",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 9 Summary Cards for the Selected Day
        item {
            NineSummaryCardsGrid(summary = dailySummary)
        }

        // Transaction Table / Ledger Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Transaction Ledger (${dailyTransactions.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Button(
                    onClick = {
                        viewModel.openTransactionModal(
                            FinanceConstants.SECTION_OFFICE,
                            FinanceConstants.TYPE_EXPENSE
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Add Entry", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (transactionsWithRunningBalance.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions recorded on ${DateUtils.formatDate(selectedDateMillis)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { viewModel.setSelectedDailyDate(System.currentTimeMillis()) }
                        ) {
                            Text("Jump to Today", color = BrandOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            itemsIndexed(transactionsWithRunningBalance, key = { _, pair -> pair.first.id }) { _, (tx, runBal) ->
                TransactionItemCard(
                    tx = tx,
                    runningBalance = runBal,
                    onView = { viewModel.setViewingTransaction(tx) },
                    onEdit = { viewModel.openTransactionModal(tx.section, tx.type, tx) },
                    onDelete = { viewModel.requestDeleteTransaction(tx) },
                    onDuplicate = { viewModel.duplicateTransaction(tx) }
                )
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { viewModel.setSelectedDailyDate(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("Select Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun WeeklyViewScreen(
    viewModel: FinanceViewModel,
    weekStartMondayMillis: Long,
    allTransactions: List<TransactionEntity>
) {
    val weekEndSundayMillis = DateUtils.endOfDay(weekStartMondayMillis + 6 * 86400000L)

    val weeklyTransactions = remember(allTransactions, weekStartMondayMillis, weekEndSundayMillis) {
        allTransactions.filter { it.dateMillis in weekStartMondayMillis..weekEndSundayMillis }
    }

    val weeklySummary = remember(weeklyTransactions) {
        FinancialSummary.fromTransactions(weeklyTransactions)
    }

    val dayBreakdowns = remember(weekStartMondayMillis, weeklyTransactions) {
        viewModel.getWeeklyDayBreakdown(weekStartMondayMillis, weeklyTransactions)
    }

    val weeklyChartPoints = remember(dayBreakdowns) {
        dayBreakdowns.map { it.dayName.take(3) to it.totalExpense }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("weekly_view_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Week Selector Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Weekly Finance Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = "${DateUtils.formatShortDate(weekStartMondayMillis)} – ${DateUtils.formatDate(weekEndSundayMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandOrange
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setSelectedWeekStart(weekStartMondayMillis - 7 * 86400000L) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Week",
                                tint = Color.White
                            )
                        }
                        TextButton(
                            onClick = { viewModel.setSelectedWeekStart(System.currentTimeMillis()) }
                        ) {
                            Text("This Week", color = BrandOrange, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { viewModel.setSelectedWeekStart(weekStartMondayMillis + 7 * 86400000L) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Week",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 9 Weekly Summary Cards
        item {
            NineSummaryCardsGrid(summary = weeklySummary)
        }

        // Weekly Chart
        item {
            ExpenseTrendLineChartCard(
                title = "Weekly Expense Trend (Monday – Sunday)",
                subtitle = "Day-wise expenditure trajectory for selected week",
                points = weeklyChartPoints,
                lineColor = BrandOrange
            )
        }

        // Day-wise Breakdown Table (Monday to Sunday)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Day-Wise Breakdown (Monday – Sunday)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Daily Income, Expense & Net Balance across the week",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandDarkBlue, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Day",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF34D399),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF87171),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    dayBreakdowns.forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = day.dayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = day.dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = CurrencyUtils.formatInr(day.totalIncome),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = IncomeGreen,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = CurrencyUtils.formatInr(day.totalExpense),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = BrandDarkRed,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = CurrencyUtils.formatInr(day.balance),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (day.balance >= 0) BrandDarkBlue else BrandDarkRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}

@Composable
fun MonthlyViewScreen(
    viewModel: FinanceViewModel,
    selectedMonth: Int,
    selectedYear: Int,
    allTransactions: List<TransactionEntity>
) {
    val (monthStart, monthEnd) = remember(selectedMonth, selectedYear) {
        DateUtils.getMonthRange(selectedYear, selectedMonth)
    }

    val monthlyTransactions = remember(allTransactions, monthStart, monthEnd) {
        allTransactions.filter { it.dateMillis in monthStart..monthEnd }
    }

    val monthlySummary = remember(monthlyTransactions) {
        FinancialSummary.fromTransactions(monthlyTransactions)
    }

    val categoryExpenseBreakdown = remember(monthlyTransactions) {
        viewModel.getCategoryBreakdown(monthlyTransactions, null, FinanceConstants.TYPE_EXPENSE)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("monthly_view_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Month & Year Selector Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Finance Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = "${DateUtils.monthNames[selectedMonth]} $selectedYear",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandOrange
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (selectedMonth == 0) {
                                    viewModel.setSelectedMonthYear(11, selectedYear - 1)
                                } else {
                                    viewModel.setSelectedMonthYear(selectedMonth - 1, selectedYear)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BrandOrange
                        ) {
                            Text(
                                text = "${DateUtils.monthNames[selectedMonth].take(3)} $selectedYear",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (selectedMonth == 11) {
                                    viewModel.setSelectedMonthYear(0, selectedYear + 1)
                                } else {
                                    viewModel.setSelectedMonthYear(selectedMonth + 1, selectedYear)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 9 Monthly Summary Cards
        item {
            NineSummaryCardsGrid(summary = monthlySummary)
        }

        // Monthly Income vs Expense Chart
        item {
            IncomeVsExpenseBarChart(summary = monthlySummary)
        }

        // Monthly Category Expense Breakdown Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monthly Expense Breakdown by Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Detailed category expenditure for ${DateUtils.monthNames[selectedMonth]} $selectedYear",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandDarkBlue, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Category (Section)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandOrange
                        )
                    }

                    if (categoryExpenseBreakdown.isEmpty()) {
                        Text(
                            text = "No expenses recorded for this month.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        categoryExpenseBreakdown.forEach { item ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = item.section,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = CurrencyUtils.formatInr(item.amount),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = BrandDarkRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (item.percentage / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = BrandOrange,
                                    trackColor = BrandOrange.copy(alpha = 0.15f)
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
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
