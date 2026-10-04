package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppNotificationEntity
import com.example.data.AppScreen
import com.example.data.BudgetEntity
import com.example.data.BudgetProgressItem
import com.example.data.CategoryBreakdownItem
import com.example.data.CategoryEntity
import com.example.data.DayBreakdownItem
import com.example.data.FinanceConstants
import com.example.data.FinanceRepository
import com.example.data.FinancialSummary
import com.example.data.RecurringExpenseEntity
import com.example.data.ReportType
import com.example.data.SortOption
import com.example.data.TransactionEntity
import com.example.data.TransactionFormPreset
import com.example.data.UserEntity
import com.example.util.CurrencyUtils
import com.example.util.DateFilterOption
import com.example.util.DateUtils
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository =
        FinanceRepository(AppDatabase.getInstance(application).financeDao())

    // Navigation & UI state
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenHistory = ArrayDeque<AppScreen>()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _activeTransactionModal = MutableStateFlow<TransactionFormPreset?>(null)
    val activeTransactionModal: StateFlow<TransactionFormPreset?> = _activeTransactionModal.asStateFlow()

    private val _viewingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val viewingTransaction: StateFlow<TransactionEntity?> = _viewingTransaction.asStateFlow()

    private val _deletingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val deletingTransaction: StateFlow<TransactionEntity?> = _deletingTransaction.asStateFlow()

    private val _showNotificationsSheet = MutableStateFlow(false)
    val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

    // Dashboard Date Filter
    private val _dashboardDateFilter = MutableStateFlow(DateFilterOption.THIS_MONTH)
    val dashboardDateFilter: StateFlow<DateFilterOption> = _dashboardDateFilter.asStateFlow()

    private val _customStartDate = MutableStateFlow<Long?>(null)
    val customStartDate: StateFlow<Long?> = _customStartDate.asStateFlow()

    private val _customEndDate = MutableStateFlow<Long?>(null)
    val customEndDate: StateFlow<Long?> = _customEndDate.asStateFlow()

    // Daily View selected date
    private val _selectedDailyDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDailyDate: StateFlow<Long> = _selectedDailyDate.asStateFlow()

    // Weekly View selected week start (Monday)
    private val _selectedWeekStart = MutableStateFlow(DateUtils.getStartOfWeekMonday(System.currentTimeMillis()))
    val selectedWeekStart: StateFlow<Long> = _selectedWeekStart.asStateFlow()

    // Monthly View selected month & year
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    // All Transactions Search, Filters & Sort
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterSection = MutableStateFlow("ALL") // ALL, OFFICE, HOME
    val filterSection: StateFlow<String> = _filterSection.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // ALL, INCOME, EXPENSE
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    private val _filterPaymentMethod = MutableStateFlow("ALL")
    val filterPaymentMethod: StateFlow<String> = _filterPaymentMethod.asStateFlow()

    private val _filterDateOption = MutableStateFlow(DateFilterOption.ALL_TIME)
    val filterDateOption: StateFlow<DateFilterOption> = _filterDateOption.asStateFlow()

    private val _minAmountFilter = MutableStateFlow("")
    val minAmountFilter: StateFlow<String> = _minAmountFilter.asStateFlow()

    private val _maxAmountFilter = MutableStateFlow("")
    val maxAmountFilter: StateFlow<String> = _maxAmountFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST_FIRST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Reports Screen State
    private val _selectedReportType = MutableStateFlow(ReportType.OFFICE)
    val selectedReportType: StateFlow<ReportType> = _selectedReportType.asStateFlow()

    private val _reportDateFilter = MutableStateFlow(DateFilterOption.THIS_MONTH)
    val reportDateFilter: StateFlow<DateFilterOption> = _reportDateFilter.asStateFlow()

    // Auth error / status
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Logged in user Flow
    val currentUser: StateFlow<UserEntity?> = repository.loggedInUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<TransactionEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getTransactions(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getCategories(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getBudgets(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecurringExpenses: StateFlow<List<RecurringExpenseEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getRecurringExpenses(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotificationEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getNotifications(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard filtered transactions & summary
    val dashboardTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _dashboardDateFilter,
        _customStartDate,
        _customEndDate
    ) { txs, filterOpt, start, end ->
        val (minDate, maxDate) = DateUtils.getRangeForFilter(filterOpt, start, end)
        txs.filter { it.dateMillis in minDate..maxDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardSummary: StateFlow<FinancialSummary> = combine(dashboardTransactions) { (txs) ->
        FinancialSummary.fromTransactions(txs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Overall All-Time Summary
    val overallSummary: StateFlow<FinancialSummary> = combine(allTransactions) { (txs) ->
        FinancialSummary.fromTransactions(txs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Filtered & Sorted Transactions for All Transactions screen
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _searchQuery,
        _filterSection,
        _filterType,
        _filterCategory
    ) { txs, query, sec, type, cat ->
        txs.filter { tx ->
            val matchesSec = sec == "ALL" || tx.section == sec
            val matchesType = type == "ALL" || tx.type == type
            val matchesCat = cat == "ALL" || tx.category.equals(cat, ignoreCase = true)
            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                tx.title.lowercase().contains(q) ||
                tx.description.lowercase().contains(q) ||
                tx.clientOrPaidTo.lowercase().contains(q) ||
                tx.category.lowercase().contains(q) ||
                tx.referenceNumber.lowercase().contains(q) ||
                tx.paymentMethod.lowercase().contains(q) ||
                tx.amount.toLong().toString().contains(q) ||
                DateUtils.formatDate(tx.dateMillis).lowercase().contains(q)
            matchesSec && matchesType && matchesCat && matchesQuery
        }
    }.combine(
        combine(
            _filterPaymentMethod,
            _filterDateOption,
            _minAmountFilter,
            _maxAmountFilter,
            _sortOption
        ) { pay, dateOpt, minAmt, maxAmt, sort ->
            FilterExtra(pay, dateOpt, minAmt, maxAmt, sort)
        }
    ) { list, extra ->
        val (minDate, maxDate) = DateUtils.getRangeForFilter(extra.dateOpt, _customStartDate.value, _customEndDate.value)
        val minVal = extra.minAmt.toDoubleOrNull()
        val maxVal = extra.maxAmt.toDoubleOrNull()
        val filtered = list.filter { tx ->
            val matchesPay = extra.pay == "ALL" || tx.paymentMethod.equals(extra.pay, ignoreCase = true)
            val matchesDate = tx.dateMillis in minDate..maxDate
            val matchesMin = minVal == null || tx.amount >= minVal
            val matchesMax = maxVal == null || tx.amount <= maxVal
            matchesPay && matchesDate && matchesMin && matchesMax
        }
        when (extra.sort) {
            SortOption.NEWEST_FIRST -> filtered.sortedWith(compareByDescending<TransactionEntity> { it.dateMillis }.thenByDescending { it.id })
            SortOption.OLDEST_FIRST -> filtered.sortedWith(compareBy<TransactionEntity> { it.dateMillis }.thenBy { it.id })
            SortOption.HIGHEST_AMOUNT -> filtered.sortedByDescending { it.amount }
            SortOption.LOWEST_AMOUNT -> filtered.sortedBy { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Budgets progress computation
    val budgetProgressList: StateFlow<List<BudgetProgressItem>> = combine(
        allBudgets,
        allTransactions
    ) { budgets, txs ->
        val nowCal = Calendar.getInstance()
        val curMonth = nowCal.get(Calendar.MONTH)
        val curYear = nowCal.get(Calendar.YEAR)
        val (mStart, mEnd) = DateUtils.getMonthRange(curYear, curMonth)
        val monthExpenses = txs.filter {
            it.type == FinanceConstants.TYPE_EXPENSE && it.dateMillis in mStart..mEnd
        }

        budgets.map { b ->
            val actual = monthExpenses
                .filter {
                    it.section == b.section &&
                        (b.category == "ALL" || it.category.equals(b.category, ignoreCase = true))
                }
                .sumOf { it.amount }
            BudgetProgressItem(budget = b, actualExpense = actual)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureDefaultAccountInitialized()
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenHistory.addLast(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (_screenHistory.isNotEmpty()) {
            _currentScreen.value = _screenHistory.removeLast()
            true
        } else if (_currentScreen.value != AppScreen.DASHBOARD) {
            _currentScreen.value = AppScreen.DASHBOARD
            true
        } else {
            false
        }
    }

    fun toggleDarkTheme() {
        _darkTheme.value = !_darkTheme.value
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun openTransactionModal(
        section: String,
        type: String,
        existing: TransactionEntity? = null
    ) {
        _activeTransactionModal.value = TransactionFormPreset(section, type, existing)
    }

    fun closeTransactionModal() {
        _activeTransactionModal.value = null
    }

    fun setViewingTransaction(tx: TransactionEntity?) {
        _viewingTransaction.value = tx
    }

    fun requestDeleteTransaction(tx: TransactionEntity?) {
        _deletingTransaction.value = tx
    }

    fun setShowNotificationsSheet(show: Boolean) {
        _showNotificationsSheet.value = show
    }

    fun setDashboardDateFilter(
        option: DateFilterOption,
        customStart: Long? = null,
        customEnd: Long? = null
    ) {
        _dashboardDateFilter.value = option
        if (customStart != null) _customStartDate.value = customStart
        if (customEnd != null) _customEndDate.value = customEnd
    }

    fun setSelectedDailyDate(millis: Long) {
        _selectedDailyDate.value = millis
    }

    fun setSelectedWeekStart(mondayMillis: Long) {
        _selectedWeekStart.value = DateUtils.getStartOfWeekMonday(mondayMillis)
    }

    fun setSelectedMonthYear(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setFilterSection(sec: String) { _filterSection.value = sec }
    fun setFilterType(t: String) { _filterType.value = t }
    fun setFilterCategory(c: String) { _filterCategory.value = c }
    fun setFilterPaymentMethod(p: String) { _filterPaymentMethod.value = p }
    fun setFilterDateOption(opt: DateFilterOption) { _filterDateOption.value = opt }
    fun setMinAmountFilter(v: String) { _minAmountFilter.value = v }
    fun setMaxAmountFilter(v: String) { _maxAmountFilter.value = v }
    fun setSortOption(s: SortOption) { _sortOption.value = s }

    fun resetAllFilters() {
        _searchQuery.value = ""
        _filterSection.value = "ALL"
        _filterType.value = "ALL"
        _filterCategory.value = "ALL"
        _filterPaymentMethod.value = "ALL"
        _filterDateOption.value = DateFilterOption.ALL_TIME
        _minAmountFilter.value = ""
        _maxAmountFilter.value = ""
        _sortOption.value = SortOption.NEWEST_FIRST
    }

    fun setSelectedReportType(type: ReportType) {
        _selectedReportType.value = type
    }

    fun setReportDateFilter(opt: DateFilterOption) {
        _reportDateFilter.value = opt
    }

    // Auth operations
    fun clearAuthError() {
        _authError.value = null
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.login(email, password)
            res.onSuccess { user ->
                _currentScreen.value = AppScreen.DASHBOARD
                showToast("Welcome back, ${user.fullName}!")
            }.onFailure { err ->
                _authError.value = err.message ?: "Login failed"
            }
        }
    }

    fun register(fullName: String, email: String, password: String, companyName: String, phone: String) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.register(fullName, email, password, companyName, phone)
            res.onSuccess { user ->
                _currentScreen.value = AppScreen.DASHBOARD
                showToast("Account created for ${user.fullName}!")
            }.onFailure { err ->
                _authError.value = err.message ?: "Registration failed"
            }
        }
    }

    fun resetPassword(email: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.resetPassword(email, newPassword)
            res.onSuccess {
                showToast("Password updated! You can now log in.")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "Password reset failed"
            }
        }
    }

    fun updateProfile(fullName: String, companyName: String, phone: String, role: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                user.copy(
                    fullName = fullName.trim(),
                    companyName = companyName.trim(),
                    phone = phone.trim(),
                    role = role.trim()
                )
            )
            showToast("Profile updated successfully")
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user = currentUser.value ?: return
        if (user.passwordHash != currentPassword) {
            showToast("Current password is incorrect")
            return
        }
        viewModelScope.launch {
            repository.updateProfile(user.copy(passwordHash = newPassword))
            showToast("Password changed successfully")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _screenHistory.clear()
            _currentScreen.value = AppScreen.DASHBOARD
            showToast("Logged out securely")
        }
    }

    // Transaction CRUD
    fun saveTransaction(
        existingId: Long = 0L,
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
        attachmentUri: String? = null,
        attachmentName: String? = null
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = existingId,
                userId = user.id,
                section = section,
                type = type,
                dateMillis = dateMillis,
                category = category.trim(),
                title = title.trim().ifEmpty { category.trim() },
                clientOrPaidTo = clientOrPaidTo.trim(),
                description = description.trim(),
                amount = amount,
                paymentMethod = paymentMethod,
                referenceNumber = referenceNumber.trim(),
                notes = notes.trim(),
                attachmentUri = attachmentUri,
                attachmentName = attachmentName,
                isDemoData = false
            )
            repository.saveTransaction(tx)
            _activeTransactionModal.value = null

            // Automatic Alert & Notification Checks
            if (type == FinanceConstants.TYPE_EXPENSE && amount >= 25000.0) {
                repository.addNotification(
                    userId = user.id,
                    title = "High-Value ${section.lowercase().replaceFirstChar { it.uppercase() }} Expense Recorded",
                    message = "${tx.title} (${tx.category}) of ${CurrencyUtils.formatInr(amount)} was added.",
                    alertType = "HIGH_VALUE"
                )
            }

            // Check budget threshold
            if (type == FinanceConstants.TYPE_EXPENSE) {
                val matchingBudgets = allBudgets.value.filter {
                    it.section == section && (it.category == "ALL" || it.category.equals(category, ignoreCase = true))
                }
                matchingBudgets.forEach { b ->
                    val currentSpent = allTransactions.value
                        .filter {
                            it.section == section &&
                                it.type == FinanceConstants.TYPE_EXPENSE &&
                                (b.category == "ALL" || it.category.equals(b.category, ignoreCase = true))
                        }
                        .sumOf { it.amount } + (if (existingId == 0L) amount else 0.0)
                    if (currentSpent > b.monthlyLimit) {
                        val bLabel = if (b.category == "ALL") "$section Monthly Budget" else "$section ${b.category} Budget"
                        repository.addNotification(
                            userId = user.id,
                            title = "Budget Alert: $bLabel Exceeded!",
                            message = "Spent ${CurrencyUtils.formatInr(currentSpent)} against budget of ${CurrencyUtils.formatInr(b.monthlyLimit)}.",
                            alertType = "BUDGET"
                        )
                    }
                }
            }

            val actionLabel = if (existingId == 0L) "Added" else "Updated"
            showToast("$actionLabel $section $type: ${CurrencyUtils.formatInr(amount)}")
        }
    }

    fun duplicateTransaction(tx: TransactionEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val copyTx = tx.copy(
                id = 0L,
                userId = user.id,
                title = "${tx.title} (Copy)",
                dateMillis = System.currentTimeMillis(),
                isDemoData = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveTransaction(copyTx)
            showToast("Transaction duplicated (${CurrencyUtils.formatInr(tx.amount)})")
        }
    }

    fun confirmDeleteTransaction() {
        val tx = _deletingTransaction.value ?: return
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            _deletingTransaction.value = null
            if (_viewingTransaction.value?.id == tx.id) {
                _viewingTransaction.value = null
            }
            showToast("Deleted transaction: ${tx.title}")
        }
    }

    fun clearDemoTransactions() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.clearDemoData(user.id)
            showToast("Demo data cleared. Only your personal records remain.")
        }
    }

    fun loadDemoTransactions() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.seedSampleFinancialData(user.id)
            showToast("Sample Office & Home financial records loaded.")
        }
    }

    // Category Management
    fun addCategory(section: String, type: String, name: String) {
        val user = currentUser.value ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addCustomCategory(user.id, section, type, name)
            showToast("Category '${name.trim()}' added to $section $type")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            showToast("Category '${category.name}' removed")
        }
    }

    // Budget Management
    fun saveBudget(existingId: Long = 0L, section: String, category: String, monthlyLimit: Double) {
        val user = currentUser.value ?: return
        val cal = Calendar.getInstance()
        viewModelScope.launch {
            repository.saveBudget(
                BudgetEntity(
                    id = existingId,
                    userId = user.id,
                    section = section,
                    category = category,
                    monthlyLimit = monthlyLimit,
                    month = cal.get(Calendar.MONTH),
                    year = cal.get(Calendar.YEAR)
                )
            )
            showToast("Saved $section Budget (${if (category == "ALL") "Overall Monthly" else category}): ${CurrencyUtils.formatInr(monthlyLimit)}")
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            showToast("Budget removed")
        }
    }

    // Recurring Expense Management
    fun saveRecurringExpense(
        existingId: Long = 0L,
        section: String,
        title: String,
        category: String,
        amount: Double,
        frequency: String,
        paymentMethod: String,
        paidTo: String,
        nextDueDateMillis: Long,
        notes: String
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.saveRecurringExpense(
                RecurringExpenseEntity(
                    id = existingId,
                    userId = user.id,
                    section = section,
                    title = title.trim(),
                    category = category,
                    amount = amount,
                    frequency = frequency,
                    paymentMethod = paymentMethod,
                    paidTo = paidTo.trim(),
                    nextDueDateMillis = nextDueDateMillis,
                    notes = notes.trim()
                )
            )
            showToast("Recurring expense '${title.trim()}' saved")
        }
    }

    fun payRecurringExpenseNow(item: RecurringExpenseEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            // 1. Record as actual transaction
            repository.saveTransaction(
                TransactionEntity(
                    userId = user.id,
                    section = item.section,
                    type = FinanceConstants.TYPE_EXPENSE,
                    dateMillis = System.currentTimeMillis(),
                    category = item.category,
                    title = item.title,
                    clientOrPaidTo = item.paidTo,
                    description = "Recurring ${item.frequency} payment: ${item.title}",
                    amount = item.amount,
                    paymentMethod = item.paymentMethod,
                    referenceNumber = "REC-${System.currentTimeMillis() % 100000}",
                    notes = item.notes
                )
            )
            // 2. Advance next due date
            val cal = Calendar.getInstance().apply { timeInMillis = item.nextDueDateMillis }
            when (item.frequency) {
                "Daily" -> cal.add(Calendar.DAY_OF_YEAR, 1)
                "Weekly" -> cal.add(Calendar.DAY_OF_YEAR, 7)
                "Monthly" -> cal.add(Calendar.MONTH, 1)
                "Yearly" -> cal.add(Calendar.YEAR, 1)
                else -> cal.add(Calendar.MONTH, 1)
            }
            repository.saveRecurringExpense(item.copy(nextDueDateMillis = cal.timeInMillis))
            showToast("Recorded ${CurrencyUtils.formatInr(item.amount)} for '${item.title}' & advanced next due date")
        }
    }

    fun deleteRecurringExpense(item: RecurringExpenseEntity) {
        viewModelScope.launch {
            repository.deleteRecurringExpense(item)
            showToast("Recurring expense removed")
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id)
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    // Helper analytics functions
    fun getCategoryBreakdown(
        transactions: List<TransactionEntity>,
        section: String?,
        type: String = FinanceConstants.TYPE_EXPENSE
    ): List<CategoryBreakdownItem> {
        val filtered = transactions.filter {
            (section == null || it.section == section) && it.type == type
        }
        val total = filtered.sumOf { it.amount }
        if (total <= 0.0) return emptyList()

        return filtered
            .groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                CategoryBreakdownItem(
                    category = cat,
                    section = section ?: list.first().section,
                    type = type,
                    amount = sum,
                    percentage = ((sum / total) * 100.0).toFloat(),
                    count = list.size
                )
            }
            .sortedByDescending { it.amount }
    }

    fun getWeeklyDayBreakdown(weekStartMondayMillis: Long, transactions: List<TransactionEntity>): List<DayBreakdownItem> {
        val result = mutableListOf<DayBreakdownItem>()
        val cal = Calendar.getInstance().apply { timeInMillis = DateUtils.startOfDay(weekStartMondayMillis) }

        for (i in 0..6) {
            val dayStart = DateUtils.startOfDay(cal.timeInMillis)
            val dayEnd = DateUtils.endOfDay(cal.timeInMillis)
            val dayTxs = transactions.filter { it.dateMillis in dayStart..dayEnd }
            val sum = FinancialSummary.fromTransactions(dayTxs)

            result.add(
                DayBreakdownItem(
                    dayName = DateUtils.weekDayNames[i],
                    dateLabel = DateUtils.formatShortDate(dayStart),
                    dateMillis = dayStart,
                    officeIncome = sum.officeIncome,
                    officeExpense = sum.officeExpense,
                    homeIncome = sum.homeIncome,
                    homeExpense = sum.homeExpense
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }
}

private data class FilterExtra(
    val pay: String,
    val dateOpt: DateFilterOption,
    val minAmt: String,
    val maxAmt: String,
    val sort: SortOption
)
