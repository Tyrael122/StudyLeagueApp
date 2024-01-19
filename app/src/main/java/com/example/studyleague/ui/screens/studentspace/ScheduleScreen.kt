package com.example.studyleague.ui.screens.studentspace

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.studyleague.ui.components.NumberTextField
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


@Composable
fun ScheduleEntryInfoDialog(
//    startTime: LocalTime,
//    endTime: LocalTime,
    availableSubjects: List<String>, onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NumberTextField(value = "2:30PM", onValueChange = {})

                    Divider(
                        color = Color.Black, modifier = Modifier
                            .height(1.dp)
                            .width(35.dp)
                    )

                    NumberTextField(value = "4:30PM", onValueChange = {})
                }

                StudentDropdownMenu(options = availableSubjects,
                    selectedOptionText = "",
                    onSelectionChanged = {},
                    placeholder = { Text("Escolha uma matéria") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDropdownMenu(
    modifier: Modifier = Modifier,

    // Can change later to a List<Pair<Long, String>>.
    // You could provide another function, onValueChanged, that would return the id of the selected item, when there was a match in the list.
    // Provide a parameter called selectedValue too.
    // Then, you wouldn't need to override the selectedOptionText. If you still did, you would need to override onSelectionChanged too.

    options: List<String>,
    selectedOptionText: String,
    onSelectionChanged: (String) -> Unit,
    isSearchable: Boolean = true,
    textFieldColors: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        unfocusedContainerColor = Color(0xFFEEEEEE),
        focusedContainerColor = Color(0xFFEEEEEE),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
    ),
    placeholder: @Composable (() -> Unit)? = null,
    textFieldShape: Shape = RoundedCornerShape(10.dp),
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val filteredOptions = if (isSearchable) {
            options.filter { it.contains(selectedOptionText, ignoreCase = true) }
        } else {
            options
        }

        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
                .onFocusChanged {
                    expanded = it.isFocused

                    if (!filteredOptions.contains(selectedOptionText)) {
                        onSelectionChanged("")
                    }
                },
            value = selectedOptionText,
            readOnly = !isSearchable,
            onValueChange = onSelectionChanged,
            placeholder = placeholder,
            shape = textFieldShape,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = textFieldColors,
        )

        if (filteredOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .exposedDropdownSize(true)
                    .background(Color.White),
                properties = createPopUpProperties(isSearchable),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                filteredOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            expanded = false
                            focusManager.clearFocus()
                            onSelectionChanged(selectionOption)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

fun createPopUpProperties(isSearchable: Boolean): PopupProperties {
    return if (isSearchable) {
        PopupProperties(
            focusable = false,
            dismissOnClickOutside = false // This is false because the loss of focus is handled by the TextField. (See onFocusChanged)
        )
    } else {
        PopupProperties(
            focusable = true, dismissOnClickOutside = true, dismissOnBackPress = true
        )
    }
}

@Composable
fun ScheduleScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    setFullscreenMode(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var timeColumnWidth by remember { mutableIntStateOf(0) }

    val hourHeight = 40.dp

    val numberOfVisibleDays = 5
    val horizontalPadding = 10.dp

    val availableWidth =
        LocalConfiguration.current.screenWidthDp.dp - with(LocalDensity.current) { timeColumnWidth.toDp() } - horizontalPadding * 2

    val weekDayWidth by remember { mutableStateOf(availableWidth / numberOfVisibleDays) }

    var isDialogVisible by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {
        IconButton(
            onClick = onDone,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFFC1C1C1)
            ),
            modifier = Modifier
                .padding(bottom = 30.dp, end = 30.dp)
                .size(50.dp)
                .shadow(1.dp, RoundedCornerShape(50))
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Adicionar")
        }
    }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .padding(horizontal = horizontalPadding, vertical = 5.dp)
                .padding(paddingValues)
        ) {
            ScheduleHeader(
                weekDayWidth = weekDayWidth,
                modifier = Modifier
                    .padding(start = with(LocalDensity.current) {
                        timeColumnWidth.toDp()
                    })
                    .horizontalScroll(horizontalScrollState)
            )

            Row {
                ScheduleTimeColumn(hourHeight = hourHeight,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                        .onGloballyPositioned { timeColumnWidth = it.size.width })

                ScheduleGrid(
                    scheduleEntries = sampleScheduleEntries,
                    weekDayWidth = with(LocalDensity.current) { weekDayWidth.roundToPx() },
                    hourHeight = with(LocalDensity.current) { hourHeight.roundToPx() },
                    onGridClick = { dayOfWeek, hour ->
                        Log.d("ScheduleScreen", "onGridClick: $dayOfWeek, $hour")
                        isDialogVisible = true
                    },
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                )
            }
        }


        if (isDialogVisible) {
            val sampleSubjects = listOf("Matemática", "Português", "Física", "Química", "Biologia")

            ScheduleEntryInfoDialog(availableSubjects = sampleSubjects, onDismissRequest = {
                isDialogVisible = false
            })
        }
    }
}

