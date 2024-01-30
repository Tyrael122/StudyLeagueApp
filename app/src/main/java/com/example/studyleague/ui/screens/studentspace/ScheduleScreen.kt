package com.example.studyleague.ui.screens.studentspace

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.studyleague.LocalStudentViewModel
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.DefaultDialog
import com.example.studyleague.ui.components.DefaultIconButtom
import com.example.studyleague.ui.components.NumberButton
import com.example.studyleague.ui.components.NumberText
import com.example.studyleague.ui.components.Schedule
import com.example.studyleague.ui.components.ScheduleEntryData
import com.example.studyleague.ui.components.StudentDropdownMenu
import com.example.studyleague.ui.components.TimePickerDialog
import java.time.LocalTime


@Composable
fun ScheduleScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    setFullscreenMode(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val studentViewModel = LocalStudentViewModel.current
    val studentUiState by studentViewModel.uiState.collectAsState()

    val scheduleEntries = remember { studentViewModel.getScheduleEntries().toMutableList() }

    Scaffold(modifier = modifier, floatingActionButton = {
        DefaultIconButtom(
            onClick = {
                studentViewModel.updateScheduleEntries(scheduleEntries)

                onDone()

            }, modifier = Modifier
                .padding(bottom = 30.dp, end = 30.dp)
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Adicionar")
        }
    }) { paddingValues ->

        var isDialogVisible by remember { mutableStateOf(false) }

        var loadedScheduleEntryData by remember { mutableStateOf(ScheduleEntryData()) }

        var scheduleDialogOnDone by remember { mutableStateOf({ _: ScheduleEntryData -> }) }

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
                availableSubjects = studentUiState.subjects,
                onDone = scheduleDialogOnDone,
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
    onDone: (ScheduleEntryData) -> Unit
) {
    DefaultDialog(
        onDismissRequest = onDismissRequest, modifier = Modifier.width(300.dp)
    ) {
        var copiedScheduleEntry by remember { mutableStateOf(initialScheduleEntry.copy()) }

        var timePickerTime by remember { mutableStateOf(LocalTime.MIDNIGHT) }
        var timePickerCallback by remember { mutableStateOf({ _: LocalTime -> }) }

        var isTimePickerVisible by remember { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.padding(15.dp)
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

                    Divider(
                        color = Color.Black, modifier = Modifier
                            .height(1.dp)
                            .width(35.dp)
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
            }

            TextButton(shape = RoundedCornerShape(0),
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val subjectColor = availableSubjects.find {
                        it.subjectDTO.name == copiedScheduleEntry.content
                    }?.color ?: throw Exception("Subject not found")

                    onDone(
                        copiedScheduleEntry.copy(color = subjectColor)
                    )
                }) {
                Text(
                    "Confirmar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Black,
                )
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
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}