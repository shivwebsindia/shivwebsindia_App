package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ShivWebsIndiaFooter
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandDarkRed
import com.example.ui.theme.BrandOrange

private enum class AuthTab {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    authError: String?,
    onClearError: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (fullName: String, email: String, password: String, companyName: String, phone: String) -> Unit,
    onResetPassword: (email: String, newPassword: String, onSuccess: () -> Unit) -> Unit
) {
    var mode by remember { mutableStateOf(AuthTab.LOGIN) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("admin@shivwebsindia.com") }
    var password by remember { mutableStateOf("admin123") }
    var companyName by remember { mutableStateOf("ShivWebsIndia") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandDarkBlue, Color(0xFF061626))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Brand Identity Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_shivwebs_logo),
                        contentDescription = "ShivWebsIndia Logo",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Smart Expense & Income Manager",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = BrandDarkBlue,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BrandOrange.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Designed & Developed by ShivWebsIndia",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandOrange,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Website Design | Web Development | Digital Marketing",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Auth Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = mode == AuthTab.LOGIN,
                            onClick = {
                                mode = AuthTab.LOGIN
                                onClearError()
                            },
                            label = { Text("Sign In") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = mode == AuthTab.REGISTER,
                            onClick = {
                                mode = AuthTab.REGISTER
                                onClearError()
                            },
                            label = { Text("Register") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = mode == AuthTab.FORGOT_PASSWORD,
                            onClick = {
                                mode = AuthTab.FORGOT_PASSWORD
                                onClearError()
                            },
                            label = { Text("Reset") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (mode == AuthTab.REGISTER) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_fullname_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company / Office Name") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            onClearError()
                        },
                        label = { Text("Email Address *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onClearError()
                        },
                        label = {
                            Text(
                                if (mode == AuthTab.FORGOT_PASSWORD) "New Password *" else "Password *"
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    if (authError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandDarkRed.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, BrandDarkRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authError,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = BrandDarkRed,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            when (mode) {
                                AuthTab.LOGIN -> onLogin(email, password)
                                AuthTab.REGISTER -> onRegister(fullName, email, password, companyName, phone)
                                AuthTab.FORGOT_PASSWORD -> onResetPassword(email, password) {
                                    mode = AuthTab.LOGIN
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Text(
                            text = when (mode) {
                                AuthTab.LOGIN -> "Login to Financial Dashboard"
                                AuthTab.REGISTER -> "Create Account & Continue"
                                AuthTab.FORGOT_PASSWORD -> "Reset Password"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                    }

                    if (mode == AuthTab.LOGIN) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                email = "admin@shivwebsindia.com"
                                password = "admin123"
                                onLogin("admin@shivwebsindia.com", "admin123")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_demo_login_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Quick Login with Default Admin Account",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = BrandDarkBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.widthIn(max = 480.dp)) {
                ShivWebsIndiaFooter()
            }
        }
    }
}
