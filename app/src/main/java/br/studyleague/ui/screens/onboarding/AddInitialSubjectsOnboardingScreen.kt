package br.studyleague.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.Subject
import br.studyleague.ui.components.DefaultOutlinedTextField
import br.studyleague.ui.components.OnboardingButton
import br.studyleague.ui.components.OnboardingColumn
import br.studyleague.ui.components.OnboardingTitle
import dtos.SubjectDTO
import kotlinx.coroutines.launch

@Composable
fun AddInitialSubjectsOnboardingScreen(navigateToNextScreen: () -> Unit) {
    val subjects = remember {
        mutableStateListOf<String>()
    }

    OnboardingColumn {
        OnboardingTitle(text = "Quais as matérias que você estuda?")

        Spacer(Modifier.height(30.dp))

        var currentSubject by remember { mutableStateOf("") }

        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1F)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Adicione as matérias apertando 'Enter' no teclado.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
            )

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

        Spacer(Modifier.height(20.dp))

        val studentViewModel = LocalStudentViewModel.current
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        OnboardingButton(onClick = {
            if (subjects.isEmpty()) {
                Toast.makeText(context, "Adicione pelo menos uma matéria", Toast.LENGTH_SHORT).show()

                return@OnboardingButton
            }

            coroutineScope.launch {
                studentViewModel.addSubjects(subjects = subjects.map {
                    val subjectDto = SubjectDTO()
                    subjectDto.name = it

                    Subject(subjectDTO = subjectDto)
                })

                navigateToNextScreen()
            }
        }, text = "CONTINUAR")
    }
}

@Preview
@Composable
fun AddInitialSubjectsOnboardingScreenPreview() {
    AddInitialSubjectsOnboardingScreen(navigateToNextScreen = {})
}
