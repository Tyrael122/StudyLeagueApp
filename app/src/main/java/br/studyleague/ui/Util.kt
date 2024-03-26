package br.studyleague.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.util.Locale

object Util {
    fun formatFloat(number: Float): String {
        return String.format(Locale.getDefault(), "%.2f", number)
    }

    fun calculateColorForGrade(grade: Float): Color {
        val green = Color(0xFF00B607)
        val yellow = Color(0xFFE68A00)
        val red = Color(0xFFBA0000)
        return when {
            grade < 5 -> lerp(red, yellow, grade / 5F)
            else -> lerp(yellow, green, (grade - 5) / 5F)
        }
    }

    fun convertHourFloatToLocalTime(hour: Float): LocalTime {
        return LocalTime.ofSecondOfDay((hour * 3600).toLong())
    }
}

@Preview
@Composable
fun ColorByGradePreview() {
    var grade by remember { mutableFloatStateOf(0f) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Slider(value = grade, valueRange = 0f..10f, onValueChange = { grade = it })

        TextWithColorByGrade(grade = grade, gradeToColor = Util::calculateColorForGrade)
    }
}

@Composable
fun TextWithColorByGrade(grade: Float, gradeToColor: (Float) -> Color) {
    Text(
        Util.formatFloat(grade),
        color = gradeToColor(grade),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp)
    )
}