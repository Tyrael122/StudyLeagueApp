package com.example.studyleague

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyleague.ui.screens.StudentScreen
import com.example.studyleague.ui.screens.StudentSpace
import com.example.studyleague.ui.screens.onboarding.AddSubjectsScreen
import com.example.studyleague.ui.screens.onboarding.explanation.GoalsExplanationScreen
import com.example.studyleague.ui.screens.onboarding.OnboardingScreen
import com.example.studyleague.ui.screens.onboarding.explanation.ScheduleExplanationScreen
import com.example.studyleague.ui.screens.studentspace.ScheduleScreen
import com.example.studyleague.ui.screens.studentspace.SubjectTableScreen

enum class Screen {
    ONBOARDING, ADD_SUBJECTS, SCHEDULE_EXPLANATION, GOALS_EXPLANATION, STUDENT_SPACE
}

@Composable
fun StudyLeagueApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.STUDENT_SPACE.name) {
        composable(Screen.ONBOARDING.name) {
            OnboardingScreen(navigateToNextScreen = { navController.navigate(Screen.ADD_SUBJECTS.name) })
        }

        composable(Screen.ADD_SUBJECTS.name) {
            AddSubjectsScreen(navigateToNextScreen = { navController.navigate(Screen.SCHEDULE_EXPLANATION.name) })
        }

        composable(Screen.SCHEDULE_EXPLANATION.name) {
            ScheduleExplanationScreen(navigateToNextScreen = { navController.navigate(StudentScreen.SCHEDULE.name) })
        }

        composable(StudentScreen.SCHEDULE.name) {
            ScheduleScreen(onDone = { navController.navigate(Screen.GOALS_EXPLANATION.name) })
        }

        composable(Screen.GOALS_EXPLANATION.name) {
            GoalsExplanationScreen(navigateToNextScreen = { navController.navigate(Screen.STUDENT_SPACE.name) })
        }

        composable(Screen.STUDENT_SPACE.name) {
            StudentSpace()
        }
    }
}
