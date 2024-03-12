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
import br.studyleague.ui.screens.StudentScreens
import br.studyleague.ui.screens.StudentSpace
import br.studyleague.ui.screens.onboarding.AddInitialSubjectsOnboardingScreen
import br.studyleague.ui.screens.onboarding.OnboardingScreen
import br.studyleague.ui.screens.onboarding.signin.PersonalInfoScreen
import br.studyleague.ui.screens.onboarding.explanation.GoalsExplanationScreen
import br.studyleague.ui.screens.onboarding.explanation.ScheduleExplanationScreen
import br.studyleague.ui.screens.onboarding.signin.LoginScreen
import br.studyleague.ui.screens.onboarding.signin.SignUpScreen
import br.studyleague.ui.screens.studentspace.ScheduleScreen
import kotlinx.coroutines.runBlocking

enum class OnboardingScreens {
    ONBOARDING, FILL_PERSONAL_INFO, ADD_SUBJECTS, SCHEDULE_EXPLANATION, GOALS_EXPLANATION, STUDENT_SPACE, LOGIN, SIGN_UP
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

    var hasCompletedOnboarding = runBlocking {
        dataStoreManager.getValueFromDataStore(
            DataStoreKeys.hasCompletedOnboardingKey
        ) ?: false
    }

    CompositionLocalProvider(LocalStudentViewModel provides studentViewModel) {
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) OnboardingScreens.STUDENT_SPACE.name else OnboardingScreens.ONBOARDING.name
        ) {
            composable(OnboardingScreens.ONBOARDING.name) {
                OnboardingScreen(navigateToLoginScreen = { navController.navigate(OnboardingScreens.LOGIN.name) },
                    navigateToSignUpScreen = { navController.navigate(OnboardingScreens.FILL_PERSONAL_INFO.name) })
            }

            composable(OnboardingScreens.FILL_PERSONAL_INFO.name) {
                PersonalInfoScreen(navigateToNextScreen = { navController.navigate(OnboardingScreens.SIGN_UP.name) })
            }

            composable(OnboardingScreens.LOGIN.name) {
                LoginScreen {
                    navController.navigate(OnboardingScreens.STUDENT_SPACE.name)

                    if (!hasCompletedOnboarding) {
                        setOnboardingAsComplete(dataStoreManager)
                        hasCompletedOnboarding = true
                    }
                }
            }

            composable(OnboardingScreens.SIGN_UP.name) {
                SignUpScreen { navController.navigate(OnboardingScreens.ADD_SUBJECTS.name) }
            }

            composable(OnboardingScreens.ADD_SUBJECTS.name) {
                AddInitialSubjectsOnboardingScreen(navigateToNextScreen = {
                    navController.navigate(
                        OnboardingScreens.SCHEDULE_EXPLANATION.name
                    )
                })
            }

            composable(OnboardingScreens.SCHEDULE_EXPLANATION.name) {
                ScheduleExplanationScreen(navigateToNextScreen = {
                    navController.navigate(
                        StudentScreens.SCHEDULE.name
                    )
                })
            }

            composable(StudentScreens.SCHEDULE.name) {
                ScheduleScreen(onDone = { navController.navigate(OnboardingScreens.GOALS_EXPLANATION.name) })
            }

            composable(OnboardingScreens.GOALS_EXPLANATION.name) {
                GoalsExplanationScreen(navigateToNextScreen = {
                    navController.navigate(
                        OnboardingScreens.STUDENT_SPACE.name
                    )
                })
            }

            composable(OnboardingScreens.STUDENT_SPACE.name) {
                StudentSpace(hasCompletedOnboarding = hasCompletedOnboarding)

                if (!hasCompletedOnboarding) {
                    setOnboardingAsComplete(dataStoreManager)
                    hasCompletedOnboarding = true
                }
            }
        }
    }
}

private fun setOnboardingAsComplete(dataStoreManager: DataStoreManager) {
    runBlocking {
        dataStoreManager.setDataStoreValue(
            DataStoreKeys.hasCompletedOnboardingKey, true
        )
    }
}