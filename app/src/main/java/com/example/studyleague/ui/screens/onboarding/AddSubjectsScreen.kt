package com.example.studyleague.ui.screens.onboarding

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.DefaultOutlinedTextField
import com.example.studyleague.ui.components.OnboardingButton
import dtos.SubjectDTO

@Composable
fun AddSubjectsScreen(navigateToNextScreen: () -> Unit) {
    val subjects = remember {
        mutableStateListOf<String>()
    }

    LaunchedEffect(Unit) {
        Log.d("AddSubjectsScreen", "has passed here, inside launched effect")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 20.dp, bottom = 30.dp)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quais as matérias que você estuda?",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(Modifier.height(30.dp))

            var currentSubject by remember { mutableStateOf("") }

            Column(
                verticalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                val placeholder = @Composable {
                    Text("Escreva aqui sua matéria")
                }

                subjects.forEachIndexed { index, it ->
                    DefaultOutlinedTextField(
                        value = it,
                        onValueChange = { subjects[index] = it },
                        placeholder = placeholder,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                DefaultOutlinedTextField(
                    value = currentSubject,
                    onValueChange = { currentSubject = it },
                    placeholder = placeholder,
                    keyboardActions = KeyboardActions(onDone = {
                        subjects.add(currentSubject)
                        currentSubject = ""
                    }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val studentViewModel = LocalStudentViewModel.current
        OnboardingButton(onClick = {
            studentViewModel.addSubjects(subjects = subjects.map {
                val subjectDto = SubjectDTO()
                subjectDto.name = it

                Subject(subjectDTO = subjectDto)
            })

            navigateToNextScreen()
        }, text = "CONTINUAR")
    }
}