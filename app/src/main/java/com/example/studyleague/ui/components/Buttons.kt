package com.example.studyleague.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingButton(modifier: Modifier = Modifier, onClick: () -> Unit, text: String) {
    Button(
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Black),
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun NumberButton(modifier: Modifier = Modifier, onClick: () -> Unit, text: @Composable () -> Unit) {
    OutlinedButton(
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(vertical = 7.dp, horizontal = 5.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        text()
    }
}

@Composable
fun DefaultIconButtom(modifier: Modifier = Modifier, onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        onClick = onClick, colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black, contentColor = Color.White
        ), modifier = modifier
            .size(50.dp)
    ) {
        content()
    }
}