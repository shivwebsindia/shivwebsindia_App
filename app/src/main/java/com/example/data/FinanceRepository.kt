package com.example.data

import java.util.Calendar
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val dao: FinanceDao) {

    val loggedInUser: Flow<UserEntity?> = dao.getLoggedInUser()

    fun getTransactions(userId: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsForUser(userId)

    fun getCategories(userId: Long): Flow<List<CategoryEntity>> =
        dao.getCategoriesForUser(userId)

    fun getBudgets(userId: Long): Flow<List<BudgetEntity>> =
        dao.getBudgetsForUser(userId)

    fun getRecurringExpenses(userId: Long): Flow<List<RecurringExpenseEntity>> =
        dao.getRecurringExpensesForUser(userId)

    fun getNotifications(userId: Long): Flow<List<AppNotificationEntity>> =
        dao.getNotificationsForUser(userId)

    suspend fun ensureDefaultAccountInitialized() {
        val current = dao.getLoggedInUserOnce()
        if (current != null) {
            ensureCategoriesSeeded(current.id)
            return
        }
        val existingAdmin = dao.getUserByEmail("admin@shivwebsindia.com")
        if (existingAdmin == null) {
            val newUserId = dao.insertUser(
                UserEntity(
                    fullName = "ShivWebsIndia Admin",
                    email = "admin@shivwebsindia.com",
                    passwordHash = "admin123",
                    companyName = "ShivWebsIndia",
                    phone = "+91 98765 43210",
                    role = "Managing Director",
                    isLoggedIn = true
                )
            )
            ensureCategoriesSeeded(newUserId)
            seedSampleFinancialData(newUserId)
        }
    }

    suspend fun ensureCategoriesSeeded(userId: Long) {
        val count = dao.getCategoryCountForUser(userId)
        if (count > 0) return

        val categories = mutableListOf<CategoryEntity>()
        FinanceConstants.DEFAULT_OFFICE_EXPENSE_CATEGORIES.forEach { name ->
            categories.add(
                CategoryEntity(
                    userId = userId,
                    section = FinanceConstants.SECTION_OFFICE,
                    type = FinanceConstants.TYPE_EXPENSE,
                    name = name,
                    isDefault = true
                )
            )
        }
        FinanceConstants.DEFAULT_OFFICE_INCOME_SOURCES.forEach { name ->
            categories.add(
                CategoryEntity(
                    userId = userId,
                    section = FinanceConstants.SECTION_OFFICE,
                    type = FinanceConstants.TYPE_INCOME,
                    name = name,
                    isDefault = true
                )
            )
        }
        FinanceConstants.DEFAULT_HOME_EXPENSE_CATEGORIES.forEach { name ->
            categories.add(
                CategoryEntity(
                    userId = userId,
                    section = FinanceConstants.SECTION_HOME,
                    type = FinanceConstants.TYPE_EXPENSE,
                    name = name,
                    isDefault = true
                )
            )
        }
        FinanceConstants.DEFAULT_HOME_INCOME_SOURCES.forEach { name ->
            categories.add(
                CategoryEntity(
                    userId = userId,
                    section = FinanceConstants.SECTION_HOME,
                    type = FinanceConstants.TYPE_INCOME,
                    name = name,
                    isDefault = true
                )
            )
        }
        dao.insertCategories(categories)
    }

    suspend fun seedSampleFinancialData(userId: Long) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        fun daysAgo(days: Int): Long {
            val c = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, -days)
            }
            return c.timeInMillis
        }

        fun daysAhead(days: Int): Long {
            val c = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, days)
            }
            return c.timeInMillis
        }

        // Seed realistic transactions matching the user prompt example (Office Income ₹1,25,000, Office Expense ₹72,500, Home Income ₹80,000, Home Expense ₹45,000)
        val sampleTransactions = listOf(
            // Office Incomes (Total = ₹1,25,000)
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = daysAgo(0),
                category = "Website Development Project",
                title = "Corporate Portal Milestone",
                clientOrPaidTo = "Apex Global Tech Pvt Ltd",
                description = "Full-stack corporate web portal development & deployment",
                amount = 65000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "INV-SWI-2026-101",
                notes = "Milestone 2 cleared via NEFT",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = daysAgo(2),
                category = "Digital Marketing Retainer",
                title = "Monthly SEO & Google Ads Retainer",
                clientOrPaidTo = "Vanguard Healthcare",
                description = "Monthly digital marketing, SEO optimization & PPC campaign management",
                amount = 38000.0,
                paymentMethod = "UPI",
                referenceNumber = "INV-SWI-2026-102",
                notes = "Paid via Business UPI",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = daysAgo(5),
                category = "Annual Maintenance Contract (AMC)",
                title = "E-Commerce Cloud Hosting & AMC",
                clientOrPaidTo = "Royal Ethnic Wear",
                description = "Annual website maintenance and dedicated server hosting",
                amount = 22000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "INV-SWI-2026-098",
                notes = "Annual renewal settled",
                isDemoData = true
            ),

            // Office Expenses (Total = ₹72,500)
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(0),
                category = "Google Ads",
                title = "Lead Generation Ad Campaign",
                clientOrPaidTo = "Google India Digital Services",
                description = "Search & Display campaign for agency lead generation",
                amount = 15000.0,
                paymentMethod = "Credit Card",
                referenceNumber = "BILL-GADS-8821",
                notes = "Monthly ad spend threshold",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(1),
                category = "Office Rent",
                title = "Commercial Office Suite Rent",
                clientOrPaidTo = "Skyline Business Park LLP",
                description = "Monthly rent for Suite 402 office premises",
                amount = 20000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "RENT-OCT-402",
                notes = "Paid on time",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(3),
                category = "Salary",
                title = "Developer & Designer Payout",
                clientOrPaidTo = "Core Development Team",
                description = "Part salary & bonus disbursement for project sprint",
                amount = 24500.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "PAY-SWI-10",
                notes = "IMPS transfer",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(4),
                category = "Electricity",
                title = "Office Electricity & Power Backup",
                clientOrPaidTo = "State Power Distribution Co",
                description = "Monthly commercial meter bill",
                amount = 5000.0,
                paymentMethod = "UPI",
                referenceNumber = "EB-992014",
                notes = "Auto bill pay",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(6),
                category = "Hosting",
                title = "Cloud VPS & Domain Renewals",
                clientOrPaidTo = "AWS & CloudFlare",
                description = "Production server clusters and SSL renewals",
                amount = 8000.0,
                paymentMethod = "Credit Card",
                referenceNumber = "AWS-INV-4490",
                notes = "Monthly cloud infrastructure",
                isDemoData = true
            ),

            // Home Incomes (Total = ₹80,000)
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = daysAgo(0),
                category = "Monthly Salary / Draw",
                title = "Director Monthly Household Draw",
                clientOrPaidTo = "Self / Personal Account",
                description = "Monthly personal allocation for household management",
                amount = 65000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "HOME-TR-01",
                notes = "Transferred to family savings account",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = daysAgo(4),
                category = "Rental Income",
                title = "Residential Annex Rental",
                clientOrPaidTo = "Mr. R. Sharma",
                description = "Monthly rental income from 2nd floor apartment",
                amount = 15000.0,
                paymentMethod = "UPI",
                referenceNumber = "UPI-41289012",
                notes = "Received via GPay",
                isDemoData = true
            ),

            // Home Expenses (Total = ₹45,000)
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(0),
                category = "Grocery",
                title = "Monthly Supermarket & Organic Provisions",
                clientOrPaidTo = "Reliance Smart & Fresh Mart",
                description = "Monthly staples, fruits, vegetables & household supplies",
                amount = 11500.0,
                paymentMethod = "UPI",
                referenceNumber = "GROC-8812",
                notes = "Includes weekly dairy & fresh produce",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(2),
                category = "School Fees",
                title = "Term Tuition & Activity Fee",
                clientOrPaidTo = "St. Xavier's International School",
                description = "Quarterly tuition and STEM lab fee",
                amount = 16000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "SCH-2026-441",
                notes = "Receipt saved in portal",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(3),
                category = "EMI",
                title = "Home Car Loan EMI",
                clientOrPaidTo = "HDFC Bank Auto Loan",
                description = "Monthly auto loan installment",
                amount = 12000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "EMI-OCT-2026",
                notes = "Auto-debit ECS",
                isDemoData = true
            ),
            TransactionEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = daysAgo(5),
                category = "Electricity",
                title = "Home Electricity & Fiber Internet",
                clientOrPaidTo = "Power Grid & Airtel Fiber",
                description = "Home utility and broadband bill",
                amount = 5500.0,
                paymentMethod = "Debit Card",
                referenceNumber = "UTIL-7731",
                notes = "Paid online",
                isDemoData = true
            )
        )
        dao.insertTransactions(sampleTransactions)

        // Seed sample budgets
        dao.insertBudget(
            BudgetEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                category = "ALL",
                monthlyLimit = 100000.0,
                month = currentMonth,
                year = currentYear
            )
        )
        dao.insertBudget(
            BudgetEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                category = "Google Ads",
                monthlyLimit = 20000.0,
                month = currentMonth,
                year = currentYear
            )
        )
        dao.insertBudget(
            BudgetEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                category = "Office Rent",
                monthlyLimit = 20000.0,
                month = currentMonth,
                year = currentYear
            )
        )
        dao.insertBudget(
            BudgetEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                category = "ALL",
                monthlyLimit = 60000.0,
                month = currentMonth,
                year = currentYear
            )
        )
        dao.insertBudget(
            BudgetEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                category = "Grocery",
                monthlyLimit = 15000.0,
                month = currentMonth,
                year = currentYear
            )
        )

        // Seed recurring expenses
        val recurringItems = listOf(
            RecurringExpenseEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                title = "Office Suite Monthly Rent",
                category = "Office Rent",
                amount = 20000.0,
                frequency = "Monthly",
                paymentMethod = "Bank Transfer",
                paidTo = "Skyline Business Park LLP",
                nextDueDateMillis = daysAhead(3),
                notes = "Due by 5th of every month"
            ),
            RecurringExpenseEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                title = "Cloud Dedicated Server & AWS",
                category = "Hosting",
                amount = 8000.0,
                frequency = "Monthly",
                paymentMethod = "Credit Card",
                paidTo = "AWS Cloud Services",
                nextDueDateMillis = daysAhead(5),
                notes = "Auto-renews monthly"
            ),
            RecurringExpenseEntity(
                userId = userId,
                section = FinanceConstants.SECTION_OFFICE,
                title = "High-Speed Leased Line Internet",
                category = "Internet",
                amount = 3500.0,
                frequency = "Monthly",
                paymentMethod = "UPI",
                paidTo = "Airtel Enterprise",
                nextDueDateMillis = daysAhead(2),
                notes = "300 Mbps symmetrical line"
            ),
            RecurringExpenseEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                title = "Car Loan Monthly EMI",
                category = "EMI",
                amount = 12000.0,
                frequency = "Monthly",
                paymentMethod = "Bank Transfer",
                paidTo = "HDFC Bank",
                nextDueDateMillis = daysAhead(4),
                notes = "ECS auto-debit on 10th"
            ),
            RecurringExpenseEntity(
                userId = userId,
                section = FinanceConstants.SECTION_HOME,
                title = "Family Health Insurance Premium",
                category = "Insurance",
                amount = 18500.0,
                frequency = "Yearly",
                paymentMethod = "Credit Card",
                paidTo = "Star Health Insurance",
                nextDueDateMillis = daysAhead(14),
                notes = "Annual floater policy"
            )
        )
        dao.insertRecurringExpenses(recurringItems)

        // Seed initial notifications
        dao.insertNotification(
            AppNotificationEntity(
                userId = userId,
                title = "Upcoming Recurring Expense",
                message = "High-Speed Leased Line Internet (₹3,500) is due in 2 days.",
                alertType = "RECURRING"
            )
        )
        dao.insertNotification(
            AppNotificationEntity(
                userId = userId,
                title = "Office Rent Budget Reached 100%",
                message = "Office Rent expense (₹20,000) has reached your monthly budget of ₹20,000.",
                alertType = "BUDGET"
            )
        )
        dao.insertNotification(
            AppNotificationEntity(
                userId = userId,
                title = "Monthly Financial Report Ready",
                message = "Your Office & Home combined balance stands at ₹87,500. View or export PDF/CSV in Reports.",
                alertType = "REPORT"
            )
        )
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = dao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalArgumentException("No account found with email: $email"))
        if (user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Invalid password. Please try again."))
        }
        dao.logoutAllUsers()
        dao.setLoggedInUser(user.id)
        ensureCategoriesSeeded(user.id)
        return Result.success(user.copy(isLoggedIn = true))
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        companyName: String,
        phone: String
    ): Result<UserEntity> {
        val existing = dao.getUserByEmail(email.trim())
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }
        dao.logoutAllUsers()
        val newUser = UserEntity(
            fullName = fullName.trim(),
            email = email.trim(),
            passwordHash = password,
            companyName = companyName.trim().ifEmpty { "ShivWebsIndia" },
            phone = phone.trim(),
            role = "Owner / Administrator",
            isLoggedIn = true
        )
        val id = dao.insertUser(newUser)
        ensureCategoriesSeeded(id)
        return Result.success(newUser.copy(id = id))
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val existing = dao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalArgumentException("No registered user found with email $email"))
        dao.updateUser(existing.copy(passwordHash = newPassword))
        return Result.success(Unit)
    }

    suspend fun updateProfile(user: UserEntity) {
        dao.updateUser(user)
    }

    suspend fun logout() {
        dao.logoutAllUsers()
    }

    suspend fun saveTransaction(tx: TransactionEntity): Long {
        return if (tx.id == 0L) {
            dao.insertTransaction(tx)
        } else {
            dao.updateTransaction(tx.copy(updatedAt = System.currentTimeMillis()))
            tx.id
        }
    }

    suspend fun deleteTransaction(tx: TransactionEntity) {
        dao.deleteTransaction(tx)
    }

    suspend fun clearDemoData(userId: Long) {
        dao.clearDemoTransactions(userId)
    }

    suspend fun addCustomCategory(userId: Long, section: String, type: String, name: String) {
        dao.insertCategory(
            CategoryEntity(
                userId = userId,
                section = section,
                type = type,
                name = name.trim(),
                isDefault = false
            )
        )
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        dao.deleteCategory(category)
    }

    suspend fun saveBudget(budget: BudgetEntity) {
        if (budget.id == 0L) {
            dao.insertBudget(budget)
        } else {
            dao.updateBudget(budget)
        }
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        dao.deleteBudget(budget)
    }

    suspend fun saveRecurringExpense(item: RecurringExpenseEntity) {
        if (item.id == 0L) {
            dao.insertRecurringExpense(item)
        } else {
            dao.updateRecurringExpense(item)
        }
    }

    suspend fun deleteRecurringExpense(item: RecurringExpenseEntity) {
        dao.deleteRecurringExpense(item)
    }

    suspend fun addNotification(userId: Long, title: String, message: String, alertType: String) {
        dao.insertNotification(
            AppNotificationEntity(
                userId = userId,
                title = title,
                message = message,
                alertType = alertType
            )
        )
    }

    suspend fun markAllNotificationsRead(userId: Long) {
        dao.markAllNotificationsRead(userId)
    }

    suspend fun deleteNotification(id: Long) {
        dao.deleteNotification(id)
    }
}
