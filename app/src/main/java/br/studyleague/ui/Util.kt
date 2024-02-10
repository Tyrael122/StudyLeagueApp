package br.studyleague.ui

import androidx.compose.ui.graphics.Color

object Util {
    fun formatFloat(number: Float): String {
        return String.format("%.2f", number)
    }

    fun calculateColorForGrade(grade: Float): Color {
        return when {
            grade < 5 -> Color(0xFFBA0000)
            grade < 7 -> Color(0xFFE68A00)
            else -> Color(0xFF00B607)
        }
    }
}