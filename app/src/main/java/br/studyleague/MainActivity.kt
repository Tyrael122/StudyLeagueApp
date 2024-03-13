package br.studyleague

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.studyleague.ui.theme.StudyLeagueTheme
import br.studyleague.util.CustomLogger
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudyLeagueTheme(darkTheme = false, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    StudyLeagueApp()
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(error: Throwable? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Alguma coisa deu errado.",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFF44336),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CustomLogger.d("ErrorScreen", "Informações técnicas: ${error?.message ?: "Erro desconhecido"}")

        Text(
            text = "Informações técnicas: ${error?.message ?: "Erro desconhecido"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}