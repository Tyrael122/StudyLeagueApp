package com.example.studyleague.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.ui.components.DefaultOutlinedTextField
import com.example.studyleague.ui.components.OnboardingButton
import com.example.studyleague.ui.components.OnboardingColumn
import com.example.studyleague.ui.components.OnboardingTitle
import kotlinx.coroutines.launch

@Composable
fun PersonalInfoScreen(navigateToNextScreen: () -> Unit) {
    OnboardingColumn {
        val data = remember {
            mutableStateListOf(
                listOf("Qual seu nome?", "Seu nome", ""),
                listOf("Qual seu objetivo?", "Ex.: Residência médica, concursos", ""),
                listOf("Qual sua área de estudo?", "Ex.: Direito, Engenharia", "")
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingTitle(text = "Um pouco sobre você")

            Spacer(Modifier.height(30.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data.forEachIndexed { index, stringList ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringList[0],
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 7.dp)
                        )

                        DefaultOutlinedTextField(
                            value = stringList[2],
                            onValueChange = {
                                data[index] = listOf(stringList[0], stringList[1], it)
                            },
                            placeholder = { Text(stringList[1]) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFEFEFEF),
                                focusedContainerColor = Color(0xFFEFEFEF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        val studentViewModel = LocalStudentViewModel.current
        val coroutineScope = rememberCoroutineScope()

        OnboardingButton(onClick = {
            val name = data[0][2]
            val goal = data[1][2]
            val studyArea = data[2][2]

            coroutineScope.launch {
                val isCreationSucessful = studentViewModel.createStudent(name, goal, studyArea)
                if (isCreationSucessful) {
                    navigateToNextScreen()
                }
            }
        }, text = "CONTINUAR")
    }
}