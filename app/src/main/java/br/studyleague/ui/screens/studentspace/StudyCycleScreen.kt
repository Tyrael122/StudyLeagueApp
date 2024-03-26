package br.studyleague.ui.screens.studentspace

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.studyleague.LocalStudentViewModel
import br.studyleague.model.StudyCycleEntry
import br.studyleague.ui.FetchState
import br.studyleague.ui.StudentUiState
import br.studyleague.ui.Util
import br.studyleague.ui.components.DefaultDialog
import br.studyleague.ui.components.NumberTextField
import br.studyleague.ui.components.StatisticsSquare
import br.studyleague.ui.components.StudentDropdownMenu
import br.studyleague.ui.screens.StudentSpaceDefaultColumn
import dtos.SubjectDTO
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.floor


@Composable
fun StudyCycleScreen() {
    val studentViewModel = LocalStudentViewModel.current
    val uiState by studentViewModel.uiState.collectAsState()

    var fetchState by remember { mutableStateOf<FetchState>(FetchState.Empty) }

    LaunchedEffect(Unit) {
        studentViewModel.fetchStudyCycle()
        studentViewModel.fetchAllSubjects()
        studentViewModel.fetchStudentStats()

        fetchState = FetchState.Loaded
    }

    val coroutineScope = rememberCoroutineScope()

    when (fetchState) {
        is FetchState.Loaded -> StudyCycleScreenContent(uiState,
            addSubjectToStudyCycle = { subject, duration ->
                coroutineScope.launch {
                    studentViewModel.addSubjectToStudyCycle(subject, duration)
                }
            },
            onWeeklyGoalChange = { weeklyGoal ->
                coroutineScope.launch {
                    studentViewModel.updateStudyCycleWeeklyGoal(weeklyGoal)
                }
            },
            onReorderStudyCycleEntries = { entriesIds ->
                coroutineScope.launch {
                    studentViewModel.updateStudyCycleEntries(entriesIds)
                }
            },
            onDeleteStudyCycleEntry = { entryIds ->
                coroutineScope.launch {
                    studentViewModel.updateStudyCycleEntries(entryIds)
                }
            },
            onNextStudyCycleEntry = { questionsDone, reviewsDone ->
                coroutineScope.launch {
                    studentViewModel.nextSubjectInStudyCycle(questionsDone, reviewsDone)
                }
            })

        else -> {}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StudyCycleScreenContent(
    studentUiState: StudentUiState,
    addSubjectToStudyCycle: (SubjectDTO, Int) -> Unit,
    onWeeklyGoalChange: (weeklyGoal: Int) -> Unit,
    onReorderStudyCycleEntries: (entriesIds: List<Long>) -> Unit,
    onDeleteStudyCycleEntry: (entryIds: List<Long>) -> Unit,
    onNextStudyCycleEntry: (questions: Int, reviews: Int) -> Unit
) {
    StudentSpaceDefaultColumn(modifier = Modifier.fillMaxSize()) {
        var showChangeWeeklyGoalDialog by remember { mutableStateOf(false) }

        Row {
            val completedCycles =
                (studentUiState.studentStats.studentStatisticsDTO.dailyStatistic.hours * 60) / studentUiState.studyCycleDTO.weeklyMinutesToStudy

            BiggerStatisticsSquare(
                title = "Ciclos completos",
                data = floor(completedCycles).toInt().toString(),
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            BiggerStatisticsSquare(title = "Progresso semanal", modifier = Modifier.clickable {
                showChangeWeeklyGoalDialog = true
            }, data = buildAnnotatedString {
                append("${studentUiState.studentStats.studentStatisticsDTO.weeklyStatistic.hours * 60}")
                append("/")
                append("${studentUiState.studyCycleDTO.weeklyMinutesToStudy}")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp)) {
                    append(" min")
                }
            })

            Spacer(modifier = Modifier.width(20.dp))

            BiggerStatisticsSquare(
                title = "Progresso diário",
                modifier = Modifier.clickable {
                    showChangeWeeklyGoalDialog = true
                },
                data = "${studentUiState.studentStats.studentStatisticsDTO.dailyStatistic.hours * 60}/${studentUiState.studyCycleDTO.weeklyMinutesToStudy / 7}"
            )
        }

        val cards = remember(studentUiState.studyCycleDTO.entries) {
            val state = mutableStateListOf<StudyCycleEntry>()

            val actualCards = studentUiState.studyCycleDTO.entries.map {
                StudyCycleEntry(
                    it.id,
                    it.subject,
                    it.durationInMinutes,
                )
            }
            state.addAll(actualCards)

            state
        }

        var isInEditionMode by remember { mutableStateOf(false) }
        var showAddSubjectDialog by remember { mutableStateOf(false) }
        var showNextSubjectDialog by remember { mutableStateOf(false) }

        val lazyListState = rememberLazyListState()
        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            cards.apply {
                add(to.index, removeAt(from.index))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1F)
        ) {
            items(cards.size, key = { cards[it].id }) {
                ReorderableItem(reorderableLazyListState, key = cards[it].id) { isDragging ->
                    val isBeingStudied =
                        studentUiState.studyCycleDTO.currentEntry.id == cards[it].id

                    StudyCycleCard(
                        name = cards[it].subject.name,
                        duration = cards[it].duration,
                        minutesStudied = cards[it].subject.weeklyStatistic.hours * 60,
                        questionsAnswered = cards[it].subject.weeklyStatistic.questions,
                        reviews = cards[it].subject.weeklyStatistic.reviews,
                        isSelected = cards[it].isSelected,
                        isBeingStudied = isBeingStudied && !isInEditionMode,
                        isExpanded = cards[it].isExpanded,
                        onClick = {
                            if (isInEditionMode) {
                                cards[it] = cards[it].copy(isSelected = !cards[it].isSelected)
                            } else {
                                cards[it] = cards[it].copy(isExpanded = !cards[it].isExpanded)
                            }
                        },
                        onLongClick = {
                            if (isInEditionMode) return@StudyCycleCard

                            isInEditionMode = true

                            cards.forEachIndexed { index, card ->
                                cards[index] = card.copy(isExpanded = false)
                            }

                            cards[it] = cards[it].copy(isSelected = true)
                        },
                        onNextStudyCycleEntry = {
                            if (!isBeingStudied) return@StudyCycleCard

                            showNextSubjectDialog = true
                        },
                        modifier = Modifier.draggableHandle(isInEditionMode, onDragStopped = {
                            onReorderStudyCycleEntries(cards.map { entry -> entry.id })
                        })
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isInEditionMode) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    cards.forEachIndexed { index, card ->
                        cards[index] = card.copy(isSelected = false)
                    }
                    isInEditionMode = false
                }) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }

                IconButton(onClick = {
                    val unselectedEntries = cards.filter { !it.isSelected }
                    val entryIds = unselectedEntries.map { it.id }

                    onDeleteStudyCycleEntry(entryIds)

                    isInEditionMode = false
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                }
            }
        } else {
            TextButton(onClick = { showAddSubjectDialog = true }) {
                Text(
                    "Adicionar matéria", style = TextStyle(
                        fontWeight = FontWeight.Light, fontSize = 20.sp, color = Color.Black
                    )
                )
            }
        }

        if (showAddSubjectDialog) {
            AddSubjectDialog(subjects = studentUiState.subjects.map { it.subjectDTO },
                hideDialog = { showAddSubjectDialog = false }) { subject, duration ->
                addSubjectToStudyCycle(subject, duration)
            }
        }

        if (showChangeWeeklyGoalDialog) {
            WeeklyGoalDialog(
                currentWeeklyGoal = studentUiState.studyCycleDTO.weeklyMinutesToStudy.toString(),
                hideDialog = { showChangeWeeklyGoalDialog = false },
                onWeeklyGoalChange = onWeeklyGoalChange
            )
        }

        if (showNextSubjectDialog) {
            NextSubjectDialog(
                minutesStudied = studentUiState.studyCycleDTO.currentEntry.durationInMinutes,
                hideDialog = { showNextSubjectDialog = false },
                onNextStudyCycleEntry = onNextStudyCycleEntry
            )
        }
    }
}

