package com.sentfromgod.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.activity.compose.setContent

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SentFromGodApp(
                onFingerprint = { onResult ->
                    val manager = BiometricManager.from(this)
                    val authenticators =
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK

                    if (
                        manager.canAuthenticate(authenticators) !=
                        BiometricManager.BIOMETRIC_SUCCESS
                    ) {
                        onResult(
                            false,
                            "Set up fingerprint or biometrics in your phone settings first."
                        )
                    } else {
                        val executor = ContextCompat.getMainExecutor(this)

                        val prompt = BiometricPrompt(
                            this,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(
                                    result: BiometricPrompt.AuthenticationResult
                                ) {
                                    onResult(true, "Fingerprint verified.")
                                }

                                override fun onAuthenticationFailed() {
                                    onResult(
                                        false,
                                        "Fingerprint not recognised. Try again."
                                    )
                                }

                                override fun onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence
                                ) {
                                    onResult(false, errString.toString())
                                }
                            }
                        )

                        val info = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Sent From GOD")
                            .setSubtitle("Verify your identity")
                            .setAllowedAuthenticators(authenticators)
                            .setNegativeButtonText("Cancel")
                            .build()

                        prompt.authenticate(info)
                    }
                }
            )
        }
    }
}

private val Purple = androidx.compose.ui.graphics.Color(0xFF6941C6)

@Composable
fun SentFromGodApp(
    onFingerprint: ((Boolean, String) -> Unit) -> Unit
) {
    var signedIn by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }
    var biometricMessage by remember { mutableStateOf("") }

    val colors = if (darkMode) {
        darkColorScheme()
    } else {
        lightColorScheme(primary = Purple)
    }

    MaterialTheme(colorScheme = colors) {
        if (!signedIn) {
            AuthScreen(onSignedIn = { signedIn = true })
        } else {
            AppPages(
                darkMode = darkMode,
                onDarkMode = { darkMode = it },
                onSignOut = {
                    signedIn = false
                    biometricMessage = ""
                },
                onFingerprint = {
                    onFingerprint { success, message ->
                        biometricMessage = message
                    }
                },
                biometricMessage = biometricMessage
            )
        }
    }
}

@Composable
private fun AppPages(
    darkMode: Boolean,
    onDarkMode: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onFingerprint: () -> Unit,
    biometricMessage: String
) {
    var page by remember { mutableStateOf("Home") }
    var balance by remember { mutableDoubleStateOf(0.0) }
    val history = remember { mutableStateListOf<String>() }
    var accountName by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var accountReference by remember { mutableStateOf("") }
    var withdrawalAmount by remember { mutableStateOf("5.00") }
    var withdrawalMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                .verticalScroll(rememberScrollState())
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
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Your demo balance")
                            Text(
                                formatGBP(balance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Demo only. This is not real or withdrawable money."
                            )
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
                        MenuRow("$name  +${formatGBP(amount)}") {
                            balance += amount
                            history.add("$name — +${formatGBP(amount)} (demo)")
                            page = "Earnings history"
                        }
                    }
                    Text(
                        "Real reward providers have not been connected.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Wallet" -> {
                    PageTitle("My Wallet")
                    Text(
                        "Demo balance: ${formatGBP(balance)}",
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
                    MenuRow("Request withdrawal") {
                        page = "Withdraw"
                    }
                    Text(
                        "Real withdrawals are not connected.",
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
                        "You are signed in for this app session.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        "Profile details are not yet saved to the database.",
                        modifier = Modifier.padding(16.dp)
                    )
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
                    MenuRow("Fingerprint verification") {
                        onFingerprint()
                    }
                    if (biometricMessage.isNotBlank()) {
                        Text(
                            biometricMessage,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    MenuRow("Help & support") {
                        page = "Help & support"
                    }
                    MenuRow("Terms & privacy") {
                        page = "Terms & privacy"
                    }
                    MenuRow("Sign out") {
                        scope.launch {
                            try {
                                SupabaseClient.client.auth.signOut()
                            } finally {
                                onSignOut()
                            }
                        }
                    }
                }

                "Payment details" -> {
                    PageTitle("Payment details")
                    Text(
                        "Enter your payout details. This form does not save them yet.",
                        modifier = Modifier.padding(16.dp)
                    )
                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account holder name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { paymentMethod = it },
                        label = { Text("Payment method") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    OutlinedTextField(
                        value = accountReference,
                        onValueChange = { accountReference = it },
                        label = { Text("Account reference") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Text(
                        "Never enter your bank password, card PIN or CVV.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        "Saving payment details is not connected yet.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Withdraw" -> {
                    PageTitle("Request withdrawal")
                    Text(
                        "Minimum request: £5.00",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        "Your current balance is demo money and cannot be withdrawn.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    OutlinedTextField(
                        value = withdrawalAmount,
                        onValueChange = { withdrawalAmount = it },
                        label = { Text("Amount in GBP (£)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Button(
                        onClick = {
                            val amount = withdrawalAmount.toDoubleOrNull()
                            withdrawalMessage = when {
                                amount == null || !amount.isFinite() ->
                                    "Enter a valid amount."
                                amount < 5.0 ->
                                    "The minimum request is £5.00."
                                else ->
                                    "Not submitted. Real earnings, secure balance checks and payout processing are not connected."
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Check request")
                    }
                    if (withdrawalMessage.isNotBlank()) {
                        Text(
                            withdrawalMessage,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                "Earnings history" -> {
                    PageTitle("Earnings history")
                    Text(
                        "Demo activity for this app session only.",
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
                        "History is not permanently stored.",
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
                                    "Join me on Sent From GOD! Ask me for the app link."
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, "Invite friends")
                            )
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Share invitation")
                    }
                    Text(
                        "Referral links and tracking are not configured yet.",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                "Help & support" -> {
                    PageTitle("Help & support")
                    Text(
                        "Support email has not yet been configured.",
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
                }

                "Terms & privacy" -> {
                    PageTitle("Terms & Privacy")
                    Text(
                        "Demo balances are not real money and cannot be withdrawn. " +
                            "No earnings are guaranteed. Payment details are not saved " +
                            "by this demo screen. A complete privacy policy and terms " +
                            "of service are required before public launch.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatGBP(amount: Double): String =
    String.format(Locale.UK, "£%.2f", amount)

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
