package com.example.studyleague.ui.screens.studentspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import br.studyleague.dtos.enums.StatisticType
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.Accordion
import com.example.studyleague.ui.components.DefaultIconButtom
import com.example.studyleague.ui.components.DefaultOutlinedTextField
import com.example.studyleague.ui.components.ProgressIndicator
import com.example.studyleague.ui.components.TopBarTitle
import com.example.studyleague.ui.components.TopBarTitleStyles
import com.example.studyleague.ui.screens.StudentSpaceDefaultColumn
import kotlin.math.roundToInt


enum class SubjectScreens(val icon: ImageVector, val label: String) {
    UPDATE(Icons.Outlined.Edit, "Edição"), STATS(Icons.Outlined.BarChart, "Estatísticas")
}

@Composable
fun SubjectScreen() {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()
    val selectedSubject = uiState.selectedSubject

    TopBarTitle.setTitle(selectedSubject.subjectDTO.name, TopBarTitleStyles.medium())

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
            startDestination = SubjectScreens.UPDATE.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            composable(SubjectScreens.UPDATE.name) {
                SubjectUpdateScreen(selectedSubject = selectedSubject)
            }

            composable(SubjectScreens.STATS.name) {
                SubjectStatsScreen(selectedSubject = selectedSubject)
            }
        }
    }
}

@Composable
fun SubjectUpdateScreen(selectedSubject: Subject) {
    val studentViewModel = LocalStudentViewModel.current

    var subjectName by remember { mutableStateOf(selectedSubject.subjectDTO.name) }

    val allTimeGoal = selectedSubject.subjectDTO.allTimeGoals
    val allTimeGoals = listOf(
        mutableListOf("Horas", allTimeGoal.hours.toString()),
        mutableListOf("Questões", allTimeGoal.questions.toString()),
        mutableListOf("Revisões", allTimeGoal.reviews.toString()),
    )

    val weeklyGoal = selectedSubject.subjectDTO.weeklyGoals
    val weeklyGoals = listOf(
        mutableListOf("Questões", weeklyGoal.hours.toString()),
        mutableListOf("Revisões", weeklyGoal.reviews.toString()),
    )

    Scaffold(floatingActionButton = {
        DefaultIconButtom(onClick = {
            studentViewModel.updateSelectedSubjectName(subjectName)
            studentViewModel.updateSelectedSubjectAlltimeGoals(allTimeGoals.map { it[1].toFloat() })
            studentViewModel.updateSelectedSubjectWeeklyGoals(allTimeGoals.map { it[1].toFloat() })
        }) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Adicionar")
        }
    }) { paddingValues ->
        StudentSpaceDefaultColumn(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DefaultOutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                placeholder = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Accordion(title = "Metas - Totais", body = {
                Accordion.TextFieldRow(items = allTimeGoals, onValueChange = { index, string ->
                    allTimeGoals[index][1] = string
                })
            })

            Accordion(title = "Metas - Semanais", body = {
                Accordion.TextRow(items = listOf(listOf("Horas", weeklyGoal.hours.toString())))

                Accordion.TextFieldRow(items = weeklyGoals, onValueChange = { index, string ->
                    weeklyGoals[index][1] = string
                })
            })
        }
    }
}

@Composable
fun SubjectStatsScreen(selectedSubject: Subject) {
    StudentSpaceDefaultColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Accordion(title = "Total", startsExpanded = true, body = {
            val allTimeGoal = selectedSubject.subjectDTO.allTimeGoals
            val allTimeStats = selectedSubject.subjectDTO.allTimeStatistic

            ProgressIndicator(
                header = "Questões",
                target = allTimeGoal.questions,
                current = allTimeStats.questions
            )

            Spacer(Modifier.height(3.dp))

            Accordion.TextRow(
                items = listOf(
                    listOf("Horas", allTimeStats.hours.toString()),
                    listOf("Revisões", allTimeStats.reviews.toString())
                )
            )
        })

        Accordion(title = "Semanal", startsExpanded = true, body = {
            val weeklyGoal = selectedSubject.subjectDTO.weeklyGoals
            val weeklyStats = selectedSubject.subjectDTO.weeklyStatistic

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProgressIndicator(
                    header = "Questões",
                    target = weeklyGoal.questions,
                    current = weeklyStats.questions
                )
                ProgressIndicator(
                    header = "Horas",
                    target = weeklyGoal.hours.roundToInt(),
                    current = weeklyStats.hours.roundToInt()
                )
                ProgressIndicator(
                    header = "Revisões", target = weeklyGoal.reviews, current = weeklyStats.reviews
                )
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