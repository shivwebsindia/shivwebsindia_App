package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CategoryBreakdownItem
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.TransactionEntity
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateFilterOption
import com.example.util.DateUtils

val ChartColorPalette = listOf(
    Color(0xFFFF6A00), // Orange
    Color(0xFF0A2540), // Dark Blue
    Color(0xFF8B1E1E), // Dark Red
    Color(0xFF0E7C4D), // Emerald Green
    Color(0xFF2563EB), // Royal Blue
    Color(0xFFD97706), // Amber
    Color(0xFF7C3AED), // Violet
    Color(0xFF0891B2)  // Cyan
)

@Composable
fun ShivWebsIndiaLogoBadge(
    modifier: Modifier = Modifier,
    darkSurface: Boolean = true,
    compact: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_shivwebs_logo),
            contentDescription = "ShivWebsIndia Logo",
            modifier = Modifier
                .size(if (compact) 32.dp else 40.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = "SHIVWEBSINDIA",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp
                ),
                color = if (darkSurface) BrandOrange else BrandDarkBlue
            )
            if (!compact) {
                Text(
                    text = "Website Design | Web Development | Digital Marketing",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (darkSurface) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShivWebsIndiaFooter(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
            .testTag("shivwebsindia_footer"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_shivwebs_logo),
                    contentDescription = "ShivWebsIndia Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Designed & Developed by ShivWebsIndia",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Website Design | Web Development | Digital Marketing",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "© 2026 Smart Expense & Income Manager",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BrandOrange.copy(alpha = 0.18f),
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shivwebsindia.com"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Website",
                            tint = BrandOrange,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "www.shivwebsindia.com",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTopSummaryStrip(
    title: String,
    subtitle: String,
    income: Double,
    expense: Double,
    balance: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMetricPill(
                    label = "Income",
                    amount = income,
                    accentColor = Color(0xFF34D399),
                    prefix = "+",
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricPill(
                    label = "Expense",
                    amount = expense,
                    accentColor = Color(0xFFF87171),
                    prefix = "-",
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricPill(
                    label = "Balance",
                    amount = balance,
                    accentColor = if (balance >= 0) BrandOrange else Color(0xFFF87171),
                    prefix = "",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricPill(
    label: String,
    amount: Double,
    accentColor: Color,
    prefix: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp
                ),
                color = Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (prefix.isNotEmpty() && amount > 0) "$prefix${CurrencyUtils.formatInr(amount)}"
                else CurrencyUtils.formatInr(amount),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionButtonsRow(
    onAddOfficeIncome: () -> Unit,
    onAddOfficeExpense: () -> Unit,
    onAddHomeIncome: () -> Unit,
    onAddHomeExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 2
    ) {
        QuickActionButton(
            text = "+ Add Office Income",
            containerColor = BrandDarkBlue,
            contentColor = Color.White,
            tag = "btn_quick_office_income",
            onClick = onAddOfficeIncome,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            text = "+ Add Office Expense",
            containerColor = BrandOrange,
            contentColor = Color.White,
            tag = "btn_quick_office_expense",
            onClick = onAddOfficeExpense,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            text = "+ Add Home Income",
            containerColor = IncomeGreen,
            contentColor = Color.White,
            tag = "btn_quick_home_income",
            onClick = onAddHomeIncome,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            text = "+ Add Home Expense",
            containerColor = BrandDarkRed,
            contentColor = Color.White,
            tag = "btn_quick_home_expense",
            onClick = onAddHomeExpense,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .testTag(tag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DateFilterRow(
    selectedOption: DateFilterOption,
    onSelectOption: (DateFilterOption) -> Unit,
    onOpenCustomPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        DateFilterOption.TODAY,
        DateFilterOption.YESTERDAY,
        DateFilterOption.THIS_WEEK,
        DateFilterOption.LAST_WEEK,
        DateFilterOption.THIS_MONTH,
        DateFilterOption.LAST_MONTH,
        DateFilterOption.ALL_TIME,
        DateFilterOption.CUSTOM
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            val selected = selectedOption == opt
            FilterChip(
                selected = selected,
                onClick = {
                    if (opt == DateFilterOption.CUSTOM) {
                        onOpenCustomPicker()
                    } else {
                        onSelectOption(opt)
                    }
                },
                label = {
                    Text(
                        text = opt.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandOrange,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("date_filter_${opt.name.lowercase()}")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NineSummaryCardsGrid(
    summary: FinancialSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Group 1: OFFICE FINANCES (Cards 1, 2, 3)
        SectionCardsHeader(
            title = "OFFICE FINANCES SUMMARY",
            icon = Icons.Default.Business,
            accentColor = BrandOrange
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricSummaryCard(
                title = "Total Office Income",
                amount = summary.officeIncome,
                indicatorColor = IncomeGreen,
                badgeText = "Office Inflow",
                tag = "card_office_income",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Total Office Expense",
                amount = summary.officeExpense,
                indicatorColor = BrandDarkRed,
                badgeText = "Office Outflow",
                tag = "card_office_expense",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Office Balance",
                amount = summary.officeBalance,
                indicatorColor = if (summary.officeBalance >= 0) BrandOrange else BrandDarkRed,
                badgeText = if (summary.officeBalance >= 0) "Net Profit" else "Deficit",
                tag = "card_office_balance",
                modifier = Modifier.weight(1f)
            )
        }

        // Group 2: HOME FINANCES (Cards 4, 5, 6)
        SectionCardsHeader(
            title = "HOME FINANCES SUMMARY",
            icon = Icons.Default.Home,
            accentColor = BrandDarkBlue
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricSummaryCard(
                title = "Total Home Income",
                amount = summary.homeIncome,
                indicatorColor = IncomeGreen,
                badgeText = "Home Inflow",
                tag = "card_home_income",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Total Home Expense",
                amount = summary.homeExpense,
                indicatorColor = BrandDarkRed,
                badgeText = "Household Spend",
                tag = "card_home_expense",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Home Balance",
                amount = summary.homeBalance,
                indicatorColor = if (summary.homeBalance >= 0) BrandOrange else BrandDarkRed,
                badgeText = "Remaining",
                tag = "card_home_balance",
                modifier = Modifier.weight(1f)
            )
        }

        // Group 3: COMBINED SUMMARY (Cards 7, 8, 9)
        SectionCardsHeader(
            title = "COMBINED EXECUTIVE TOTALS (OFFICE + HOME)",
            icon = Icons.Default.AccountBalance,
            accentColor = IncomeGreen
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricSummaryCard(
                title = "Combined Income",
                amount = summary.combinedIncome,
                indicatorColor = IncomeGreen,
                badgeText = "Total Income",
                isHighlighted = true,
                tag = "card_combined_income",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Combined Expense",
                amount = summary.combinedExpense,
                indicatorColor = BrandDarkRed,
                badgeText = "Total Spend",
                isHighlighted = true,
                tag = "card_combined_expense",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryCard(
                title = "Combined Balance",
                amount = summary.combinedBalance,
                indicatorColor = if (summary.combinedBalance >= 0) BrandOrange else BrandDarkRed,
                badgeText = "Net Balance",
                isHighlighted = true,
                tag = "card_combined_balance",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectionCardsHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    amount: Double,
    indicatorColor: Color,
    badgeText: String,
    tag: String,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag(tag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) BrandDarkBlue else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isHighlighted) indicatorColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isHighlighted) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyUtils.formatInr(amount),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = if (isHighlighted) Color.White else indicatorColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = indicatorColor.copy(alpha = if (isHighlighted) 0.25f else 0.12f)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = if (isHighlighted) Color.White else indicatorColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun IncomeVsExpenseBarChart(
    summary: FinancialSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Income vs Expense Comparison",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Office, Home & Combined cashflow comparison",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ChartLegendDot(label = "Income", color = IncomeGreen)
                    ChartLegendDot(label = "Expense", color = BrandDarkRed)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val maxVal = maxOf(
                summary.combinedIncome,
                summary.combinedExpense,
                summary.officeIncome,
                summary.officeExpense,
                1.0
            ).toFloat()

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                val width = size.width
                val height = size.height
                val groups = listOf(
                    summary.officeIncome.toFloat() to summary.officeExpense.toFloat(),
                    summary.homeIncome.toFloat() to summary.homeExpense.toFloat(),
                    summary.combinedIncome.toFloat() to summary.combinedExpense.toFloat()
                )

                // Horizontal grid lines
                for (i in 0..3) {
                    val y = height * (i / 4f)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.35f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val groupWidth = width / groups.size
                val barWidth = (groupWidth * 0.28f).coerceAtMost(42.dp.toPx())

                groups.forEachIndexed { index, (inc, exp) ->
                    val centerX = groupWidth * index + groupWidth / 2f
                    val incHeight = ((inc / maxVal) * (height - 16.dp.toPx())).coerceAtLeast(4f)
                    val expHeight = ((exp / maxVal) * (height - 16.dp.toPx())).coerceAtLeast(4f)

                    // Income Bar
                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(centerX - barWidth - 4.dp.toPx(), height - incHeight),
                        size = Size(barWidth, incHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Expense Bar
                    drawRoundRect(
                        color = BrandDarkRed,
                        topLeft = Offset(centerX + 4.dp.toPx(), height - expHeight),
                        size = Size(barWidth, expHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    "Office\n(${CurrencyUtils.formatInr(summary.officeBalance)})",
                    "Home\n(${CurrencyUtils.formatInr(summary.homeBalance)})",
                    "Combined\n(${CurrencyUtils.formatInr(summary.combinedBalance)})"
                ).forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseBreakdownDonutCard(
    title: String,
    subtitle: String,
    items: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No expenses recorded for this period yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val totalAmount = items.sumOf { it.amount }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(135.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            val strokeWidth = 22.dp.toPx()
                            var startAngle = -90f
                            items.take(7).forEachIndexed { idx, item ->
                                val sweep = (item.percentage / 100f) * 360f
                                drawArc(
                                    color = ChartColorPalette[idx % ChartColorPalette.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweep.coerceAtLeast(2f),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                )
                                startAngle += sweep
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.formatInr(totalAmount),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items.take(6).forEachIndexed { idx, item ->
                            val color = ChartColorPalette[idx % ChartColorPalette.size]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${CurrencyUtils.formatInr(item.amount)} (${item.percentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseTrendLineChartCard(
    title: String,
    subtitle: String,
    points: List<Pair<String, Double>>,
    lineColor: Color = BrandOrange,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            val maxVal = (points.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0).toFloat()

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val width = size.width
                val height = size.height
                if (points.isEmpty()) return@Canvas

                val stepX = if (points.size > 1) width / (points.size - 1) else width / 2f
                val coords = points.mapIndexed { idx, pair ->
                    val x = if (points.size > 1) idx * stepX else width / 2f
                    val y = height - ((pair.second.toFloat() / maxVal) * (height - 20.dp.toPx())) - 6.dp.toPx()
                    Offset(x, y)
                }

                // Area fill under line
                if (coords.size >= 2) {
                    val fillPath = Path().apply {
                        moveTo(coords.first().x, height)
                        coords.forEach { lineTo(it.x, it.y) }
                        lineTo(coords.last().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent)
                        )
                    )

                    val strokePath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        for (i in 1 until coords.size) {
                            lineTo(coords[i].x, coords[i].y)
                        }
                    }
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                coords.forEach { pt ->
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                    drawCircle(color = lineColor, radius = 3.5.dp.toPx(), center = pt)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (value > 0) {
                            Text(
                                text = CurrencyUtils.formatInr(value),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = lineColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItemCard(
    tx: TransactionEntity,
    runningBalance: Double? = null,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isIncome = tx.type == FinanceConstants.TYPE_INCOME
    val isOffice = tx.section == FinanceConstants.SECTION_OFFICE
    val amountColor = if (isIncome) IncomeGreen else BrandDarkRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onView() }
            .testTag("tx_card_${tx.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = amountColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = tx.type,
                                tint = amountColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isOffice) BrandDarkBlue else BrandOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = tx.section,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (isOffice) Color.White else BrandOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = tx.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (!tx.attachmentUri.isNullOrBlank()) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Receipt Attached",
                                    tint = BrandOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tx.title.ifEmpty { tx.category },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (tx.clientOrPaidTo.isNotBlank()) {
                            Text(
                                text = if (isIncome) "Client / Source: ${tx.clientOrPaidTo}" else "Paid To: ${tx.clientOrPaidTo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyUtils.formatInr(
                            amount = if (isIncome) tx.amount else -tx.amount,
                            includeSign = true
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = amountColor
                    )
                    Text(
                        text = "${DateUtils.formatDate(tx.dateMillis)} • ${tx.paymentMethod}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (runningBalance != null) {
                        Text(
                            text = "Bal: ${CurrencyUtils.formatInr(runningBalance)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandDarkBlue
                        )
                    }
                }
            }

            if (tx.description.isNotBlank() || tx.referenceNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildString {
                        if (tx.description.isNotBlank()) append(tx.description)
                        if (tx.referenceNumber.isNotBlank()) {
                            if (isNotEmpty()) append("  •  ")
                            append("Ref: ${tx.referenceNumber}")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onView,
                    modifier = Modifier.testTag("btn_view_tx_${tx.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }
                if (onDuplicate != null) {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.testTag("btn_dup_tx_${tx.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate Transaction",
                            tint = BrandDarkBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("btn_edit_tx_${tx.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Transaction",
                        tint = BrandOrange,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("btn_delete_tx_${tx.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = BrandDarkRed,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
