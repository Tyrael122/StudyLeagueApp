package com.example.studyleague.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyleague.ui.components.DrawerContent
import com.example.studyleague.ui.components.NavigationItem
import com.example.studyleague.ui.components.NavigationItemBuilder
import com.example.studyleague.ui.components.StudentTopBar
import com.example.studyleague.ui.components.TopBarTitle
import com.example.studyleague.ui.screens.studentspace.DailyStatsScreen
import com.example.studyleague.ui.screens.studentspace.GlobalStatsScreen
import com.example.studyleague.ui.screens.studentspace.ScheduleScreen
import com.example.studyleague.ui.screens.studentspace.SubjectScreen
import com.example.studyleague.ui.screens.studentspace.SubjectTableScreen
import kotlinx.coroutines.launch


@Composable
fun StudentSpace(
    modifier: Modifier = Modifier,
    hasCompletedOnboarding: Boolean,
    bottomBar: @Composable () -> Unit = {},
) {
    val navController = rememberNavController()
    var currentRoute by remember {
        mutableStateOf(
            navController.currentBackStackEntry?.destination?.route
                ?: StudentScreen.GLOBAL_STATS.name
        )
    }

    navController.addOnDestinationChangedListener(listener = { controller, destination, arguments ->
        currentRoute = destination.route ?: StudentScreen.GLOBAL_STATS.name
    })

    val navItems = createNavigationItems(navController)

    val isCompactMode = currentRoute == StudentScreen.SCHEDULE.name

    StudentNavigationDrawer(
        currentRoute = currentRoute, navigationItems = navItems, isCompactMode = isCompactMode
    ) { openNavDrawer ->
        Scaffold(bottomBar = bottomBar, topBar = {
            if (!isCompactMode) {
                StudentTopBar(title = TopBarTitle.buildTextComposable(), onNavigationIconClick = {
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
                    startDestination = if (hasCompletedOnboarding) StudentScreen.GLOBAL_STATS.name else StudentScreen.SUBJECTS_TABLE.name
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
        composable(StudentScreen.GLOBAL_STATS.name) {
            GlobalStatsScreen()
        }

        composable(StudentScreen.DAILY_STATS.name) {
            DailyStatsScreen()
        }

        composable(StudentScreen.SUBJECTS_TABLE.name) {
            SubjectTableScreen(navigateToSubject = { navController.navigate(StudentScreen.SUBJECT.name) })
        }

        composable(StudentScreen.SUBJECT.name) {
            SubjectScreen()
        }

        composable(StudentScreen.SCHEDULE.name) {
            ScheduleScreen(onDone = { navController.navigate(StudentScreen.GLOBAL_STATS.name) })
        }
    }
}

@Composable
fun StudentNavigationDrawer(
    modifier: Modifier = Modifier,
    currentRoute: String,
    isCompactMode: Boolean = false,
    navigationItems: List<NavigationItem>,
    content: @Composable (() -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(modifier = modifier, drawerState = drawerState, drawerContent = {
        DrawerContent(items = navigationItems,
            currentRoute = currentRoute,
            isCompactMode = isCompactMode,
            onLogout = { /*TODO*/ },
            closeDrawer = {
                scope.launch {
                    drawerState.close()
                }
            })
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

fun createNavigationItems(navController: NavHostController): List<NavigationItem> {
    val navItemBuilder = NavigationItemBuilder(navController)

    for (screen in StudentScreen.entries) {
        if (screen == StudentScreen.SUBJECT) continue

        navItemBuilder.addNavigationItem(
            label = screen.label, route = screen.name, imageVector = screen.icon
        )
    }

    return navItemBuilder.build()
}

enum class StudentScreen(val icon: ImageVector, val label: String) {
    GLOBAL_STATS(
        Icons.Filled.House, "Geral"
    ),
    DAILY_STATS(Icons.Filled.House, "Diário"), SCHEDULE(
        Icons.Filled.House, "Cronograma"
    ),
    SUBJECTS_TABLE(
        Icons.Filled.House, "Matérias"
    ),
    SUBJECT(Icons.Filled.House, ""),
}