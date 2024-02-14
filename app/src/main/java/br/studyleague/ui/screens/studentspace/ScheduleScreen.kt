package br.studyleague.ui.screens.studentspace

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.Subject
import br.studyleague.ui.FetchState
import br.studyleague.ui.components.DefaultDialog
import br.studyleague.ui.components.DefaultIconButtom
import br.studyleague.ui.components.NumberButton
import br.studyleague.ui.components.NumberText
import br.studyleague.ui.components.Schedule
import br.studyleague.ui.components.ScheduleEntryData
import br.studyleague.ui.components.StudentDropdownMenu
import br.studyleague.ui.components.TimePickerDialog
import dtos.SubjectDTO
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime


@Composable
fun ScheduleScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    val studentViewModel = LocalStudentViewModel.current
    val studentUiState by studentViewModel.uiState.collectAsState()

    var fetchState by remember { mutableStateOf<FetchState<Unit>>(FetchState.Empty) }

    LaunchedEffect(Unit) {
        fetchState = FetchState.Loading

        Log.d("ScheduleScreen", "Fetching schedule at launched effect")

        studentViewModel.fetchAllSubjects()
        studentViewModel.fetchSchedule()

        fetchState = FetchState.Loaded(Unit)
    }

    val coroutineScope = rememberCoroutineScope()

    when (fetchState) {
        is FetchState.Loaded -> ScheduleScreenContent(modifier = modifier,
            onDone = {
                coroutineScope.launch {
                    studentViewModel.updateScheduleEntries(it)
                }

                onDone()
            },
            subjects = studentUiState.subjects.getLoadedValue(),
            initialScheduleEntriesGenerator = { studentViewModel.getScheduleEntries(it) })

        else -> {}
    }
}

