package com.example.studyleague.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier, header: String, target: Int, current: Int) {
    val progress = if (target == 0) 0F else current / target.toFloat()
    val progressPercentage = (progress * 100)

    Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(header, fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Text("$progressPercentage%", fontSize = 12.sp, fontWeight = FontWeight.Light)
        }

        LinearProgressIndicator(
            progress = progress,
            color = Color.Black,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth()
        )

        Text("$current/$target", fontSize = 12.sp, fontWeight = FontWeight.Light)
    }
}