package br.studyleague.ui.screens.onboarding.signin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.studyleague.LocalStudentViewModel
import br.studyleague.ui.components.DefaultFormInput
import br.studyleague.ui.components.DefaultPasswordField
import br.studyleague.ui.components.OnboardingButton
import br.studyleague.ui.components.OnboardingColumn
import br.studyleague.ui.components.OnboardingTitle
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navigateToNextScreen: () -> Unit) {
    OnboardingColumn {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingTitle(text = "Bem vindo(a) de volta!")

            Spacer(Modifier.height(30.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DefaultFormInput(label = "Qual seu email?",
                    placeholder = "Seu email",
                    value = email,
                    onValueChange = { email = it })

                DefaultFormInput(label = "Qual sua senha?") {
                    DefaultPasswordField(password = password, onValueChange = { password = it })
                }
            }
        }

        val studentViewModel = LocalStudentViewModel.current
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        OnboardingButton(onClick = {
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()

                return@OnboardingButton
            }

            coroutineScope.launch {
                try {
                    studentViewModel.login(email, password)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

                    return@launch
                }

                navigateToNextScreen()
            }
        }, text = "ENTRAR")
    }
}