package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppNotificationEntity
import com.example.data.AppScreen
import com.example.data.FinanceConstants
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.ShivWebsIndiaLogoBadge
import com.example.ui.components.TransactionDetailsDialog
import com.example.ui.components.TransactionFormDialog
import com.example.ui.screens.AllTransactionsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.CategoriesManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DailyViewScreen
import com.example.ui.screens.MonthlyViewScreen
import com.example.ui.screens.ProfileAndSettingsScreen
import com.example.ui.screens.RecurringExpensesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SectionFinanceScreen
import com.example.ui.screens.WeeklyViewScreen
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkBlueSurface
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.util.DateUtils
import com.example.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val financeViewModel: FinanceViewModel = viewModel()
            val darkTheme by financeViewModel.darkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = darkTheme) {
                SmartExpenseApp(viewModel = financeViewModel)
            }
        }
    }
}

private data class NavMenuEntry(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

private val sidebarMenuEntries = listOf(
    NavMenuEntry(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    NavMenuEntry(AppScreen.OFFICE_FINANCE, "Office Finance", Icons.Default.Business),
    NavMenuEntry(AppScreen.HOME_FINANCE, "Home Finance", Icons.Default.Home),
    NavMenuEntry(AppScreen.TRANSACTIONS, "Transactions", Icons.AutoMirrored.Filled.ReceiptLong),
    NavMenuEntry(AppScreen.DAILY_REPORT, "Daily Report", Icons.Default.Today),
    NavMenuEntry(AppScreen.WEEKLY_REPORT, "Weekly Report", Icons.Default.DateRange),
    NavMenuEntry(AppScreen.MONTHLY_REPORT, "Monthly Report", Icons.Default.CalendarMonth),
    NavMenuEntry(AppScreen.BUDGETS, "Budgets", Icons.Default.AccountBalanceWallet),
    NavMenuEntry(AppScreen.RECURRING, "Recurring Expenses", Icons.Default.Autorenew),
    NavMenuEntry(AppScreen.CATEGORIES, "Categories", Icons.Default.Category),
    NavMenuEntry(AppScreen.REPORTS, "Reports", Icons.Default.Assessment),
    NavMenuEntry(AppScreen.SETTINGS_PROFILE, "Profile & Settings", Icons.Default.Person)
)

private val bottomNavEntries = listOf(
    NavMenuEntry(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    NavMenuEntry(AppScreen.OFFICE_FINANCE, "Office", Icons.Default.Business),
    NavMenuEntry(AppScreen.HOME_FINANCE, "Home", Icons.Default.Home),
    NavMenuEntry(AppScreen.TRANSACTIONS, "Ledger", Icons.AutoMirrored.Filled.ReceiptLong),
    NavMenuEntry(AppScreen.REPORTS, "Reports", Icons.Default.Assessment)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartExpenseApp(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val dashboardTransactions by viewModel.dashboardTransactions.collectAsStateWithLifecycle()
    val dashboardSummary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val overallSummary by viewModel.overallSummary.collectAsStateWithLifecycle()
    val dashboardDateFilter by viewModel.dashboardDateFilter.collectAsStateWithLifecycle()

    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val budgetProgressList by viewModel.budgetProgressList.collectAsStateWithLifecycle()
    val recurringExpenses by viewModel.allRecurringExpenses.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val selectedDailyDate by viewModel.selectedDailyDate.collectAsStateWithLifecycle()
    val selectedWeekStart by viewModel.selectedWeekStart.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterSection by viewModel.filterSection.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val filterPaymentMethod by viewModel.filterPaymentMethod.collectAsStateWithLifecycle()
    val filterDateOption by viewModel.filterDateOption.collectAsStateWithLifecycle()
    val minAmount by viewModel.minAmountFilter.collectAsStateWithLifecycle()
    val maxAmount by viewModel.maxAmountFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    val selectedReportType by viewModel.selectedReportType.collectAsStateWithLifecycle()
    val reportDateFilter by viewModel.reportDateFilter.collectAsStateWithLifecycle()

    val activeTransactionModal by viewModel.activeTransactionModal.collectAsStateWithLifecycle()
    val viewingTransaction by viewModel.viewingTransaction.collectAsStateWithLifecycle()
    val deletingTransaction by viewModel.deletingTransaction.collectAsStateWithLifecycle()
    val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle system back button when on secondary screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateBack()
    }

    if (currentUser == null) {
        AuthScreen(
            authError = authError,
            onClearError = { viewModel.clearAuthError() },
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onRegister = { name, email, pass, comp, phone ->
                viewModel.register(name, email, pass, comp, phone)
            },
            onResetPassword = { email, newPass, onSuccess ->
                viewModel.resetPassword(email, newPass, onSuccess)
            }
        )
        return
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val unreadCount = notifications.count { !it.isRead }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 840.dp

        val mainScaffoldContent: @Composable () -> Unit = {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_shivwebs_logo),
                                    contentDescription = "ShivWebsIndia Logo",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column {
                                    Text(
                                        text = currentScreen.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Smart Expense & Income Manager • ShivWebsIndia",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = BrandOrange,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            if (!isExpandedScreen) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch { drawerState.open() }
                                    },
                                    modifier = Modifier.testTag("btn_open_drawer")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.setShowNotificationsSheet(true) },
                                modifier = Modifier.testTag("btn_notifications")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(containerColor = BrandOrange) {
                                                Text(
                                                    text = unreadCount.toString(),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.navigateTo(AppScreen.SETTINGS_PROFILE) },
                                modifier = Modifier.testTag("btn_top_profile")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Profile",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BrandDarkBlue,
                            titleContentColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    if (!isExpandedScreen) {
                        NavigationBar(
                            containerColor = BrandDarkBlue,
                            contentColor = Color.White,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            bottomNavEntries.forEach { item ->
                                val selected = currentScreen == item.screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.navigateTo(item.screen) },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = BrandOrange,
                                        indicatorColor = BrandOrange,
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("bottom_nav_${item.screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            val defaultSec = if (currentScreen == AppScreen.HOME_FINANCE) {
                                FinanceConstants.SECTION_HOME
                            } else {
                                FinanceConstants.SECTION_OFFICE
                            }
                            viewModel.openTransactionModal(defaultSec, FinanceConstants.TYPE_EXPENSE)
                        },
                        containerColor = BrandOrange,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_transaction")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Quick Add Transaction")
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            user = currentUser,
                            summary = dashboardSummary,
                            transactions = dashboardTransactions,
                            allTransactions = allTransactions,
                            dateFilter = dashboardDateFilter,
                            budgetProgressList = budgetProgressList,
                            recurringExpenses = recurringExpenses
                        )
                        AppScreen.OFFICE_FINANCE -> SectionFinanceScreen(
                            section = FinanceConstants.SECTION_OFFICE,
                            viewModel = viewModel,
                            allTransactions = allTransactions,
                            allCategories = allCategories
                        )
                        AppScreen.HOME_FINANCE -> SectionFinanceScreen(
                            section = FinanceConstants.SECTION_HOME,
                            viewModel = viewModel,
                            allTransactions = allTransactions,
                            allCategories = allCategories
                        )
                        AppScreen.TRANSACTIONS -> AllTransactionsScreen(
                            viewModel = viewModel,
                            filteredTransactions = filteredTransactions,
                            searchQuery = searchQuery,
                            filterSection = filterSection,
                            filterType = filterType,
                            filterCategory = filterCategory,
                            filterPaymentMethod = filterPaymentMethod,
                            filterDateOption = filterDateOption,
                            minAmount = minAmount,
                            maxAmount = maxAmount,
                            sortOption = sortOption
                        )
                        AppScreen.DAILY_REPORT -> DailyViewScreen(
                            viewModel = viewModel,
                            selectedDateMillis = selectedDailyDate,
                            allTransactions = allTransactions
                        )
                        AppScreen.WEEKLY_REPORT -> WeeklyViewScreen(
                            viewModel = viewModel,
                            weekStartMondayMillis = selectedWeekStart,
                            allTransactions = allTransactions
                        )
                        AppScreen.MONTHLY_REPORT -> MonthlyViewScreen(
                            viewModel = viewModel,
                            selectedMonth = selectedMonth,
                            selectedYear = selectedYear,
                            allTransactions = allTransactions
                        )
                        AppScreen.BUDGETS -> BudgetsScreen(
                            viewModel = viewModel,
                            budgetProgressList = budgetProgressList,
                            allCategories = allCategories,
                            overallSummary = overallSummary
                        )
                        AppScreen.RECURRING -> RecurringExpensesScreen(
                            viewModel = viewModel,
                            recurringExpenses = recurringExpenses,
                            allCategories = allCategories,
                            overallSummary = overallSummary
                        )
                        AppScreen.CATEGORIES -> CategoriesManagementScreen(
                            viewModel = viewModel,
                            allCategories = allCategories,
                            overallSummary = overallSummary
                        )
                        AppScreen.REPORTS -> ReportsScreen(
                            viewModel = viewModel,
                            selectedReportType = selectedReportType,
                            reportDateFilter = reportDateFilter,
                            allTransactions = allTransactions
                        )
                        AppScreen.SETTINGS_PROFILE -> ProfileAndSettingsScreen(
                            viewModel = viewModel,
                            user = currentUser,
                            darkTheme = darkTheme
                        )
                    }
                }
            }
        }

        if (isExpandedScreen) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(270.dp),
                        drawerContainerColor = BrandDarkBlue
                    ) {
                        SidebarContent(
                            currentScreen = currentScreen,
                            onSelectScreen = { viewModel.navigateTo(it) },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }
            ) {
                mainScaffoldContent()
            }
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(285.dp),
                        drawerContainerColor = BrandDarkBlue
                    ) {
                        SidebarContent(
                            currentScreen = currentScreen,
                            onSelectScreen = { screen ->
                                viewModel.navigateTo(screen)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onLogout = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.logout()
                            }
                        )
                    }
                }
            ) {
                mainScaffoldContent()
            }
        }
    }

    // Modals & Dialogs
    activeTransactionModal?.let { preset ->
        TransactionFormDialog(
            preset = preset,
            categories = allCategories,
            onDismiss = { viewModel.closeTransactionModal() },
            onSave = { id, sec, type, date, cat, title, client, desc, amt, pm, ref, notes, uri, name ->
                viewModel.saveTransaction(
                    existingId = id,
                    section = sec,
                    type = type,
                    dateMillis = date,
                    category = cat,
                    title = title,
                    clientOrPaidTo = client,
                    description = desc,
                    amount = amt,
                    paymentMethod = pm,
                    referenceNumber = ref,
                    notes = notes,
                    attachmentUri = uri,
                    attachmentName = name
                )
            }
        )
    }

    viewingTransaction?.let { tx ->
        TransactionDetailsDialog(
            tx = tx,
            onDismiss = { viewModel.setViewingTransaction(null) },
            onEdit = {
                viewModel.setViewingTransaction(null)
                viewModel.openTransactionModal(tx.section, tx.type, tx)
            },
            onDelete = { viewModel.requestDeleteTransaction(tx) }
        )
    }

    deletingTransaction?.let { tx ->
        DeleteConfirmationDialog(
            transaction = tx,
            onDismiss = { viewModel.requestDeleteTransaction(null) },
            onConfirmDelete = { viewModel.confirmDeleteTransaction() }
        )
    }

    if (showNotificationsSheet) {
        NotificationsBottomSheet(
            notifications = notifications,
            onDismiss = { viewModel.setShowNotificationsSheet(false) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onDeleteNotification = { viewModel.deleteNotification(it) }
        )
    }
}