@Composable
fun NextSubjectDialog(
    minutesStudied: Int,
    hideDialog: () -> Unit,
    onNextStudyCycleEntry: (questions: Int, reviews: Int) -> Unit
) {
    DefaultDialog(onDismissRequest = hideDialog) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            var reviews by remember { mutableIntStateOf(0) }
            var questions by remember { mutableIntStateOf(0) }

            NextSubjectDataRow(
                label = "Minutos estudados:", value = minutesStudied.toString(), readOnly = true
            )

            NextSubjectDataRow(label = "Questões feitas:", value = questions.toString()) {
                questions = it.toInt()
            }

            NextSubjectDataRow(label = "Revisões feitas:", value = reviews.toString()) {
                reviews = it.toInt()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = hideDialog,
                ) {
                    Text(
                        "Cancelar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black
                    )
                }

                TextButton(onClick = {
                    hideDialog()

                    onNextStudyCycleEntry(questions, reviews)
                }) {
                    Text(
                        "Salvar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@Composable
fun NextSubjectDataRow(
    label: String, value: String, readOnly: Boolean = false, onValueChange: (String) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            label, fontSize = 18.sp, fontWeight = FontWeight.Light
        )

        NumberTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier
                .width(64.dp)
                .background(Color(0xFFF1F3F4), RoundedCornerShape(5.dp))
                .padding(7.dp),
        )
    }
}

