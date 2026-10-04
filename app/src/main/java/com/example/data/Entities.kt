package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object FinanceConstants {
    const val SECTION_OFFICE = "OFFICE"
    const val SECTION_HOME = "HOME"

    const val TYPE_INCOME = "INCOME"
    const val TYPE_EXPENSE = "EXPENSE"

    val PAYMENT_METHODS = listOf(
        "Cash",
        "Bank Transfer",
        "UPI",
        "Credit Card",
        "Debit Card",
        "Other"
    )

    val DEFAULT_OFFICE_EXPENSE_CATEGORIES = listOf(
        "Office Rent",
        "Electricity",
        "Internet",
        "Telephone",
        "Salary",
        "Staff Expense",
        "Tea & Snacks",
        "Stationery",
        "Software",
        "Hosting",
        "Domain",
        "Advertising",
        "Google Ads",
        "Meta Ads",
        "Travel",
        "Transportation",
        "Maintenance",
        "Office Equipment",
        "Computer/Laptop",
        "Printing",
        "Marketing",
        "Client Meeting",
        "Miscellaneous"
    )

    val DEFAULT_OFFICE_INCOME_SOURCES = listOf(
        "Website Development Project",
        "Digital Marketing Retainer",
        "SEO & Google Ads Management",
        "Mobile App Development",
        "UI/UX Design Consultation",
        "Annual Maintenance Contract (AMC)",
        "Hosting & Domain Renewal",
        "E-Commerce Solution",
        "Brand Consulting",
        "Other Office Income"
    )

    val DEFAULT_HOME_EXPENSE_CATEGORIES = listOf(
        "Grocery",
        "Electricity",
        "Water",
        "Gas",
        "Rent",
        "School Fees",
        "Education",
        "Medical",
        "Medicine",
        "Transportation",
        "Fuel",
        "Shopping",
        "Clothing",
        "Mobile",
        "Internet",
        "Household",
        "Entertainment",
        "Food",
        "Travel",
        "EMI",
        "Insurance",
        "Family",
        "Miscellaneous"
    )

    val DEFAULT_HOME_INCOME_SOURCES = listOf(
        "Monthly Salary / Draw",
        "Business Dividend",
        "Rental Income",
        "Investment Returns",
        "Freelance / Side Project",
        "Fixed Deposit Interest",
        "Family Contribution",
        "Other Home Income"
    )

    val FREQUENCIES = listOf("Daily", "Weekly", "Monthly", "Yearly")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val companyName: String = "ShivWebsIndia",
    val phone: String = "+91 98765 43210",
    val role: String = "Administrator",
    val isLoggedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val section: String, // "OFFICE" or "HOME"
    val type: String, // "INCOME" or "EXPENSE"
    val dateMillis: Long,
    val category: String, // Expense Category or Income Source
    val title: String, // Expense Title or Income Source Title
    val clientOrPaidTo: String, // Client Name (Office Income) or Paid To (Expense)
    val description: String,
    val amount: Double,
    val paymentMethod: String, // Cash, Bank Transfer, UPI, Credit Card, Debit Card, Other
    val referenceNumber: String, // Reference Number or Bill/Invoice Number
    val notes: String,
    val attachmentUri: String? = null,
    val attachmentName: String? = null,
    val isDemoData: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val section: String, // "OFFICE" or "HOME"
    val type: String, // "EXPENSE" or "INCOME"
    val name: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val section: String, // "OFFICE" or "HOME"
    val category: String, // "ALL" for overall monthly section budget, or specific category name
    val monthlyLimit: Double,
    val month: Int, // 0..11
    val year: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val section: String, // "OFFICE" or "HOME"
    val title: String,
    val category: String,
    val amount: Double,
    val frequency: String, // "Daily", "Weekly", "Monthly", "Yearly"
    val paymentMethod: String,
    val paidTo: String,
    val nextDueDateMillis: Long,
    val isActive: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val alertType: String, // "BUDGET", "RECURRING", "HIGH_VALUE", "LOW_BALANCE", "REPORT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