@Composable
private fun SidebarContent(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        // Sidebar Brand Header with ShivWebsIndia Logo
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            ShivWebsIndiaLogoBadge(darkSurface = true)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Smart Expense & Income Manager",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White
            )
            Text(
                text = "Designed & Developed by ShivWebsIndia",
                style = MaterialTheme.typography.labelSmall,
                color = BrandOrange
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.14f))
        Spacer(modifier = Modifier.height(10.dp))

        sidebarMenuEntries.forEach { item ->
            val selected = currentScreen == item.screen
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                selected = selected,
                onClick = { onSelectScreen(item.screen) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = BrandOrange,
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = Color(0xFFCBD5E1),
                    unselectedTextColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .testTag("drawer_item_${item.screen.name.lowercase()}")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.14f))
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout"
                )
            },
            selected = false,
            onClick = onLogout,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = BrandDarkRed.copy(alpha = 0.25f),
                unselectedIconColor = Color(0xFFF87171),
                unselectedTextColor = Color(0xFFF87171)
            ),
            modifier = Modifier.testTag("drawer_item_logout")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsBottomSheet(
    notifications: List<AppNotificationEntity>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onDeleteNotification: (Long) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Alerts & Notifications (${notifications.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                TextButton(onClick = onMarkAllRead) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark All Read", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All caught up! No active alerts.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { n ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (n.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            else BrandOrange.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = n.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = n.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = DateUtils.formatDateTime(n.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandOrange
                                    )
                                }
                                IconButton(onClick = { onDeleteNotification(n.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
