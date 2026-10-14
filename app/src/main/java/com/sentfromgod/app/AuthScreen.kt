
package com.sentfromgod.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Sent From GOD",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                if (isRegistering) "Create your account"
                else "Welcome back"
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (at least 6 characters)") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!email.contains("@") || password.length < 6) {
                        message = "Enter a valid email and a password of at least 6 characters."
                        return@Button
                    }

                    scope.launch {
                        busy = true
                        message = ""

                        try {
                            if (isRegistering) {
                                SupabaseClient.client.auth.signUpWith(Email) {
                                    this.email = email.trim()
                                    this.password = password
                                }

                                message =
                                    "Registration submitted. Check your email to verify your account, then sign in."
                            } else {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    this.email = email.trim()
                                    this.password = password
                                }

                                onSignedIn()
                            }
                        } catch (e: Exception) {
                            message = e.message
                                ?: "Unable to complete the request. Please try again."
                        } finally {
                            busy = false
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (busy) "Please wait..."
                    else if (isRegistering) "Create account"
                    else "Log in"
                )
            }

            TextButton(
                onClick = {
                    isRegistering = !isRegistering
                    message = ""
                },
                enabled = !busy
            ) {
                Text(
                    if (isRegistering)
                        "Already registered? Log in"
                    else
                        "New here? Create an account"
                )
            }

            if (message.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(message)
            }
        }
    }
}
