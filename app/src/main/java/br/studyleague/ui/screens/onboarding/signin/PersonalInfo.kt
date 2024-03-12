package br.studyleague.ui.screens.onboarding.signin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.LocalStudentViewModel
import br.studyleague.ui.components.DefaultFormInput
import br.studyleague.ui.components.DefaultOutlinedTextField
import br.studyleague.ui.components.OnboardingButton
import br.studyleague.ui.components.OnboardingColumn
import br.studyleague.ui.components.OnboardingTitle
import kotlinx.coroutines.launch

@Composable
fun PersonalInfoScreen(navigateToNextScreen: () -> Unit) {
    OnboardingColumn {
        var name by remember { mutableStateOf("") }
        var goal by remember { mutableStateOf("") }
        var studyArea by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingTitle(text = "Um pouco sobre você")

            Spacer(Modifier.height(30.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DefaultFormInput(label = "Qual seu nome?",
                    placeholder = "Seu nome",
                    value = name,
                    onValueChange = { name = it })

                DefaultFormInput(label = "Qual seu objetivo?",
                    placeholder = "Ex.: Residência médica, concursos",
                    value = goal,
                    onValueChange = { goal = it })

                DefaultFormInput(label = "Qual sua área de estudo?",
                    placeholder = "Ex.: Direito, Engenharia",
                    value = studyArea,
                    onValueChange = { studyArea = it })
            }
        }

        val studentViewModel = LocalStudentViewModel.current
        val context = LocalContext.current

        OnboardingButton(onClick = {
            if (name.isEmpty() || goal.isEmpty() || studyArea.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()

                return@OnboardingButton
            }

            studentViewModel.addStudyInfoToStudent(name, goal, studyArea)

            navigateToNextScreen()
        }, text = "CONTINUAR")
    }
}