package br.studyleague.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.R
import br.studyleague.ui.components.OnboardingButton

@Composable
fun OnboardingScreen(navigateToLoginScreen: () -> Unit, navigateToSignUpScreen: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 60.dp, bottom = 30.dp)
            .padding(horizontal = 10.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.studyleaguelogo),
                contentDescription = null
            )

            Spacer(Modifier.height(40.dp))

            Text(
                "Monitore seus estudos e obtenha resultados",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Lembre-se de monitorar suas conquistas diárias",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }

        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            OnboardingButton(onClick = navigateToLoginScreen, text = "ENTRAR")

            TextButton(onClick = navigateToSignUpScreen) {
                Text(
                    "CADASTRAR",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
