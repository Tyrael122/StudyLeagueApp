package com.example.studyleague.ui.screens.studentspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studyleague.ui.components.Accordion
import com.example.studyleague.ui.components.AccordionBody
import com.example.studyleague.ui.components.DefaultOutlinedTextField
import com.example.studyleague.ui.components.ProgressIndicator
import com.example.studyleague.ui.components.TopBarTitle
import com.example.studyleague.ui.components.TopBarTitleStyles
import com.example.studyleague.ui.screens.StudentSpace
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn


enum class SubjectScreens(val icon: ImageVector, val label: String) {
    UPDATE(Icons.Outlined.Edit, "Edição"), STATS(Icons.Outlined.BarChart, "Estatísticas")
}

@Composable
fun SubjectScreen() {
    TopBarTitle.setTitle("Direito constitucional", TopBarTitleStyles.medium())

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(bottomBar = {
        DefaultBottomNavBar(currentDestination = currentDestination, onDestinationChanged = {
            navController.navigate(it)
        })
    }) {
        NavHost(
            navController = navController,
            startDestination = SubjectScreens.STATS.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            composable(SubjectScreens.UPDATE.name) {
                SubjectUpdateScreen()
            }

            composable(SubjectScreens.STATS.name) {
                SubjectStatsScreen()
            }
        }
    }
}

@Composable
fun SubjectUpdateScreen() {
    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DefaultOutlinedTextField(value = "",
            onValueChange = {},
            placeholder = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Accordion(title = "Metas - Totais", body = {
            AccordionBody.TextFieldRow(items = listOf(listOf("Questões", "12")), onValueChange = {})
        })

        Accordion(title = "Metas - Semanais", body = {
            AccordionBody.TextFieldRow(items = listOf(
                listOf("Horas", "12"), listOf("Revisões", "1234"), listOf("Questões", "12")
            ), onValueChange = {})
        })
    }
}

@Composable
fun SubjectStatsScreen() {
    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Accordion(title = "Total", initialExpandedState = true, body = {
            ProgressIndicator(header = "Questões", total = 1000, current = 234)
            ProgressIndicator(header = "Horas", total = 232, current = 123)
            ProgressIndicator(header = "Revisões", total = 100, current = 12)
        })

        Accordion(title = "Semanal", initialExpandedState = true, body = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProgressIndicator(header = "Questões", total = 100, current = 23)
                ProgressIndicator(header = "Horas", total = 100, current = 50)
                ProgressIndicator(header = "Revisões", total = 100, current = 74)
            }
        })
    }
}

@Composable
fun DefaultBottomNavBar(
    currentDestination: NavDestination?, onDestinationChanged: (String) -> Unit
) {
    val screens = listOf(SubjectScreens.UPDATE, SubjectScreens.STATS)

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(5.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        screens.forEach {
            NavigationBarItem(
                selected = currentDestination?.route == it.name,
                onClick = { onDestinationChanged(it.name) },
                label = { Text(it.label, fontSize = 12.sp, fontWeight = FontWeight.Normal) },
                icon = {
                    Icon(
                        it.icon, contentDescription = null
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFEEEEEE)
                )
            )
        }
    }
}