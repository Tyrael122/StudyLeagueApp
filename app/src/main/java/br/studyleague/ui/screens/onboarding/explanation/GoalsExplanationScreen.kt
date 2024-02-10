package br.studyleague.ui.screens.onboarding.explanation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.ui.components.OnboardingButton
import br.studyleague.ui.components.OnboardingColumn
import br.studyleague.ui.components.OnboardingTitle


@Composable
fun GoalsExplanationScreen(navigateToNextScreen: () -> Unit) {
    OnboardingColumn {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingTitle("Quais são suas metas?")

            Spacer(Modifier.height(30.dp))

            Text(
                "Na próxima tela você poderá adicionar metas de estudo para suas matérias clicando nelas.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
            )
        }

        OnboardingButton(onClick = navigateToNextScreen, text = "ADICIONAR METAS")
    }
}