@Composable
fun ScheduleHeader(modifier: Modifier = Modifier, weekDayWidth: Dp) {
    Row(modifier = modifier) {
        DayOfWeek.entries.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT_STANDALONE,
                    LocalConfiguration.current.locales[0]
                ).uppercase(), style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center
                ), modifier = Modifier.width(weekDayWidth)
            )
        }
    }
}

@Composable
fun ScheduleTimeColumn(modifier: Modifier, hourHeight: Dp) {
    Column(modifier = modifier) {
        for (hour in 0..23) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(hourHeight)) {
                Text(text = "$hour:00")
            }
        }
    }
}

@Composable
fun ScheduleGrid(
    modifier: Modifier = Modifier,
    scheduleEntries: List<ScheduleEntryData>,
    hourHeight: Int,
    weekDayWidth: Int,
    onGridClick: (DayOfWeek, Int) -> Unit = { _, _ -> },
    dividerColor: Color = Color.LightGray
) {
    Layout(modifier = modifier
        .drawBehind {
            repeat(23) {
                drawLine(
                    dividerColor,
                    start = Offset(0F, (it + 1F) * hourHeight),
                    end = Offset(size.width, (it + 1F) * hourHeight),
                    strokeWidth = 1.dp.toPx()
                )
            }
            repeat(6) {
                drawLine(
                    dividerColor,
                    start = Offset((it + 1F) * weekDayWidth, 0F),
                    end = Offset((it + 1F) * weekDayWidth, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                onGridClick(
                    DayOfWeek.entries[(it.x / weekDayWidth).toInt()], (it.y / hourHeight).toInt()
                )
            })
        }, content = {
        scheduleEntries.sortedBy { it.startTime }.forEach { scheduleEntryData ->
            ScheduleEntry(
                scheduleEntryData = scheduleEntryData,
                modifier = Modifier.scheduleEntryData(scheduleEntryData)
            )
        }
    }) { measurables, constraints ->
        val placeablesWithScheduleData = measurables.map { measurable ->
            val scheduleEntryData = measurable.parentData as ScheduleEntryData

            val scheduleEntryHeight =
                calculateEventHeight(scheduleEntryData, hourHeight).roundToInt()

            val placeable = measurable.measure(
                Constraints(
                    minWidth = weekDayWidth,
                    maxWidth = weekDayWidth,
                    minHeight = scheduleEntryHeight,
                    maxHeight = scheduleEntryHeight
                )
            )

            Pair(placeable, scheduleEntryData)
        }

        val totalHeight = hourHeight * 24
        val totalWidth = weekDayWidth * DayOfWeek.entries.size
        layout(totalWidth, totalHeight) {

            placeablesWithScheduleData.forEach { (placeable, scheduleData) ->
                val yOffset = calculateYOffset(scheduleData, hourHeight)
                val xOffset = calculateXOffset(scheduleData, weekDayWidth)

                placeable.placeRelative(xOffset, yOffset.roundToInt())
            }
        }
    }
}

fun calculateXOffset(scheduleData: ScheduleEntryData, weekDayWidth: Int): Int {
    val dayOfWeek = scheduleData.dayOfWeek.value

    return (dayOfWeek - 1) * weekDayWidth
}

private fun calculateYOffset(
    scheduleData: ScheduleEntryData, hourHeight: Int
): Float {
    val hoursFromMidnight = ChronoUnit.MINUTES.between(
        LocalTime.MIDNIGHT, scheduleData.startTime
    ) / 60F

    return hoursFromMidnight * hourHeight
}

private fun calculateEventHeight(
    scheduleEntryData: ScheduleEntryData, hourHeight: Int
): Float {
    val eventDurationInMinutes =
        ChronoUnit.MINUTES.between(scheduleEntryData.startTime, scheduleEntryData.endTime)
    return (eventDurationInMinutes / 60F) * hourHeight
}

private class ScheduleEntryDataModifier(
    val scheduleEntry: ScheduleEntryData,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = scheduleEntry
}

private fun Modifier.scheduleEntryData(scheduleEntry: ScheduleEntryData) =
    this.then(ScheduleEntryDataModifier(scheduleEntry))

@Composable
fun ScheduleEntry(
    modifier: Modifier = Modifier,
    scheduleEntryData: ScheduleEntryData,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(scheduleEntryData.color)
            .clickable {
                scheduleEntryData.onClick()
            }) {
        Text(
            text = scheduleEntryData.content,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class ScheduleEntryData(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: DayOfWeek,
    val color: Color,
    val content: String,
    val onClick: () -> Unit,
)

@Composable
private fun setFullscreenMode(screenOrientation: Int) {
    val context = LocalContext.current

    DisposableEffect(screenOrientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation

        activity.requestedOrientation = screenOrientation

        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).let {
            it.show(WindowInsetsCompat.Type.systemBars())
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

val yellow = Color(0xFFF1FF9C)
val blue = Color(0xFF9FACF1)
val red = Color(0xFFF1A49F)
val purple = Color(0xFFDB9FF1)

private fun generateSampleScheduleEntries(): List<ScheduleEntryData> {
    val medicinaVeterinaria = listOf(
        listOf(9, 11, DayOfWeek.MONDAY),
        listOf(10, 11, DayOfWeek.TUESDAY),
        listOf(9, 10, DayOfWeek.WEDNESDAY),
        listOf(12, 13, DayOfWeek.WEDNESDAY),
        listOf(14, 15, DayOfWeek.WEDNESDAY),
    )

    val direitoConstitucional = listOf(
        listOf(9, 10, DayOfWeek.TUESDAY),
        listOf(13, 14, DayOfWeek.WEDNESDAY),
        listOf(11, 12, DayOfWeek.THURSDAY),
        listOf(10, 11, DayOfWeek.FRIDAY),
    )

    val arquiteturaDeComputadores = listOf(
        listOf(11, 13, DayOfWeek.TUESDAY),
        listOf(11, 12, DayOfWeek.WEDNESDAY),
        listOf(10, 11, DayOfWeek.THURSDAY),
    )

    val fisicaQuantica = listOf(
        listOf(13, 14, DayOfWeek.TUESDAY),
        listOf(10, 11, DayOfWeek.WEDNESDAY),
        listOf(12, 14, DayOfWeek.THURSDAY),
    )

    val sampleScheduleEntries = mutableListOf<ScheduleEntryData>()

    for (days in medicinaVeterinaria) {
        sampleScheduleEntries.add(
            ScheduleEntryData(startTime = LocalTime.of(days[0] as Int, 0),
                endTime = LocalTime.of(days[1] as Int, 0),
                dayOfWeek = days[2] as DayOfWeek,
                color = yellow,
                content = "Medicina Veterinária",
                onClick = {})
        )
    }

    for (days in direitoConstitucional) {
        sampleScheduleEntries.add(
            ScheduleEntryData(startTime = LocalTime.of(days[0] as Int, 0),
                endTime = LocalTime.of(days[1] as Int, 0),
                dayOfWeek = days[2] as DayOfWeek,
                color = blue,
                content = "Direito Constitucional",
                onClick = {})
        )
    }

    for (days in arquiteturaDeComputadores) {
        sampleScheduleEntries.add(
            ScheduleEntryData(startTime = LocalTime.of(days[0] as Int, 0),
                endTime = LocalTime.of(days[1] as Int, 0),
                dayOfWeek = days[2] as DayOfWeek,
                color = red,
                content = "Arquitetura de Computadores",
                onClick = {})
        )
    }

    for (days in fisicaQuantica) {
        sampleScheduleEntries.add(
            ScheduleEntryData(startTime = LocalTime.of(days[0] as Int, 0),
                endTime = LocalTime.of(days[1] as Int, 0),
                dayOfWeek = days[2] as DayOfWeek,
                color = purple,
                content = "Física Quântica",
                onClick = {})
        )
    }

    return sampleScheduleEntries
}

val sampleScheduleEntries = generateSampleScheduleEntries()