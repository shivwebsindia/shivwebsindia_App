package com.example.data

import com.example.util.DateFilterOption

enum class AppScreen(val title: String) {
    DASHBOARD("Dashboard"),
    OFFICE_FINANCE("Office Finance"),
    HOME_FINANCE("Home Finance"),
    TRANSACTIONS("All Transactions"),
    DAILY_REPORT("Daily View"),
    WEEKLY_REPORT("Weekly View"),
    MONTHLY_REPORT("Monthly View"),
    BUDGETS("Expense Budgets"),
    RECURRING("Recurring Expenses"),
    CATEGORIES("Categories"),
    REPORTS("Financial Reports"),
    SETTINGS_PROFILE("Settings & Profile")
}

enum class SortOption(val label: String) {
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    HIGHEST_AMOUNT("Highest Amount"),
    LOWEST_AMOUNT("Lowest Amount")
}

enum class ReportType(val label: String, val subtitle: String) {
    DAILY("Daily Report", "Complete breakdown of today's Office & Home transactions"),
    WEEKLY("Weekly Report", "7-day Monday to Sunday performance and expense trends"),
    MONTHLY("Monthly Report", "Month-wise income, expense & category summary"),
    OFFICE("Office Finance Report", "Dedicated Office Income, Expenses & Profit calculation"),
    HOME("Home Finance Report", "Dedicated Household Income, Expenses & Remaining Balance"),
    INCOME("Income Report", "All Office & Home income sources and client inflows"),
    EXPENSE("Expense Report", "Complete Office & Home expenditure analysis"),
    CATEGORY_WISE("Category-wise Report", "Aggregated totals grouped by financial categories"),
    PAYMENT_METHOD("Payment Method Report", "Breakdown by Cash, Bank Transfer, UPI, Cards"),
    PROFIT_BALANCE("Profit / Balance Report", "Net profit, savings rate & Office vs Home comparison")
}

data class FinancialSummary(
    val officeIncome: Double = 0.0,
    val officeExpense: Double = 0.0,
    val homeIncome: Double = 0.0,
    val homeExpense: Double = 0.0
) {
    // Strictly enforced automatic calculation formulas:
    // Total Income = Office Income + Home Income
    val combinedIncome: Double get() = officeIncome + homeIncome

    // Total Expense = Office Expense + Home Expense
    val combinedExpense: Double get() = officeExpense + homeExpense

    // Office Balance = Office Income - Office Expense
    val officeBalance: Double get() = officeIncome - officeExpense

    // Home Balance = Home Income - Home Expense
    val homeBalance: Double get() = homeIncome - homeExpense

    // Combined Balance = Total Income - Total Expense
    val combinedBalance: Double get() = combinedIncome - combinedExpense

    companion object {
        fun fromTransactions(list: List<TransactionEntity>): FinancialSummary {
            var offInc = 0.0
            var offExp = 0.0
            var homInc = 0.0
            var homExp = 0.0
            for (tx in list) {
                if (tx.section == FinanceConstants.SECTION_OFFICE) {
                    if (tx.type == FinanceConstants.TYPE_INCOME) offInc += tx.amount
                    else if (tx.type == FinanceConstants.TYPE_EXPENSE) offExp += tx.amount
                } else if (tx.section == FinanceConstants.SECTION_HOME) {
                    if (tx.type == FinanceConstants.TYPE_INCOME) homInc += tx.amount
                    else if (tx.type == FinanceConstants.TYPE_EXPENSE) homExp += tx.amount
                }
            }
            return FinancialSummary(
                officeIncome = offInc,
                officeExpense = offExp,
                homeIncome = homInc,
                homeExpense = homExp
            )
        }
    }
}

data class CategoryBreakdownItem(
    val category: String,
    val section: String,
    val type: String,
    val amount: Double,
    val percentage: Float,
    val count: Int
)

data class DayBreakdownItem(
    val dayName: String,
    val dateLabel: String,
    val dateMillis: Long,
    val officeIncome: Double,
    val officeExpense: Double,
    val homeIncome: Double,
    val homeExpense: Double
) {
    val totalIncome: Double get() = officeIncome + homeIncome
    val totalExpense: Double get() = officeExpense + homeExpense
    val balance: Double get() = totalIncome - totalExpense
}

data class BudgetProgressItem(
    val budget: BudgetEntity,
    val actualExpense: Double
) {
    val remaining: Double get() = budget.monthlyLimit - actualExpense
    val progressRatio: Float
        get() = if (budget.monthlyLimit <= 0.0) 0f else (actualExpense / budget.monthlyLimit).toFloat()
    val isExceeded: Boolean get() = actualExpense > budget.monthlyLimit
    val isNearLimit: Boolean get() = !isExceeded && progressRatio >= 0.85f
}

data class TransactionFormPreset(
    val section: String = FinanceConstants.SECTION_OFFICE,
    val type: String = FinanceConstants.TYPE_EXPENSE,
    val existingTransaction: TransactionEntity? = null
)
