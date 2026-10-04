package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restore
import com.example.util.ApkExportHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CategoryEntity
import com.example.data.FinanceConstants
import com.example.data.FinancialSummary
import com.example.data.UserEntity
import com.example.ui.components.SectionTopSummaryStrip
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.IncomeGreen
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesManagementScreen(
    viewModel: FinanceViewModel,
    allCategories: List<CategoryEntity>,
    overallSummary: FinancialSummary
) {
    var selectedSection by remember { mutableStateOf(FinanceConstants.SECTION_OFFICE) }
    var selectedType by remember { mutableStateOf(FinanceConstants.TYPE_EXPENSE) }
    var customName by remember { mutableStateOf("") }

    val matchingCategories = remember(allCategories, selectedSection, selectedType) {
        allCategories.filter { it.section == selectedSection && it.type == selectedType }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("categories_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            SectionTopSummaryStrip(
                title = "Office & Home Categories Directory",
                subtitle = "Manage built-in & custom Office and Household income & expense categories",
                income = overallSummary.combinedIncome,
                expense = overallSummary.combinedExpense,
                balance = overallSummary.combinedBalance
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Add New Custom Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedSection == FinanceConstants.SECTION_OFFICE,
                            onClick = { selectedSection = FinanceConstants.SECTION_OFFICE },
                            label = { Text("Office Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedSection == FinanceConstants.SECTION_HOME,
                            onClick = { selectedSection = FinanceConstants.SECTION_HOME },
                            label = { Text("Home Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedType == FinanceConstants.TYPE_EXPENSE,
                            onClick = { selectedType = FinanceConstants.TYPE_EXPENSE },
                            label = { Text("Expense Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandDarkRed,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedType == FinanceConstants.TYPE_INCOME,
                            onClick = { selectedType = FinanceConstants.TYPE_INCOME },
                            label = { Text("Income Sources") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("New Category Name") },
                            placeholder = { Text("Enter custom category name") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_new_category")
                        )
                        Button(
                            onClick = {
                                if (customName.isNotBlank()) {
                                    viewModel.addCategory(selectedSection, selectedType, customName)
                                    customName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("btn_add_category")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "$selectedSection $selectedType Categories (${matchingCategories.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchingCategories.forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    if (!cat.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { viewModel.deleteCategory(cat) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Custom Category",
                                                tint = BrandDarkRed,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
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

@Composable
fun ProfileAndSettingsScreen(
    viewModel: FinanceViewModel,
    user: UserEntity?,
    darkTheme: Boolean
) {
    val context = LocalContext.current
    var fullName by remember(user) { mutableStateOf(user?.fullName.orEmpty()) }
    var companyName by remember(user) { mutableStateOf(user?.companyName ?: "ShivWebsIndia") }
    var phone by remember(user) { mutableStateOf(user?.phone.orEmpty()) }
    var role by remember(user) { mutableStateOf(user?.role ?: "Administrator") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBlue)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_shivwebs_logo),
                        contentDescription = "Profile & Brand Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.fullName ?: "Administrator",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = user?.email ?: "admin@shivwebsindia.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandOrange
                        )
                        Text(
                            text = "${user?.role ?: "Admin"} • ${user?.companyName ?: "ShivWebsIndia"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        // Update User Profile Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Edit User Profile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Office / Company Name") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Designation / Role") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.updateProfile(fullName, companyName, phone, role) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Change Password Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Security & Change Password",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newPassword.isNotBlank()) {
                                viewModel.changePassword(currentPassword, newPassword)
                                currentPassword = ""
                                newPassword = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandDarkBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Password", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // App Preferences & Demo Data Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Preferences & Data Controls",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = BrandOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dark Dashboard Theme", fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { viewModel.toggleDarkTheme() }
                        )
                    }

                    HorizontalDivider()

                    Text(
                        text = "Demo Data Separation Control",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Initial sample records are tagged separately from your real entries. You can clear sample records at any time or reload them.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearDemoTransactions() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDarkRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Demo Data")
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadDemoTransactions() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reload Sample")
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Download / Share Mobile APK",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = BrandDarkBlue
                    )
                    Text(
                        text = "Extract and share the signed Smart_Expense_Income_Manager.apk file directly to your device, Drive, or WhatsApp.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            ApkExportHelper.shareInstalledApk(context).onSuccess { file ->
                                viewModel.showToast("APK ready: ${file.name}")
                            }.onFailure {
                                viewModel.showToast("APK located at: app/build/outputs/apk/debug/app-debug.apk")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_share_apk")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download / Share Signed APK (.apk)", fontWeight = FontWeight.ExtraBold)
                    }

                    HorizontalDivider()

                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandDarkRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_logout")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout of Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // About ShivWebsIndia Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_shivwebs_logo),
                            contentDescription = "ShivWebsIndia Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SHIVWEBSINDIA",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = BrandDarkBlue
                            )
                            Text(
                                text = "Website Design | Web Development | Digital Marketing",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BrandOrange
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Smart Expense & Income Manager is professionally designed and developed by ShivWebsIndia to empower modern businesses and households with accurate, real-time financial intelligence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shivwebsindia.com"))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Visit www.shivwebsindia.com", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            ShivWebsIndiaFooter()
        }
    }
}
