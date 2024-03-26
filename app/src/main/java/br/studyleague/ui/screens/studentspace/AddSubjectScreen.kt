package br.studyleague.ui.screens.studentspace

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.Subject
import br.studyleague.ui.components.DefaultIconButtom
import br.studyleague.ui.components.DefaultOutlinedTextField
import br.studyleague.ui.screens.StudentSpaceDefaultColumn
import dtos.SubjectDTO
import kotlinx.coroutines.launch

@Composable
fun AddSubjectScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    var subjectName by remember { mutableStateOf("") }

    Scaffold(modifier = modifier, floatingActionButton = {
        val studentViewModel = LocalStudentViewModel.current
        val coroutineScope = rememberCoroutineScope()


        DefaultIconButtom(
            onClick = {
                coroutineScope.launch {
                    val subjectDTO = SubjectDTO()
                    subjectDTO.name = subjectName
                    val subjectToAdd = Subject(subjectDTO)

                    studentViewModel.addSubjects(listOf(subjectToAdd))

                    onDone()
                }
            }, modifier = Modifier.padding(bottom = 15.dp, end = 15.dp)
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Adicionar")
        }
    }) { paddingValues ->
        StudentSpaceDefaultColumn(modifier = Modifier.padding(paddingValues)) {

            DefaultOutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                placeholder = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}