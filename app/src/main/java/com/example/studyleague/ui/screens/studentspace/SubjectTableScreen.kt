package com.example.studyleague.ui.screens.studentspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.datagrid.DataGrid
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn


@Composable
fun SubjectTableScreen(navigateToSubject: (Subject) -> Unit) {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    StudentSpaceDefaultColumn {
        DataGrid(columns = Subject.columns, items = uiState.subjects, onItemClick = {
            navigateToSubject(it)
            studentViewModel.selectSubject(it)
        })
    }
}