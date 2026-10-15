
package com.sentfromgod.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SentFromGodApp() }
    }
}

private val Purple = androidx.compose.ui.graphics.Color(0xFF6941C6)

@Composable
fun SentFromGodApp() {
    var signedIn by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }

    val colors = if (darkMode) darkColorScheme() else lightColorScheme(
        primary = Purple
    )

    MaterialTheme(colorScheme = colors) {
        if (!signedIn) {
            AuthScreen(onSignedIn = { signedIn = true })
        } else {
            AppPages(
                darkMode = darkMode,
                onDarkMode = { darkMode = it },
                onSignOut = { signedIn = false }
            )
        }
    }
}

@Composable
private fun AppPages(
    darkMode: Boolean,
    onDarkMode: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    var page by remember { mutableStateOf("Home") }
    var balance by remember { mutableDoubleStateOf(0.0) }
    val history = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Home", "Earn", "Wallet", "Profile").forEach { item ->
                    NavigationBarItem(
                        selected = page == item,
                        onClick = { page = item },
                        icon = {
                            Text(
                                when (item) {
                                    "Home" -> "⌂"
                                    "Earn" -> "▶"
                                    "Wallet" -> "£"
                                    else -> "●"
                                },
                                fontSize = 20.sp
                            )
                        },
                        label = { Text(item) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (page) {
                "Home" -> {
                    PageTitle("Sent From GOD")
                    Text(
                        "Play • Watch • Listen • Earn",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Your demo balance")
                            Text(
                                "£%.2f".format(balance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Demo balance only — not withdrawable cash.")
                            Button(onClick = { page = "Earn" }) {
                                Text("Explore rewards")
                            }
                        }
                    }
                    MenuRow("My profile") { page = "My profile" }
                    MenuRow("Settings") { page = "Settings" }
                }

                "Earn" -> {
                    PageTitle("Earn rewards")
                    Text(
                        "These sample tasks add demo amounts only.",
                        modifier = Modifier.padding(16.dp)
                    )
                    listOf(
                        "Game demo" to 0.50,
                        "Video demo" to 0.10,
                        "Music demo" to 0.05,
                        "Daily bonus demo" to 0.20
                    ).forEach { (name, amount) ->
                        MenuRow("$name  +£%.2f".format(amount)) {
                            balance += amount
                            history.add(
                                "$name — +£%.2f (demo)".format(amount)
                            )
                            page = "Earnings history"
                        }
                    }
                }

                "Wallet" -> {
                    PageTitle("My Wallet")
                    Text(
                        "Demo balance: £%.2f".format(balance),
                        modifier = Modifier.padding(20.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    MenuRow("Earnings history") {
                        page = "Earnings history"
                    }
                    MenuRow("Payment details") {
                        page = "Payment details"
                    }
                    Text(
                        "Withdrawals are not connected.",
                        modifier = Modifier.padding(20.dp)
                    )
                }

                "Profile" -> {
                    PageTitle("My Profile")
                    listOf(
                        "My profile",
                        "Settings",
                        "Payment details",
                        "Earnings history",
                        "Invite friends",
                        "Help & support",
                        "Terms & privacy"
                    ).forEach { option ->
                        MenuRow(option) { page = option }
                    }
                }

                "My profile" -> {
                    PageTitle("My Profile")
                    Text(
                        "Your account is signed in.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        "Email: use the email you registered with.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        "Profile names and details are not yet saved to the database.",
                        modifier = Modifier.padding(16.dp)
                    )
                    MenuRow("Settings") { page = "Settings" }
                    MenuRow("Back to Profile") { page = "Profile" }
                }

                "Settings" -> {
                    PageTitle("Settings")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark mode", Modifier.weight(1f))
                        Switch(
                            checked = darkMode,
                            onCheckedChange = onDarkMode
                        )
                    }
                    MenuRow("Help & support") {
                        page = "Help & support"
                    }
                    MenuRow("Terms & privacy") {
                        page = "Terms & privacy"
                    }
                    MenuRow("Sign out") { onSignOut() }
                }

                "Payment details" -> {
                    var accountName by remember { mutableStateOf("") }
                    var paymentMethod by remember { mutableStateOf("") }
                    var accountReference by remember { mutableStateOf("") }
                    var message by remember { mutableStateOf("") }

                    PageTitle("Payment details")
                    Text(
                        "Demo form. Details are not yet saved to Supabase.",
                        modifier = Modifier.padding(16.dp)
                    )
                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account holder name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { paymentMethod = it },
                        label = { Text("Payment method") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    OutlinedTextField(
                        value = accountReference,
                        onValueChange = { accountReference = it },
                        label = { Text("Account reference") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Button(
                        onClick = {
                            message =
                                "Not saved yet. Database connection is still needed."
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Save details")
                    }
                    Text(message, modifier = Modifier.padding(16.dp))
                }

                "Earnings history" -> {
                    PageTitle("Earnings history")
                    Text(
                        "Demo activity for this app session.",
                        modifier = Modifier.padding(16.dp)
                    )
                    if (history.isEmpty()) {
                        Text(
                            "No demo earnings yet.",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        history.reversed().forEach { entry ->
                            MenuRow(entry) {}
                        }
                    }
                    Text(
                        "History is not yet stored permanently.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Invite friends" -> {
                    PageTitle("Invite friends")
                    Text(
                        "Share Sent From GOD with your friends.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Join me on Sent From GOD! " +
                                        "Ask me for the app link."
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    sendIntent,
                                    "Invite friends"
                                )
                            )
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Share invitation")
                    }
                    Text(
                        "A referral link and referral tracking still need to be configured.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Help & support" -> {
                    PageTitle("Help & support")
                    Text(
                        "Need help with your account? Contact support by email.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_SENDTO,
                                Uri.parse("mailto:support@example.com")
                            ).apply {
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Sent From GOD support"
                                )
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Email support")
                    }
                    Text(
                        "The support email must be replaced with your real support address.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Terms & privacy" -> {
                    PageTitle("Terms & Privacy")
                    Text(
                        "Sent From GOD demo app",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Demo balances are not real money and cannot be withdrawn. " +
                            "No earnings are guaranteed. Payment details and profile " +
                            "information must not be considered saved until the " +
                            "secure database connection is implemented. Do not enter " +
                            "bank passwords, card PINs, or other secret credentials.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        "A complete published privacy policy and terms of service " +
                            "must be provided before public launch.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PageTitle(title: String) {
    Text(
        title,
        modifier = Modifier.padding(20.dp),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun MenuRow(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            title,
            modifier = Modifier.padding(18.dp)
        )
    }
}
