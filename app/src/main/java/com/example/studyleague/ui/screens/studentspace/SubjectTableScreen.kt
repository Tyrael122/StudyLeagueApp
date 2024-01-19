package com.example.studyleague.ui.screens.studentspace

import androidx.compose.runtime.Composable
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.datagrid.DataGrid
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn


@Composable
fun SubjectTableScreen(navigateToSubject: (Subject) -> Unit) {
    StudentSpaceDefaultColumn {
        DataGrid(
            columns = Subject.columns, items = sampleSubjectList, onItemClick = navigateToSubject
        )
    }
}

val sampleSubjectList = listOf(
    Subject(name = "Matemática", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Português", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "História", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Geografia", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Biologia", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Física", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Química", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Inglês", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Espanhol", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Filosofia", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Sociologia", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Educação Física", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Artes", workload = 60, completedQuestionsPercentage = 80),
    Subject(name = "Redação", workload = 60, completedQuestionsPercentage = 80),
)