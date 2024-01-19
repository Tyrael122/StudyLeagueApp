package com.example.studyleague.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.ui.components.DefaultOutlinedTextField
import com.example.studyleague.ui.components.OnboardingButton

@Composable
fun AddSubjectsScreen(navigateToNextScreen: () -> Unit) {
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

            val subjects = listOf("Direito constitucional", "Medicina", "Física quântica", "")

            Column(
                verticalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                subjects.forEach {
                    DefaultOutlinedTextField(value = it, onValueChange = { }, placeholder = {
                        Text("Escreva aqui sua matéria")
                    }, modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        OnboardingButton(onClick = navigateToNextScreen, text = "CONTINUAR")
    }
}