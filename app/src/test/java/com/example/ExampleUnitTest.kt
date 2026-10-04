package com.example

import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.TransactionEntity
import com.example.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun indianCurrencyFormatting_isCorrect() {
        assertEquals("₹5,000", CurrencyUtils.formatInr(5000.0))
        assertEquals("₹25,000", CurrencyUtils.formatInr(25000.0))
        assertEquals("₹1,25,000", CurrencyUtils.formatInr(125000.0))
        assertEquals("₹10,50,000", CurrencyUtils.formatInr(1050000.0))
    }

    @Test
    fun automaticFinancialCalculations_areStrictlyAccurate() {
        val sample = listOf(
            TransactionEntity(
                userId = 1L,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = 1000L,
                category = "Website Development",
                title = "Portal",
                clientOrPaidTo = "Client A",
                description = "",
                amount = 125000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "INV-1",
                notes = ""
            ),
            TransactionEntity(
                userId = 1L,
                section = FinanceConstants.SECTION_OFFICE,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = 1000L,
                category = "Office Rent",
                title = "Rent",
                clientOrPaidTo = "Landlord",
                description = "",
                amount = 72500.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "RENT-1",
                notes = ""
            ),
            TransactionEntity(
                userId = 1L,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_INCOME,
                dateMillis = 1000L,
                category = "Salary",
                title = "Draw",
                clientOrPaidTo = "Self",
                description = "",
                amount = 80000.0,
                paymentMethod = "Bank Transfer",
                referenceNumber = "SAL-1",
                notes = ""
            ),
            TransactionEntity(
                userId = 1L,
                section = FinanceConstants.SECTION_HOME,
                type = FinanceConstants.TYPE_EXPENSE,
                dateMillis = 1000L,
                category = "Grocery",
                title = "Grocery",
                clientOrPaidTo = "Mart",
                description = "",
                amount = 45000.0,
                paymentMethod = "UPI",
                referenceNumber = "GR-1",
                notes = ""
            )
        )

        val summary = FinancialSummary.fromTransactions(sample)
        assertEquals(125000.0, summary.officeIncome, 0.01)
        assertEquals(72500.0, summary.officeExpense, 0.01)
        assertEquals(52500.0, summary.officeBalance, 0.01)
        assertEquals(80000.0, summary.homeIncome, 0.01)
        assertEquals(45000.0, summary.homeExpense, 0.01)
        assertEquals(35000.0, summary.homeBalance, 0.01)
        assertEquals(205000.0, summary.combinedIncome, 0.01)
        assertEquals(117500.0, summary.combinedExpense, 0.01)
        assertEquals(87500.0, summary.combinedBalance, 0.01)
    }
}
