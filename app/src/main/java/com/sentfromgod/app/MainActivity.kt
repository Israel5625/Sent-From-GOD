
package com.sentfromgod.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFF6941C6)
private val Navy = Color(0xFF171735)
private val Background = Color(0xFFF7F6FC)
private val Green = Color(0xFF16865B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SentFromGodApp() }
    }
}

@Composable
fun SentFromGodApp() {
    var tab by remember { mutableIntStateOf(0) }
    var balance by remember { mutableDoubleStateOf(0.0) }

    MaterialTheme {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar {
                    listOf("Home", "Earn", "Wallet", "Profile")
                        .forEachIndexed { index, name ->
                            NavigationBarItem(
                                selected = tab == index,
                                onClick = { tab = index },
                                icon = {
                                    Text(
                                        listOf("⌂", "▶", "£", "●")[index],
                                        fontSize = 20.sp
                                    )
                                },
                                label = { Text(name) }
                            )
                        }
                }
            }
        ) { padding ->
            when (tab) {
                0 -> HomeScreen(balance, padding) { tab = 1 }
                1 -> EarnScreen(padding) { amount ->
                    balance += amount
                }
                2 -> WalletScreen(balance, padding)
                else -> ProfileScreen(padding)
            }
        }
    }
}

@Composable
fun BrandHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(Purple, RoundedCornerShape(17.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("SFG", color = Color.White, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "Sent From GOD",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
            Text(
                "Play • Watch • Listen • Earn",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun HomeScreen(
    balance: Double,
    padding: PaddingValues,
    onEarn: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { BrandHeader() }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Navy)
            ) {
                Column(Modifier.padding(22.dp)) {
                    Text("Your demo balance", color = Color.LightGray)
                    Text(
                        "£${"%.2f".format(balance)}",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Earn up to £20/day when eligible offers are available.",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onEarn,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple
                        )
                    ) {
                        Text("Explore rewards")
                    }
                }
            }
        }
        item { Heading("Discover ways to earn") }
        item { RewardCard("🎮", "Play games", "Explore game offers") }
        item { RewardCard("▶", "Watch videos", "Discover sponsored videos") }
        item { RewardCard("♫", "Music", "Explore music campaigns") }
        item { RewardCard("🎁", "Daily bonus", "Check available bonuses") }
        item {
            Text(
                "Rewards depend on eligible campaigns. Demo balances are not cash.",
                modifier = Modifier.padding(20.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun Heading(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        fontWeight = FontWeight.Bold,
        color = Navy,
        fontSize = 19.sp
    )
}

@Composable
fun RewardCard(icon: String, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Navy)
                Text(description, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EarnScreen(
    padding: PaddingValues,
    onDemoReward: (Double) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding)
    ) {
        Text(
            "Earn rewards",
            modifier = Modifier.padding(20.dp),
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Navy
        )
        Text(
            "Try the demo reward buttons below. They do not pay real money.",
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Gray,
            fontSize = 13.sp
        )

        listOf(
            Triple("🎮", "Game task", 0.50),
            Triple("▶", "Video demo", 0.10),
            Triple("♫", "Music demo", 0.05),
            Triple("🎁", "Daily bonus demo", 0.20)
        ).forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 7.dp)
                    .clickable {
                        onDemoReward(item.third)
                        message = "Demo balance updated by £%.2f"
                            .format(item.third)
                    },
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.first, fontSize = 25.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(item.second, Modifier.weight(1f), color = Navy)
                    Text(
                        "+£%.2f".format(item.third),
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (message.isNotEmpty()) {
            Text(
                message,
                modifier = Modifier.padding(20.dp),
                color = Green
            )
        }
    }
}

@Composable
fun WalletScreen(balance: Double, padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)
    ) {
        Text(
            "My Wallet",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Navy
        )
        Spacer(Modifier.height(18.dp))
        Card(shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(22.dp)) {
                Text("Demo balance", color = Color.Gray)
                Text(
                    "£${"%.2f".format(balance)}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Text("Withdrawals not connected yet")
                }
                Text(
                    "Real withdrawals require a verified account, backend, " +
                        "and payment provider.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)
    ) {
        Text(
            "My Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Navy
        )
        Spacer(Modifier.height(16.dp))
        listOf(
            "Account settings",
            "Payment details",
            "Earnings history",
            "Invite friends",
            "Help & support",
            "Terms & privacy"
        ).forEach { option ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    option,
                    modifier = Modifier.padding(17.dp),
                    color = Navy
                )
            }
        }
    }
}
