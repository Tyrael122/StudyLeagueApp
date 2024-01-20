package com.example.studyleague.ui.screens.studentspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.ui.components.Accordion
import com.example.studyleague.ui.components.Accordion.TextRow
import com.example.studyleague.ui.components.StatisticsSquare
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn


@Composable
fun GlobalStatsScreen() {
    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val titleTextStyle = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 12.sp
            )

            val dataTextStyle = TextStyle(
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFBA0000), fontSize = 25.sp
            )

            StatisticsSquare(
                title = "Nota semanal",
                data = "6.3",
                dataTextStyle = dataTextStyle,
                titleTextStyle = titleTextStyle
            )

            StatisticsSquare(
                title = "Nota mensal",
                data = "8.2",
                dataTextStyle = dataTextStyle.copy(color = Color(0xFF00B607)),
                titleTextStyle = titleTextStyle
            )
        }

        val globalItems = listOf(
            listOf("Revisões totais", "59"),
            listOf("Questões totais", "1234"),
            listOf("Horas totais", "234")
        )


        val weeeklyItems = listOf(
            listOf("Revisões semanais", "59"),
            listOf("Questões semanais", "1234"),
            listOf("Horas semanais", "234")
        )

        Accordion(title = "Total", initialExpandedState = true, body = {
            TextRow(
                items = globalItems
            )
        })

        Accordion(title = "Semanal", body = {
            TextRow(
                items = weeeklyItems
            )
        })
    }
}
