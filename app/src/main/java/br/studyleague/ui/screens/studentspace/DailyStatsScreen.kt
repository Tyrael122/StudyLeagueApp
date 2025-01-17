package br.studyleague.ui.screens.studentspace

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.Subject
import br.studyleague.ui.FetchState
import br.studyleague.ui.StudentViewModel
import br.studyleague.ui.Util.calculateColorForGrade
import br.studyleague.ui.Util.formatFloat
import br.studyleague.ui.components.StatisticsSquare
import br.studyleague.ui.components.datagrid.DataGrid
import br.studyleague.ui.screens.StudentSpaceDefaultColumn
import br.studyleague.util.debug
import enums.StudySchedulingMethods
import kotlinx.coroutines.launch


@Composable
fun DailyStatsScreen() {
    val studentViewModel = LocalStudentViewModel.current

    var fetchState by remember { mutableStateOf<FetchState>(FetchState.Empty) }

    LaunchedEffect(Unit) {
        debug("Fetching student stats at daily screen")

        studentViewModel.fetchScheduledSubjectsForDay()
        studentViewModel.fetchStudentStats()

        fetchState = FetchState.Loaded
    }

    when (fetchState) {
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

    val studentStats = uiState.studentStats.studentStatisticsDTO

    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val isUsingSchedule =
            uiState.student.currentStudySchedulingMethod == StudySchedulingMethods.SCHEDULE

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val titleTextStyle = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 12.sp
            )

            val dataTextStyle = TextStyle(
                fontWeight = FontWeight.ExtraBold, fontSize = 25.sp
            )

            StatisticsSquare(
                title = "Nota diária",
                data = formatFloat(studentStats.dailyGrade),
                dataTextStyle = dataTextStyle.copy(color = calculateColorForGrade(studentStats.dailyGrade)),
                titleTextStyle = titleTextStyle
            )

            if (isUsingSchedule) {
                StatisticsSquare(
                    title = "Metas batidas",
                    data = "${studentStats.hoursGoalsCompleted}/${uiState.subjects.size}",
                    dataTextStyle = dataTextStyle,
                    titleTextStyle = titleTextStyle
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val dailyStudentStats = studentStats.dailyStatistic

            StatisticsSquare(title = "Horas", data = dailyStudentStats.hours.toString())
            StatisticsSquare(title = "Questões", data = dailyStudentStats.questions.toString())
            StatisticsSquare(title = "Revisões", data = dailyStudentStats.reviews.toString())
        }

        var isBottomSheetVisible by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        if (isUsingSchedule) {
            DataGrid(isSearchBarVisible = false,
                columns = Subject.columns,
                items = uiState.subjects,
                transformToDataGridView = { it.toDailyStatsView() },
                noContentText = "Nenhuma matéria agendada\npara hoje",
                onItemClick = {
                    studentViewModel.selectSubject(it)

                    isBottomSheetVisible = true
                })
        }

        if (isBottomSheetVisible) {
            val selectedSubjectDailyStats = uiState.selectedSubject.subjectDTO.dailyStatistic

            val data = remember {
                mutableStateListOf(
                    listOf("Horas estudadas", selectedSubjectDailyStats.hours.toString()),
                    listOf("Questões", selectedSubjectDailyStats.questions.toString()),
                    listOf("Revisões", selectedSubjectDailyStats.reviews.toString()),
                )
            }

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
                    data.forEachIndexed { index, stringList ->
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
                                onValueChange = { data[index] = listOf(stringList[0], it) },
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