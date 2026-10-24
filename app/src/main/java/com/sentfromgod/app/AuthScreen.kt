
package com.sentfromgod.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
private data class ProfileInsert(
    val id: String,
    val email: String,
    val display_name: String
)

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sent From GOD",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(if (isRegistering) "Create your account" else "Welcome back")

        Spacer(Modifier.height(24.dp))

        if (isRegistering) {
            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    message = ""
                },
                label = { Text("Display name") },
                singleLine = true,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                message = ""
            },
            label = { Text("Email address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                message = ""
            },
            label = { Text("Password (at least 6 characters)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val cleanEmail = email.trim()
                val cleanName = displayName.trim()

                if (!cleanEmail.contains("@") || password.length < 6) {
                    message = "Enter a valid email and a password of at least 6 characters."
                    return@Button
                }

                if (isRegistering && cleanName.isBlank()) {
                    message = "Enter your display name."
                    return@Button
                }

                scope.launch {
                    busy = true
                    message = ""

                    try {
                        if (isRegistering) {
                            SupabaseClient.client.auth.signUpWith(Email) {
                                this.email = cleanEmail
                                this.password = password
                            }

                            val user = SupabaseClient.client.auth.currentUserOrNull()

                            if (user == null) {
                                message = "Registration submitted. Check your email to verify your account, then log in."
                            } else {
                                SupabaseClient.client
                                    .from("Profiles")
                                    .insert(
                                        ProfileInsert(
                                            id = user.id,
                                            email = cleanEmail,
                                            display_name = cleanName
                                        )
                                    )

                                message = "Account and profile created successfully. You can now log in."
                                isRegistering = false
                            }
                        } else {
                            SupabaseClient.client.auth.signInWith(Email) {
                                this.email = cleanEmail
                                this.password = password
                            }

                            val user = SupabaseClient.client.auth.currentUserOrNull()

                            if (user == null) {
                                message = "Login could not be confirmed. Please try again."
                            } else {
                                onSignedIn()
                            }
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
                when {
                    busy -> "Please wait..."
                    isRegistering -> "Create account"
                    else -> "Log in"
                }
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
                if (isRegistering) "Already registered? Log in"
                else "New here? Create an account"
            )
        }

        if (message.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(message)
        }
    }
}
