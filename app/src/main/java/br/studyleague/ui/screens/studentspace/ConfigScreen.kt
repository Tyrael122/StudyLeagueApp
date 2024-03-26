package br.studyleague.ui.screens.studentspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.LocalStudentViewModel
import br.studyleague.ui.screens.StudentSpaceDefaultColumn
import enums.StudySchedulingMethods
import kotlinx.coroutines.launch


@Composable
fun ConfigScreen() {
    val studentViewModel = LocalStudentViewModel.current

    val uiState by studentViewModel.uiState.collectAsState()

    StudentSpaceDefaultColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Usa o ciclo de estudos?",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
            )

            val coroutineScope = rememberCoroutineScope()
            Switch(checked = uiState.student.currentStudySchedulingMethod == StudySchedulingMethods.STUDYCYCLE,
                onCheckedChange = {
                    val newMethod =
                        if (it) StudySchedulingMethods.STUDYCYCLE else StudySchedulingMethods.SCHEDULE

                    coroutineScope.launch {
                        studentViewModel.changeScheduleMethod(newMethod)
                    }
                })
        }
    }
}

@Preview
@Composable
fun ConfigScreenPreview() {
    ConfigScreen()
}