@Composable
fun ScheduleScreenContent(
    modifier: Modifier,
    onDone: (List<ScheduleEntryData>) -> Unit,
    subjects: List<Subject>,
    initialScheduleEntriesGenerator: ((ScheduleEntryData) -> Unit) -> List<ScheduleEntryData>
) {
    setFullscreenMode(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    var isDialogVisible by remember { mutableStateOf(false) }

    var loadedScheduleEntryData by remember { mutableStateOf(ScheduleEntryData()) }

    var scheduleDialogOnDone by remember { mutableStateOf({ _: ScheduleEntryData -> }) }

    val scheduleEntries = remember { mutableStateListOf<ScheduleEntryData>() }

    LaunchedEffect(Unit) {
        scheduleEntries.addAll(initialScheduleEntriesGenerator {
            loadedScheduleEntryData = it

            scheduleDialogOnDone = { updatedSchedule ->
                scheduleEntries.remove(it)
                scheduleEntries.add(updatedSchedule)

                isDialogVisible = false
            }

            isDialogVisible = true
        })
    }

    Scaffold(modifier = modifier, floatingActionButton = {
        DefaultIconButtom(
            onClick = {
                onDone(scheduleEntries)
            }, modifier = Modifier.padding(bottom = 30.dp, end = 30.dp)
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Adicionar")
        }
    }) { paddingValues ->

        Schedule(
            scheduleEntries = scheduleEntries,
            onGridClick = { dayOfWeek, hour ->

                loadedScheduleEntryData = loadedScheduleEntryData.copy(
                    startTime = LocalTime.of(hour.toInt(), 0),
                    endTime = LocalTime.of(hour.toInt() + 1, 0),
                    dayOfWeek = dayOfWeek
                )

                isDialogVisible = true

                scheduleDialogOnDone = { scheduleEntryData ->
                    loadedScheduleEntryData = scheduleEntryData.copy(onClick = {
                        loadedScheduleEntryData = it

                        scheduleDialogOnDone = { updatedSchedule ->
                            scheduleEntries.remove(it)
                            scheduleEntries.add(updatedSchedule)

                            isDialogVisible = false
                        }

                        isDialogVisible = true
                    })

                    scheduleEntries.add(loadedScheduleEntryData)

                    loadedScheduleEntryData = ScheduleEntryData()

                    isDialogVisible = false
                }
            },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .padding(paddingValues)
                .fillMaxSize()
        )

        if (isDialogVisible) {
            ScheduleEntryInfoDialog(initialScheduleEntry = loadedScheduleEntryData,
                availableSubjects = subjects,
                onDone = scheduleDialogOnDone,
                onDelete = {
                    scheduleEntries.remove(loadedScheduleEntryData)
                    isDialogVisible = false
                },
                onDismissRequest = {
                    isDialogVisible = false
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEntryInfoDialog(
    initialScheduleEntry: ScheduleEntryData,
    availableSubjects: List<Subject>,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    onDone: (ScheduleEntryData) -> Unit
) {
    DefaultDialog(
        onDismissRequest = onDismissRequest, modifier = Modifier.width(300.dp)
    ) {
        var copiedScheduleEntry by remember { mutableStateOf(initialScheduleEntry) }

        var timePickerTime by remember { mutableStateOf(LocalTime.MIDNIGHT) }
        var timePickerCallback by remember { mutableStateOf({ _: LocalTime -> }) }

        var isTimePickerVisible by remember { mutableStateOf(false) }

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp, bottom = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                NumberButton(onClick = {
                    timePickerTime = copiedScheduleEntry.startTime
                    timePickerCallback = {
                        copiedScheduleEntry = copiedScheduleEntry.copy(startTime = it)
                    }

                    isTimePickerVisible = true
                }) {
                    NumberText(text = copiedScheduleEntry.startTime.toString())
                }

                HorizontalDivider(
                    modifier = Modifier.width(35.dp), color = Color.Black
                )

                NumberButton(onClick = {
                    timePickerTime = copiedScheduleEntry.endTime
                    timePickerCallback = {
                        copiedScheduleEntry = copiedScheduleEntry.copy(endTime = it)
                    }

                    isTimePickerVisible = true
                }) {
                    NumberText(text = copiedScheduleEntry.endTime.toString())
                }
            }

            StudentDropdownMenu(
                options = availableSubjects.map { it.subjectDTO.name },
                selectedOptionText = copiedScheduleEntry.content,
                onSelectionChanged = {
                    copiedScheduleEntry = copiedScheduleEntry.copy(content = it)
                },
                isSearchable = false,
                placeholder = { Text("Escolha uma matéria") },
                modifier = Modifier.shadow(1.dp, RoundedCornerShape(10.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    val subject = availableSubjects.find {
                        it.subjectDTO.name == copiedScheduleEntry.content
                    }

                    if (subject != null) {
                        onDone(
                            copiedScheduleEntry.copy(color = subject.color)
                        )
                    } else {
                        onDismissRequest()
                    }

                }) {
                    Text(
                        "Confirmar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black,
                    )
                }

                TextButton(
                    onClick = onDelete,
                ) {
                    Text(
                        "Deletar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black
                    )
                }
            }
        }

        if (isTimePickerVisible) {
            TimePickerDialog(initialTime = timePickerTime,
                onDismissRequest = { isTimePickerVisible = false },
                onDone = {
                    timePickerCallback(it)
                    isTimePickerVisible = false
                })
        }
    }
}

@Composable
private fun setFullscreenMode(screenOrientation: Int) {
    val context = LocalContext.current

    DisposableEffect(screenOrientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation

        activity.requestedOrientation = screenOrientation

        Log.d("ScheduleScreen", "Setting fullscreen mode")

        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // restore original when view disappears
            activity.requestedOrientation = originalOrientation
            WindowCompat.getInsetsController(window, window.decorView)
                .show(WindowInsetsCompat.Type.systemBars())

            Log.d("ScheduleScreen", "Disposing fullscreen mode")
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    ScheduleEntryInfoDialog(initialScheduleEntry = ScheduleEntryData(
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(9, 0),
        dayOfWeek = DayOfWeek.MONDAY,
        content = "Matemática",
        color = Color.Blue
    ), availableSubjects = listOf(
        Subject(
            subjectDTO = SubjectDTO(
                name = "Matemática"
            )
        )
    ), onDismissRequest = { /*TODO*/ }, onDone = { /*TODO*/ }, onDelete = { /*TODO*/ })
}