@Composable
private fun WeeklyGoalDialog(
    currentWeeklyGoal: String, hideDialog: () -> Unit, onWeeklyGoalChange: (weeklyGoal: Int) -> Unit
) {
    DefaultDialog(onDismissRequest = hideDialog) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            var weeklyGoal by remember { mutableStateOf(currentWeeklyGoal) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Meta semanal (minutos):", fontSize = 18.sp, fontWeight = FontWeight.Light
                )

                NumberTextField(
                    value = weeklyGoal,
                    onValueChange = { weeklyGoal = it },
                    modifier = Modifier
                        .width(64.dp)
                        .background(Color(0xFFF1F3F4), RoundedCornerShape(5.dp))
                        .padding(7.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = hideDialog,
                ) {
                    Text(
                        "Cancelar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black
                    )
                }

                val context = LocalContext.current

                TextButton(onClick = {
                    if (!validateWeeklyGoalsField(weeklyGoal, context)) return@TextButton

                    onWeeklyGoalChange(weeklyGoal.toInt())

                    hideDialog()
                }) {
                    Text(
                        "Salvar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSubjectDialog(
    subjects: List<SubjectDTO>,
    hideDialog: () -> Unit,
    onAddSubject: (subject: SubjectDTO, duration: Int) -> Unit
) {
    DefaultDialog(onDismissRequest = {
        hideDialog()
    }) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            var selectedSubjectName by remember { mutableStateOf("") }
            var duration by remember { mutableStateOf("") }

            StudentDropdownMenu(
                options = subjects.map { it.name },
                selectedOptionText = selectedSubjectName,
                onSelectionChanged = { selectedSubjectName = it },
                placeholder = { Text("Selecione uma matéria") },
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Duração (minutos):", fontSize = 18.sp, fontWeight = FontWeight.Light)

                NumberTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    modifier = Modifier
                        .width(64.dp)
                        .background(Color(0xFFF1F3F4), RoundedCornerShape(5.dp))
                        .padding(7.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        hideDialog()
                    },
                ) {
                    Text(
                        "Cancelar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black
                    )
                }

                val context = LocalContext.current

                TextButton(onClick = {
                    if (!validateAddSubjectFields(
                            duration, selectedSubjectName, context
                        )
                    ) return@TextButton

                    hideDialog()

                    val selectedSubject = subjects.find { it.name == selectedSubjectName }
                    if (selectedSubject != null) {
                        onAddSubject(selectedSubject, duration.toInt())
                    }
                }) {
                    Text(
                        "Adicionar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudyCycleCard(
    modifier: Modifier = Modifier,
    name: String,
    duration: Int,
    minutesStudied: Float,
    questionsAnswered: Int,
    reviews: Int,
    isBeingStudied: Boolean = false,
    isSelected: Boolean = false,
    isExpanded: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onNextStudyCycleEntry: () -> Unit,
) {
    val textStyleNotHighlighted = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp)
    val textStyleHighlighted = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)

    val textStyle = if (isBeingStudied) textStyleHighlighted else textStyleNotHighlighted
    val backgroundColor by animateColorAsState(
        targetValue = if (isBeingStudied) Color(0xFFe9e8e8)
        else Color.White
    )
    val shadow = if (isBeingStudied) 5.dp else 2.dp

    val shape = if (isExpanded) RoundedCornerShape(
        topStart = 10.dp, topEnd = 10.dp
    ) else RoundedCornerShape(10.dp)

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(onLongClick = onLongClick, onClick = onClick)
                .shadow(shadow, shape = shape)
                .background(backgroundColor)
                .then(
                    if (isSelected) Modifier.border(
                        1.dp, Color(0xFF6373C6), shape = shape
                    ) else Modifier
                )
                .padding(vertical = 20.dp, horizontal = 10.dp)
        ) {
            Text(name, style = textStyle, modifier = Modifier.weight(1F))

            Spacer(modifier = Modifier.width(10.dp))

            Text("$duration min", style = textStyle)

            Spacer(modifier = Modifier.width(10.dp))

            AnimatedVisibility(visible = isBeingStudied) {
                FloatingActionButton(
                    onClick = onNextStudyCycleEntry, containerColor = Color.White
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        5.dp, RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)
                    )
                    .background(Color.White)
                    .padding(horizontal = 5.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val statsTextStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    val verticalPaddingModifier = Modifier.padding(vertical = 5.dp)

                    HighlightedSubjectColumn {
                        Text(
                            text = minutesStudied.toString(),
                            style = statsTextStyle,
                            modifier = verticalPaddingModifier
                        )

                        Text(text = "Minutos")
                    }

                    HighlightedSubjectColumn {
                        Text(
                            text = questionsAnswered.toString(),
                            style = statsTextStyle,
                            modifier = verticalPaddingModifier
                        )

                        Text(text = "Questões")
                    }

                    HighlightedSubjectColumn {
                        Text(
                            text = reviews.toString(),
                            style = statsTextStyle,
                            modifier = verticalPaddingModifier
                        )

                        Text(text = "Revisões")
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedSubjectColumn(body: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        body()
    }
}

@Composable
fun HighlightedSubjectTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    NumberTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = Modifier
            .width(50.dp)
            .background(Color(0xFFF1F3F4), shape = RoundedCornerShape(5.dp))
            .then(modifier)
    )
}

@Composable
fun BiggerStatisticsSquare(modifier: Modifier = Modifier, title: String, data: String) {
    BiggerStatisticsSquare(modifier = modifier, title = title, data = AnnotatedString(data))
}

@Composable
fun BiggerStatisticsSquare(
    modifier: Modifier = Modifier, title: String, data: AnnotatedString
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 7.dp)
            .sizeIn(minWidth = 96.dp, minHeight = 75.dp)
    ) {
        Text(title, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp))

        Spacer(modifier = Modifier.height(10.dp))

        Text(data, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp))
    }
}

private fun validateAddSubjectFields(
    duration: String, selectedSubjectName: String, context: Context
): Boolean {
    if (duration.isEmpty() || selectedSubjectName.isEmpty()) {
        Toast.makeText(
            context, "Preencha todos os campos!", Toast.LENGTH_SHORT
        ).show()

        return false
    }

    if (duration.toIntOrNull() == null) {
        Toast.makeText(
            context, "Duração inválida", Toast.LENGTH_SHORT
        ).show()

        return false
    }

    return true
}

fun validateWeeklyGoalsField(weeklyGoal: String, context: Context): Boolean {
    if (weeklyGoal.isEmpty()) {
        Toast.makeText(
            context, "Preencha todos os campos!", Toast.LENGTH_SHORT
        ).show()

        return false
    }

    if (weeklyGoal.toIntOrNull() == null) {
        Toast.makeText(
            context, "Meta semanal inválida", Toast.LENGTH_SHORT
        ).show()

        return false
    }

    return true
}

@Preview
@Composable
fun StudyCycleScreenPreview() {
    StudyCycleScreen()
}