package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Users
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUserOnce(): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedInUser(userId: Long)

    // Transactions
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY dateMillis DESC, id DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id AND userId = :userId")
    suspend fun deleteTransactionById(id: Long, userId: Long)

    @Query("DELETE FROM transactions WHERE userId = :userId AND isDemoData = 1")
    suspend fun clearDemoTransactions(userId: Long)

    // Categories
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY isDefault DESC, name ASC")
    fun getCategoriesForUser(userId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId")
    suspend fun getCategoryCountForUser(userId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // Budgets
    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY section ASC, category ASC")
    fun getBudgetsForUser(userId: Long): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // Recurring Expenses
    @Query("SELECT * FROM recurring_expenses WHERE userId = :userId ORDER BY nextDueDateMillis ASC")
    fun getRecurringExpensesForUser(userId: Long): Flow<List<RecurringExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringExpense(item: RecurringExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringExpenses(items: List<RecurringExpenseEntity>)

    @Update
    suspend fun updateRecurringExpense(item: RecurringExpenseEntity)

    @Delete
    suspend fun deleteRecurringExpense(item: RecurringExpenseEntity)

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}

@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        RecurringExpenseEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_expense_manager.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
