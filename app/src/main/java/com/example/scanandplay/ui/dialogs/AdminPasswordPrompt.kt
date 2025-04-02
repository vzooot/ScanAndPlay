package com.example.scanandplay.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AdminPasswordPrompt(
    password: String,
    onPasswordChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    shakeTrigger: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🔐 Admin Access", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Enter password") },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = onConfirm) {
                    Text("Confirm")
                }
            }
        }
    }
}
