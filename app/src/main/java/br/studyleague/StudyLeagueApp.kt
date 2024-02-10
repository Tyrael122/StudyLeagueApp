package br.studyleague

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.studyleague.data.DataStoreKeys
import br.studyleague.data.DataStoreManager
import br.studyleague.ui.StudentViewModel
import br.studyleague.ui.screens.StudentScreen
import br.studyleague.ui.screens.StudentSpace
import br.studyleague.ui.screens.onboarding.AddInitialSubjectsOnOnboardingScreen
import br.studyleague.ui.screens.onboarding.OnboardingScreen
import br.studyleague.ui.screens.onboarding.PersonalInfoScreen
import br.studyleague.ui.screens.onboarding.explanation.GoalsExplanationScreen
import br.studyleague.ui.screens.onboarding.explanation.ScheduleExplanationScreen
import br.studyleague.ui.screens.studentspace.ScheduleScreen
import kotlinx.coroutines.runBlocking

enum class Screen {
    ONBOARDING, PERSONAL_INFO, ADD_SUBJECTS, SCHEDULE_EXPLANATION, GOALS_EXPLANATION, STUDENT_SPACE
}

val LocalStudentViewModel =
    compositionLocalOf<StudentViewModel> { error("No StudentViewModel found!") }

@Composable
fun StudyLeagueApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dataStoreManager = DataStoreManager(context)

    val studentViewModel: StudentViewModel =
        viewModel(factory = StudentViewModel.factory(dataStoreManager))

    val hasCompletedOnboarding = runBlocking {
        dataStoreManager.getValueFromDataStore(
            DataStoreKeys.hasCompletedOnboardingKey
        ) ?: false
    }

    CompositionLocalProvider(LocalStudentViewModel provides studentViewModel) {
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) Screen.STUDENT_SPACE.name else Screen.ONBOARDING.name
        ) {
            composable(Screen.ONBOARDING.name) {
                OnboardingScreen(navigateToNextScreen = { navController.navigate(Screen.PERSONAL_INFO.name) })
            }

            composable(Screen.PERSONAL_INFO.name) {
                PersonalInfoScreen(navigateToNextScreen = { navController.navigate(Screen.ADD_SUBJECTS.name) })
            }

            composable(Screen.ADD_SUBJECTS.name) {
                AddInitialSubjectsOnOnboardingScreen(navigateToNextScreen = {
                    navController.navigate(
                        Screen.SCHEDULE_EXPLANATION.name
                    )
                })
            }

            composable(Screen.SCHEDULE_EXPLANATION.name) {
                ScheduleExplanationScreen(navigateToNextScreen = {
                    navController.navigate(
                        StudentScreen.SCHEDULE.name
                    )
                })
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
                    runBlocking {
                        dataStoreManager.setDataStoreValue(
                            DataStoreKeys.hasCompletedOnboardingKey, true
                        )
                    }
                }
            }
        }
    }
}