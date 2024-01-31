package com.example.studyleague.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyleague.model.Subject
import dtos.student.schedule.ScheduleEntryDTO
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import kotlin.random.Random


@Composable
fun Schedule(
    modifier: Modifier = Modifier,
    scheduleEntries: List<ScheduleEntryData>,
    numberOfVisibleDays: Int = 5,
    hourHeight: Dp = 40.dp,
    onGridClick: (DayOfWeek, Float) -> Unit = { _, _ -> }
) {
    val verticalScrollState = rememberScrollState(500)
    val horizontalScrollState = rememberScrollState()

    var timeColumnWidth by remember { mutableIntStateOf(0) }

    val horizontalPadding = 10.dp
    val availableWidth =
        LocalConfiguration.current.screenWidthDp.dp - with(LocalDensity.current) { timeColumnWidth.toDp() } - horizontalPadding * 2

    val weekDayWidth by remember { mutableStateOf(availableWidth / numberOfVisibleDays) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    .padding(end = 5.dp)
                    .verticalScroll(verticalScrollState)
                    .onGloballyPositioned { timeColumnWidth = it.size.width })

            ScheduleGrid(
                scheduleEntries = scheduleEntries,
                weekDayWidth = with(LocalDensity.current) { weekDayWidth.roundToPx() },
                hourHeight = with(LocalDensity.current) { hourHeight.roundToPx() },
                onGridClick = onGridClick,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            )
        }
    }
}


@Composable
fun ScheduleHeader(modifier: Modifier = Modifier, weekDayWidth: Dp) {
    Row(modifier = modifier) {
        DayOfWeek.entries.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE, LocalConfiguration.current.locales[0]
                ).uppercase(), style = androidx.compose.ui.text.TextStyle(
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
    onGridClick: (DayOfWeek, Float) -> Unit = { _, _ -> },
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
                val dayOfWeek = DayOfWeek.entries[(it.x / weekDayWidth).toInt()]

                onGridClick(dayOfWeek, it.y / hourHeight)
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
                scheduleEntryData.onClick(scheduleEntryData)
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
    val startTime: LocalTime = LocalTime.MIDNIGHT,
    val endTime: LocalTime = LocalTime.MIDNIGHT.plusHours(1),
    val dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val color: Color = Color.White,
    val content: String = "",
    val onClick: (ScheduleEntryData) -> Unit = {},
) {
    fun toScheduleEntryDTO(subjects: List<Subject>) = ScheduleEntryDTO(
        start = startTime,
        end = endTime,
        subjectId = subjects.find { it.subjectDTO.name == content }?.subjectDTO?.id
            ?: throw IllegalArgumentException("Subject not found.")
    )
}

fun Color.Companion.randomReadableColor(): Color {
    val random = Random.Default

    var red: Int = random.nextInt(256)
    var green: Int = random.nextInt(256)
    var blue: Int = random.nextInt(256)

    var luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255

    // Ensure sufficient luminance for readability
    while (luminance < 0.1) {
        red = random.nextInt(256)
        green = random.nextInt(256)
        blue = random.nextInt(256)
        luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    }

    return Color(red, green, blue, 255)
}