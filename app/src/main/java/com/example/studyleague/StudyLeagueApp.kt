package com.example.studyleague

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyleague.data.DataStoreManager
import com.example.studyleague.ui.StudentViewModel
import com.example.studyleague.ui.screens.StudentScreen
import com.example.studyleague.ui.screens.StudentSpace
import com.example.studyleague.ui.screens.onboarding.AddSubjectsScreen
import com.example.studyleague.ui.screens.onboarding.OnboardingScreen
import com.example.studyleague.ui.screens.onboarding.explanation.GoalsExplanationScreen
import com.example.studyleague.ui.screens.onboarding.explanation.ScheduleExplanationScreen
import com.example.studyleague.ui.screens.studentspace.ScheduleScreen

enum class Screen {
    ONBOARDING, ADD_SUBJECTS, SCHEDULE_EXPLANATION, GOALS_EXPLANATION, STUDENT_SPACE
}

val LocalStudentViewModel = compositionLocalOf<StudentViewModel> { error("No StudentViewModel found!") }

@Composable
fun StudyLeagueApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

//    val hasCompletedOnboarding = runBlocking { getBooleanValueFromDataStore(context, hasCompletedOnboardingKey) }
    val hasCompletedOnboarding = false

    val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModel.factory(DataStoreManager(context)))

    CompositionLocalProvider(LocalStudentViewModel provides studentViewModel) {
        NavHost(
            navController = navController,
//        startDestination = if (hasCompletedOnboarding) Screen.STUDENT_SPACE.name else Screen.ONBOARDING.name
            startDestination = Screen.ONBOARDING.name
        ) {
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
                StudentSpace(hasCompletedOnboarding = hasCompletedOnboarding)

                if (!hasCompletedOnboarding) {
//                    runBlocking {
//                        setDataStoreValue(
//                            true, context, DataStoreKeys.hasCompletedOnboardingKey
//                        )
//                    }
                }
            }
        }
    }
}