package com.example.studyleague.ui.screens.studentspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.FetchState
import com.example.studyleague.ui.StudentViewModel
import com.example.studyleague.ui.components.StatisticsSquare
import com.example.studyleague.ui.components.TopBarTitle
import com.example.studyleague.ui.components.datagrid.DataGrid
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn
import kotlinx.coroutines.launch


@Composable
fun DailyStatsScreen() {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    val currentDayOfWeek = uiState.currentDate.dayOfWeek
    TopBarTitle.setTitle(
        currentDayOfWeek.getDisplayName(
            java.time.format.TextStyle.FULL, LocalConfiguration.current.locales[0]
        )
    )

    LaunchedEffect(Unit) {
        studentViewModel.fetchScheduledSubjectsForDay()
        studentViewModel.fetchStudentStats()
    }

    when (uiState.studentStats) {
        is FetchState.Loaded -> {
            DailyScreenContent(studentViewModel)
        }

        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScreenContent(studentViewModel: StudentViewModel) {
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
                title = "Nota diária",
                data = studentStats.dailyGrade.toString(),
                dataTextStyle = dataTextStyle.copy(color = Color(0xFFBA0000)),
                titleTextStyle = titleTextStyle
            )

            StatisticsSquare(
                title = "Metas batidas",
                data = "${studentStats.hoursGoalsCompleted}/${uiState.subjects.getLoadedValue().size}",
                dataTextStyle = dataTextStyle,
                titleTextStyle = titleTextStyle
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val dailyStudentStats = studentStats.dailyStatistic

            StatisticsSquare(title = "Horas", data = dailyStudentStats.hours.toString())
            StatisticsSquare(title = "Questões", data = dailyStudentStats.questions.toString())
            StatisticsSquare(title = "Revisões", data = dailyStudentStats.reviews.toString())
        }

        var isBottomSheetVisible by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        DataGrid(isSearchBarVisible = false,
            columns = Subject.columns,
            items = uiState.subjects.getLoadedValue(),
            onItemClick = {
                studentViewModel.selectSubject(it)

                isBottomSheetVisible = true
            })

        if (isBottomSheetVisible) {
            val selectedSubjectDailyStats = uiState.selectedSubject.subjectDTO.dailyStatistic

            val data = listOf(
                mutableListOf("Horas estudadas", selectedSubjectDailyStats.hours.toString()),
                mutableListOf("Questões", selectedSubjectDailyStats.questions.toString()),
                mutableListOf("Revisões", selectedSubjectDailyStats.reviews.toString()),
            )

            val coroutineScope = rememberCoroutineScope()

            ModalBottomSheet(
                containerColor = Color.White, onDismissRequest = {
                    coroutineScope.launch {
                        studentViewModel.updateSelectedSubjectDailyStats(data.map { it[1].toFloat() })

                        isBottomSheetVisible = false
                    }
                }, sheetState = sheetState
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75F)
                        .padding(horizontal = 20.dp)
                ) {
                    data.forEach { stringList ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 67.dp)
                                .shadow(1.dp, RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEEEEE))
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                stringList[0], color = Color(0xFF545454), style = TextStyle(
                                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                                )
                            )

                            BasicTextField(
                                value = stringList[1],
                                onValueChange = { stringList[1] = it },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .width(40.dp)
                                    .border(
                                        BorderStroke(1.dp, Color.Black), RoundedCornerShape(10.dp)
                                    )
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .padding(vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}