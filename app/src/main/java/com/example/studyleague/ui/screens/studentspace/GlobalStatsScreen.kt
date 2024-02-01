package com.example.studyleague.ui.screens.studentspace

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.ui.FetchState
import com.example.studyleague.ui.Util.calculateColorForGrade
import com.example.studyleague.ui.Util.formatFloat
import com.example.studyleague.ui.components.Accordion
import com.example.studyleague.ui.components.Accordion.TextRow
import com.example.studyleague.ui.components.StatisticsSquare
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn


@Composable
fun GlobalStatsScreen() {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        studentViewModel.fetchStudentStats()
        Log.d("GlobalStatsScreen", "Fetching student stats at global screen")
    }

    when (uiState.studentStats) {
        is FetchState.Loaded -> {
            GlobalStatsScreenContent()
        }

        else -> {}
    }
}

@Composable
fun GlobalStatsScreenContent() {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    val studentStats = uiState.studentStats.getLoadedValue().studentStatisticsDTO

    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val titleTextStyle = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 12.sp
            )

            val dataTextStyle = TextStyle(
                fontWeight = FontWeight.ExtraBold, fontSize = 25.sp
            )

            StatisticsSquare(
                title = "Nota semanal",
                data = formatFloat(studentStats.weeklyGrade),
                dataTextStyle = dataTextStyle.copy(color = calculateColorForGrade(studentStats.weeklyGrade)),
                titleTextStyle = titleTextStyle
            )

            StatisticsSquare(
                title = "Nota mensal",
                data = formatFloat(studentStats.monthlyGrade),
                dataTextStyle = dataTextStyle.copy(color = calculateColorForGrade(studentStats.monthlyGrade)),
                titleTextStyle = titleTextStyle
            )
        }

        val allTimeStats = studentStats.allTimeStatistic
        val globalItems = listOf(
            listOf("Revisões totais", allTimeStats.reviews.toString()),
            listOf("Questões totais", allTimeStats.questions.toString()),
            listOf("Horas totais", allTimeStats.hours.toString())
        )

        val weeklyStats = studentStats.weeklyStatistic
        val weeeklyItems = listOf(
            listOf("Revisões semanais", weeklyStats.reviews.toString()),
            listOf("Questões semanais", weeklyStats.questions.toString()),
            listOf("Horas semanais", weeklyStats.hours.toString())
        )

        Accordion(title = "Total", startsExpanded = true, body = {
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
