package br.studyleague.ui.screens.studentspace

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.Subject
import br.studyleague.ui.FetchState
import br.studyleague.ui.components.DefaultIconButtom
import br.studyleague.ui.components.datagrid.DataGrid
import br.studyleague.ui.screens.StudentSpaceDefaultColumn


@Composable
fun SubjectTableScreen(navigateToSubject: (Subject) -> Unit, navigateToAddSubjectScreen: () -> Unit) {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("SubjectTableScreen", "Fetching all subjects")

        studentViewModel.fetchAllSubjects()
    }

    when (uiState.subjects) {
        is FetchState.Loaded -> SubjectTableScreenContent(navigateToSubject = navigateToSubject, navigateToAddSubjectScreen = navigateToAddSubjectScreen)
        else -> {}
    }
}

@Composable
fun SubjectTableScreenContent(
    navigateToSubject: (Subject) -> Unit, navigateToAddSubjectScreen: () -> Unit
) {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    Scaffold(floatingActionButton = {
        DefaultIconButtom(
            onClick = navigateToAddSubjectScreen,
            modifier = Modifier.padding(bottom = 15.dp, end = 15.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Adicionar")
        }
    }) { paddingValues ->
        StudentSpaceDefaultColumn(modifier = Modifier.padding(paddingValues)) {
            DataGrid(
                onItemClick = {
                    studentViewModel.selectSubject(it)

                    navigateToSubject(it)
                },
                columns = Subject.columns,
                items = uiState.subjects.getLoadedValue(),
                noContentText = "Nenhuma matéria agendada para hoje"
            )
        }
    }

}
