package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppScreen
import com.example.data.BudgetProgressItem
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.RecurringExpenseEntity
import com.example.data.TransactionEntity
import com.example.data.UserEntity
import com.example.ui.components.DateFilterRow
import com.example.ui.components.ExpenseBreakdownDonutCard
import com.example.ui.components.ExpenseTrendLineChartCard
import com.example.ui.components.IncomeVsExpenseBarChart
import com.example.ui.components.NineSummaryCardsGrid
import com.example.ui.components.QuickActionButtonsRow
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateFilterOption
import com.example.util.DateUtils
import com.example.viewmodel.FinanceViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    user: UserEntity?,
    summary: FinancialSummary,
    transactions: List<TransactionEntity>,
    allTransactions: List<TransactionEntity>,
    dateFilter: DateFilterOption,
    budgetProgressList: List<BudgetProgressItem>,
    recurringExpenses: List<RecurringExpenseEntity>
) {
    var showCustomStartPicker by remember { mutableStateOf(false) }
    var showCustomEndPicker by remember { mutableStateOf(false) }
    var tempStartMillis by remember { mutableStateOf(System.currentTimeMillis() - 7 * 86400000L) }

    val officeBreakdown = remember(transactions) {
        viewModel.getCategoryBreakdown(transactions, FinanceConstants.SECTION_OFFICE, FinanceConstants.TYPE_EXPENSE)
    }
    val homeBreakdown = remember(transactions) {
        viewModel.getCategoryBreakdown(transactions, FinanceConstants.SECTION_HOME, FinanceConstants.TYPE_EXPENSE)
    }

    // Compute Daily Expense Trend (last 7 days)
    val dailyTrendPoints = remember(allTransactions) {
        val now = System.currentTimeMillis()
        (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, -daysAgo)
            }
            val start = DateUtils.startOfDay(cal.timeInMillis)
            val end = DateUtils.endOfDay(cal.timeInMillis)
            val exp = allTransactions
                .filter { it.type == FinanceConstants.TYPE_EXPENSE && it.dateMillis in start..end }
                .sumOf { it.amount }
            DateUtils.formatShortDate(start) to exp
        }
    }

    // Compute Weekly Expense Trend (last 4 weeks)
    val weeklyTrendPoints = remember(allTransactions) {
        val now = System.currentTimeMillis()
        (3 downTo 0).map { weeksAgo ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.WEEK_OF_YEAR, -weeksAgo)
            }
            val mon = DateUtils.getStartOfWeekMonday(cal.timeInMillis)
            val sun = DateUtils.endOfDay(mon + 6 * 86400000L)
            val exp = allTransactions
                .filter { it.type == FinanceConstants.TYPE_EXPENSE && it.dateMillis in mon..sun }
                .sumOf { it.amount }
            "Wk ${4 - weeksAgo}" to exp
        }
    }

    // Compute Monthly Expense Trend (last 6 months)
    val monthlyTrendPoints = remember(allTransactions) {
        val nowCal = Calendar.getInstance()
        (5 downTo 0).map { monthsAgo ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = nowCal.timeInMillis
                add(Calendar.MONTH, -monthsAgo)
            }
            val yr = cal.get(Calendar.YEAR)
            val mo = cal.get(Calendar.MONTH)
            val (s, e) = DateUtils.getMonthRange(yr, mo)
            val exp = allTransactions
                .filter { it.type == FinanceConstants.TYPE_EXPENSE && it.dateMillis in s..e }
                .sumOf { it.amount }
            DateUtils.monthNames[mo].take(3) to exp
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Banner Card with Welcome & Quick Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_finance_hero),
                        contentDescription = "Financial Dashboard Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.25f
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        BrandDarkBlue.copy(alpha = 0.75f),
                                        BrandDarkBlue
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Welcome back, ${user?.fullName ?: "Administrator"}!",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${user?.companyName ?: "ShivWebsIndia"} • Office & Home Executive Finance Suite",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BrandOrange
                                ) {
                                    Text(
                                        text = CurrencyUtils.formatInr(summary.combinedBalance),
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            QuickActionButtonsRow(
                                onAddOfficeIncome = {
                                    viewModel.openTransactionModal(
                                        FinanceConstants.SECTION_OFFICE,
                                        FinanceConstants.TYPE_INCOME
                                    )
                                },
                                onAddOfficeExpense = {
                                    viewModel.openTransactionModal(
                                        FinanceConstants.SECTION_OFFICE,
                                        FinanceConstants.TYPE_EXPENSE
                                    )
                                },
                                onAddHomeIncome = {
                                    viewModel.openTransactionModal(
                                        FinanceConstants.SECTION_HOME,
                                        FinanceConstants.TYPE_INCOME
                                    )
                                },
                                onAddHomeExpense = {
                                    viewModel.openTransactionModal(
                                        FinanceConstants.SECTION_HOME,
                                        FinanceConstants.TYPE_EXPENSE
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Date Filter Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Dashboard Period",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${transactions.size} Records in Period",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandOrange
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                DateFilterRow(
                    selectedOption = dateFilter,
                    onSelectOption = { viewModel.setDashboardDateFilter(it) },
                    onOpenCustomPicker = { showCustomStartPicker = true }
                )
            }
        }

        // 9 Executive Financial Summary Cards (Office 3, Home 3, Combined 3)
        item {
            NineSummaryCardsGrid(summary = summary)
        }

        // Chart 1: Income vs Expense Bar Chart
        item {
            IncomeVsExpenseBarChart(summary = summary)
        }

        // Charts 2 & 3: Office Expense Breakdown & Home Expense Breakdown
        item {
            ExpenseBreakdownDonutCard(
                title = "Office Expense Breakdown",
                subtitle = "Category-wise distribution of Office expenses",
                items = officeBreakdown
            )
        }

        item {
            ExpenseBreakdownDonutCard(
                title = "Home Expense Breakdown",
                subtitle = "Category-wise distribution of Household expenses",
                items = homeBreakdown
            )
        }

        // Charts 4, 5, 6: Daily, Weekly, and Monthly Expense Trends
        item {
            ExpenseTrendLineChartCard(
                title = "Daily Expense Trend (Last 7 Days)",
                subtitle = "Day-by-day combined expenditure tracking",
                points = dailyTrendPoints,
                lineColor = BrandOrange
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExpenseTrendLineChartCard(
                    title = "Weekly Expense Trend",
                    subtitle = "4-week trajectory",
                    points = weeklyTrendPoints,
                    lineColor = BrandDarkBlue,
                    modifier = Modifier.weight(1f)
                )
                ExpenseTrendLineChartCard(
                    title = "Monthly Expense Trend",
                    subtitle = "6-month trajectory",
                    points = monthlyTrendPoints,
                    lineColor = BrandDarkRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Budget Status Section on Dashboard
        item {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = BrandOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Budget Status Overview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.BUDGETS) }) {
                            Text("Manage Budgets", color = BrandOrange, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    budgetProgressList.take(4).forEach { item ->
                        val label = if (item.budget.category == "ALL") {
                            "${item.budget.section} Overall Monthly Budget"
                        } else {
                            "${item.budget.section} • ${item.budget.category}"
                        }
                        val barColor = when {
                            item.isExceeded -> BrandDarkRed
                            item.isNearLimit -> BrandOrange
                            else -> IncomeGreen
                        }
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${CurrencyUtils.formatInr(item.actualExpense)} / ${CurrencyUtils.formatInr(item.budget.monthlyLimit)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = barColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { item.progressRatio.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = barColor,
                                trackColor = barColor.copy(alpha = 0.15f)
                            )
                            Text(
                                text = if (item.isExceeded) {
                                    "Alert: Budget exceeded by ${CurrencyUtils.formatInr(-item.remaining)}"
                                } else {
                                    "Remaining: ${CurrencyUtils.formatInr(item.remaining)}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.isExceeded) BrandDarkRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Upcoming Recurring Expenses Section on Dashboard
        item {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = BrandDarkBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upcoming Recurring Expenses",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.RECURRING) }) {
                            Text("View All", color = BrandOrange, fontWeight = FontWeight.Bold)
                        }
                    }

                    recurringExpenses.take(3).forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${rec.title} (${rec.section})",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Due: ${DateUtils.formatDate(rec.nextDueDateMillis)} • ${rec.frequency} • ${rec.paidTo}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = CurrencyUtils.formatInr(rec.amount),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = BrandDarkRed
                                )
                                OutlinedButton(
                                    onClick = { viewModel.payRecurringExpenseNow(rec) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Pay", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.TRANSACTIONS) }) {
                    Text("See All Transactions", color = BrandOrange, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(16.dp))
                }
            }
        }

        items(transactions.take(6), key = { it.id }) { tx ->
            TransactionItemCard(
                tx = tx,
                onView = { viewModel.setViewingTransaction(tx) },
                onEdit = { viewModel.openTransactionModal(tx.section, tx.type, tx) },
                onDelete = { viewModel.requestDeleteTransaction(tx) },
                onDuplicate = { viewModel.duplicateTransaction(tx) }
            )
        }

        // ShivWebsIndia Footer
        item {
            ShivWebsIndiaFooter()
        }
    }

    if (showCustomStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = tempStartMillis)
        DatePickerDialog(
            onDismissRequest = { showCustomStartPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { tempStartMillis = it }
                        showCustomStartPicker = false
                        showCustomEndPicker = true
                    }
                ) {
                    Text("Next: End Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomStartPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showCustomEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showCustomEndPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val endMillis = state.selectedDateMillis ?: System.currentTimeMillis()
                        viewModel.setDashboardDateFilter(
                            DateFilterOption.CUSTOM,
                            customStart = minOf(tempStartMillis, endMillis),
                            customEnd = maxOf(tempStartMillis, endMillis)
                        )
                        showCustomEndPicker = false
                    }
                ) {
                    Text("Apply Range")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomEndPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
