package com.example.studyleague

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyleague.ui.screens.StudentScreen
import com.example.studyleague.ui.screens.StudentSpace
import com.example.studyleague.ui.screens.onboarding.AddSubjectsScreen
import com.example.studyleague.ui.screens.onboarding.OnboardingScreen
import com.example.studyleague.ui.screens.onboarding.explanation.GoalsExplanationScreen
import com.example.studyleague.ui.screens.onboarding.explanation.ScheduleExplanationScreen
import com.example.studyleague.ui.screens.studentspace.ScheduleScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class Screen {
    ONBOARDING, ADD_SUBJECTS, SCHEDULE_EXPLANATION, GOALS_EXPLANATION, STUDENT_SPACE
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val hasCompletedOnboardingKey = booleanPreferencesKey("hasCompletedOnboarding")

@Composable
fun StudyLeagueApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val hasCompletedOnboarding = runBlocking { getBooleanValueFromDataStore(context, hasCompletedOnboardingKey) }

    NavHost(
        navController = navController,
        startDestination = if (hasCompletedOnboarding) Screen.STUDENT_SPACE.name else Screen.ONBOARDING.name
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
                runBlocking { setBooleanValueFromDataStore(true, context, hasCompletedOnboardingKey) }
            }
        }
    }
}

suspend fun getBooleanValueFromDataStore(context: Context, key: Preferences.Key<Boolean>): Boolean {
    return context.dataStore.data.map { preferences ->
        preferences[key] ?: false
    }.first()
}

suspend fun setBooleanValueFromDataStore(
    newValue: Boolean, context: Context, key: Preferences.Key<Boolean>
) {
    context.dataStore.edit { settings ->
        settings[key] = newValue
    }
}