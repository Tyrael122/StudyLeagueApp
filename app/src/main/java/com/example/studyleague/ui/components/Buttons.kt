package com.example.studyleague.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingButton(modifier: Modifier = Modifier, onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Black),
        modifier = modifier
    ) {
        Text(
            text, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(10.dp)
        )
    }
}