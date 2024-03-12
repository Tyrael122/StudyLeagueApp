package br.studyleague.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.studyleague.LocalStudentViewModel
import br.studyleague.ui.components.DrawerContent
import br.studyleague.ui.components.NavigationItem
import br.studyleague.ui.components.NavigationItemBuilder
import br.studyleague.ui.components.StudentTopBar
import br.studyleague.ui.components.TopBarTitleHelper
import br.studyleague.ui.components.TopBarTitleStyles
import br.studyleague.ui.screens.studentspace.AddSubjectScreen
import br.studyleague.ui.screens.studentspace.DailyStatsScreen
import br.studyleague.ui.screens.studentspace.GlobalStatsScreen
import br.studyleague.ui.screens.studentspace.ScheduleScreen
import br.studyleague.ui.screens.studentspace.SubjectScreen
import br.studyleague.ui.screens.studentspace.SubjectTableScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Composable
fun StudentSpace(
    modifier: Modifier = Modifier, hasCompletedOnboarding: Boolean, onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var currentRoute by remember {
        mutableStateOf(
            navController.currentBackStackEntry?.destination?.route
                ?: StudentScreens.GLOBAL_STATS.name
        )
    }

    navController.addOnDestinationChangedListener(listener = { controller, destination, arguments ->
        currentRoute = destination.route ?: StudentScreens.GLOBAL_STATS.name
    })

    val navItems = createNavigationItems(navController)

    val isCompactMode = currentRoute == StudentScreens.SCHEDULE.name

    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()
    val student = uiState.student.studentDTO

    StudentNavigationDrawer(
        studentName = student.name,
        studentGoal = student.goal,
        currentRoute = currentRoute,
        navigationItems = navItems,
        isCompactMode = isCompactMode,
        onLogout = onLogout
    ) { openNavDrawer ->
        Scaffold(topBar = {
            if (!isCompactMode) {
                StudentTopBar(title = topBarTitle(currentRoute), onNavigationIconClick = {
                    openNavDrawer()
                })
            }
        }, modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                StudentNavGraph(
                    navController = navController,
                    startDestination = if (hasCompletedOnboarding) StudentScreens.GLOBAL_STATS.name else StudentScreens.SUBJECTS_TABLE.name
                )
            }
        }
    }
}

@Composable
fun StudentNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(StudentScreens.GLOBAL_STATS.name) {
            GlobalStatsScreen()
        }

        composable(StudentScreens.DAILY_STATS.name) {
            DailyStatsScreen()
        }

        composable(StudentScreens.SUBJECTS_TABLE.name) {
            SubjectTableScreen(navigateToSubject = { navController.navigate(StudentScreens.SUBJECT.name) },
                navigateToAddSubjectScreen = { navController.navigate(StudentScreens.ADD_SUBJECT.name) })
        }

        composable(StudentScreens.SUBJECT.name) {
            SubjectScreen(onDeleteSubject = { navController.navigate(StudentScreens.SUBJECTS_TABLE.name) })
        }

        composable(StudentScreens.ADD_SUBJECT.name) {
            AddSubjectScreen(onDone = { navController.navigate(StudentScreens.SUBJECTS_TABLE.name) })
        }

        composable(StudentScreens.SCHEDULE.name) {
            ScheduleScreen(onDone = { navController.navigate(StudentScreens.GLOBAL_STATS.name) })
        }
    }
}

@Composable
fun StudentNavigationDrawer(
    modifier: Modifier = Modifier,
    currentRoute: String,
    isCompactMode: Boolean = false,
    studentName: String,
    studentGoal: String,
    navigationItems: List<NavigationItem>,
    onLogout: () -> Unit,
    content: @Composable (() -> Unit) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val studentViewModel = LocalStudentViewModel.current
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(modifier = modifier, drawerState = drawerState, drawerContent = {
        DrawerContent(items = navigationItems,
            currentRoute = currentRoute,
            closeDrawer = {
                scope.launch {
                    drawerState.close()
                }
            },
            onLogout = {
                coroutineScope.launch {
                    studentViewModel.logout()

                    scope.launch {
                        drawerState.close()
                    }

                    onLogout()
                }
            },
            isCompactMode = isCompactMode,
            userInfoTitle = studentName,
            userInfoSubtitle = studentGoal
        )
    }) {
        content { scope.launch { drawerState.open() } }
    }
}

@Composable
fun StudentSpaceDefaultColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        modifier = modifier.padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        content()
    }
}

@Composable
fun topBarTitle(currentRoute: String): @Composable () -> Unit {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    return when (currentRoute) {
        StudentScreens.SUBJECT.name -> {
            val selectedSubject = uiState.selectedSubject
            TopBarTitleHelper.buildTextComposable(
                selectedSubject.subjectDTO.name, TopBarTitleStyles.medium()
            )
        }

        StudentScreens.DAILY_STATS.name -> {
            val currentConfiguration = LocalConfiguration.current

            runBlocking {
                val currentDayOfWeek = studentViewModel.fetchServerCurrentTime().dayOfWeek
                val newTitle = currentDayOfWeek.getDisplayName(
                    java.time.format.TextStyle.FULL, currentConfiguration.locales[0]
                ).replaceFirstChar { it.uppercase() }

                TopBarTitleHelper.buildTextComposable(newTitle)
            }
        }

        else -> {
            TopBarTitleHelper.buildTextComposable(uiState.student.studentDTO.goal)
        }
    }
}

fun createNavigationItems(navController: NavHostController): List<NavigationItem> {
    val navItemBuilder = NavigationItemBuilder(navController)

    for (screen in StudentScreens.entries) {
        if (invisibleRoutes.contains(screen)) continue

        navItemBuilder.addNavigationItem(
            label = screen.label, route = screen.name, imageVector = screen.icon
        )
    }

    return navItemBuilder.build()
}

enum class StudentScreens(val icon: ImageVector, val label: String) {
    GLOBAL_STATS(
        Icons.Filled.House, "Total"
    ),
    DAILY_STATS(Icons.Filled.House, "Diário"), SCHEDULE(
        Icons.Filled.House, "Cronograma"
    ),
    SUBJECTS_TABLE(
        Icons.Filled.House, "Matérias"
    ),
    SUBJECT(Icons.Filled.House, ""), ADD_SUBJECT(Icons.Filled.House, ""),
}

val invisibleRoutes = listOf(StudentScreens.ADD_SUBJECT, StudentScreens.SUBJECT)