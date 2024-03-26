package br.studyleague.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.studyleague.OnboardingScreens
import br.studyleague.data.DataStoreKeys
import br.studyleague.data.DataStoreManager
import br.studyleague.data.repositories.StudentRepository
import br.studyleague.model.Schedule
import br.studyleague.model.StudentStats
import br.studyleague.model.Subject
import br.studyleague.ui.components.ScheduleEntryData
import br.studyleague.util.debug
import dtos.SubjectDTO
import dtos.signin.CredentialDTO
import dtos.signin.SignUpStudentData
import dtos.student.ScheduleDTO
import dtos.student.StudentDTO
import dtos.student.StudyCycleDTO
import dtos.student.StudyCycleEntryDTO
import dtos.student.StudyDayDTO
import dtos.student.WriteGoalDTO
import dtos.student.WriteStatisticDTO
import enums.DateRangeType
import enums.StatisticType
import enums.StudySchedulingMethods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime

class StudentViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState = _uiState.asStateFlow()

    private val studentRepository = StudentRepository()

    private var _timeDifferenceBetweenLocalAndServer: LocalDateTime? = null

    init {
        runBlocking {
            fetchServerCurrentTime()

            val studentId = dataStoreManager.getValue(DataStoreKeys.studentIdKey)
            if (studentId != null && studentId != 0L) {
                debug("Student ID found in data store: $studentId")

                fetchStudent(studentId)
            }
        }
    }

    fun addStudyInfoToStudent(name: String, goal: String, studyArea: String) {
        val studentDTO = StudentDTO()
        studentDTO.name = name
        studentDTO.goal = goal
        studentDTO.studyArea = studyArea

        _uiState.update {
            it.copy(student = studentDTO)
        }
    }

    suspend fun createStudent(email: String, password: String) {
        viewModelScope.launch {

        }
        val studentDTO = _uiState.value.student

        val credentialDTO = CredentialDTO()
        credentialDTO.email = email
        credentialDTO.password = password

        val signUpStudent = SignUpStudentData()
        signUpStudent.student = studentDTO
        signUpStudent.credential = credentialDTO

        val newStudentDTO = studentRepository.postStudent(signUpStudent)
        login(newStudentDTO)
    }

    suspend fun login(email: String, password: String) {
        val credentialDTO = CredentialDTO()
        credentialDTO.email = email
        credentialDTO.password = password

        val studentDTO = studentRepository.login(credentialDTO)
        login(studentDTO)

        updateStartupScreen(OnboardingScreens.STUDENT_SPACE.name)
    }

    suspend fun logout() {
        dataStoreManager.setValue(DataStoreKeys.studentIdKey, 0)

        _uiState.update {
            it.copy(student = StudentDTO())
        }

        updateStartupScreen(OnboardingScreens.ONBOARDING.name)
    }

    suspend fun finishOnboarding() {
        updateStartupScreen(OnboardingScreens.STUDENT_SPACE.name)
    }

    suspend fun addSubjects(subjects: List<Subject>) {
        if (subjects.isEmpty()) {
            return
        }

        val subjectsDto = subjects.map { it.subjectDTO }
        val studentId = uiState.value.student.id

        studentRepository.postSubjects(studentId, subjectsDto)
    }

    suspend fun deleteSubjects(subjects: List<Subject>) {
        if (subjects.isEmpty()) {
            return
        }

        val subjectsDto = subjects.map { it.subjectDTO }
        val studentId = uiState.value.student.id

        studentRepository.deleteSubjects(studentId, subjectsDto)
    }

    suspend fun fetchAllSubjects() {
        val studentId = uiState.value.student.id
        val currentDate = LocalDate.now()

        val selectedSubjectId = uiState.value.selectedSubject.subjectDTO.id

        val subjectsDto = studentRepository.fetchAllSubjects(studentId, currentDate)
        val subjects = subjectsDto.map { Subject(subjectDTO = it) }

        val newSelectedSubject =
            subjects.find { it.subjectDTO.id == selectedSubjectId } ?: Subject()

        _uiState.update {
            it.copy(subjects = subjects, selectedSubject = newSelectedSubject)
        }
    }

    suspend fun changeScheduleMethod(newMethod: StudySchedulingMethods) {
        val studentId = uiState.value.student.id

        studentRepository.changeScheduleMethod(studentId, newMethod)

        fetchStudent(studentId)
    }

    suspend fun updateScheduleEntries(scheduleEntries: List<ScheduleEntryData>) {
        val currentScheduleEntries = getScheduleEntries()
        if (currentScheduleEntries.containsAll(scheduleEntries) && currentScheduleEntries.size == scheduleEntries.size) {
            return
        }

        val subjects = uiState.value.subjects

        val studyDays = scheduleEntries.groupBy { it.dayOfWeek }
        val studyDaysDto = studyDays.map { (day, entries) ->
            StudyDayDTO(day, entries.map { it.toScheduleEntryDTO(subjects) })
        }

        val scheduleDto = ScheduleDTO(studyDaysDto)

        studentRepository.postSchedule(uiState.value.student.id, scheduleDto)
    }

    suspend fun fetchSchedule() {
        val scheduleDTO = studentRepository.fetchSchedule(uiState.value.student.id)
        _uiState.update {
            it.copy(schedule = Schedule(scheduleDTO = scheduleDTO))
        }
    }

    fun getScheduleEntries(defaultOnClick: (ScheduleEntryData) -> Unit = {}): List<ScheduleEntryData> {
        val listOfScheduleEntries = mutableListOf<ScheduleEntryData>()

        for (day in uiState.value.schedule.scheduleDTO.days) {
            for (scheduleEntry in day.entries) {
                val subject = findSubjectById(scheduleEntry.subjectId)

                listOfScheduleEntries.add(
                    ScheduleEntryData(
                        content = subject.subjectDTO.name,
                        startTime = scheduleEntry.startTime,
                        endTime = scheduleEntry.endTime,
                        dayOfWeek = day.dayOfWeek,
                        color = subject.color,
                        onClick = defaultOnClick
                    )
                )
            }
        }

        return listOfScheduleEntries
    }

    suspend fun fetchStudyCycle() {
        val studyCycleDTO = studentRepository.fetchStudyCycle(uiState.value.student.id)

        Log.d("StudyCycle", "Fetched entries: ${studyCycleDTO.entries.map { it.subject.name }}")

        studyCycleDTO.entries = pivotEntriesAtCurrentEntry(studyCycleDTO.entries, studyCycleDTO.currentEntry.id)

        _uiState.update {
            it.copy(studyCycleDTO = studyCycleDTO)
        }
    }

    suspend fun addSubjectToStudyCycle(subject: SubjectDTO, duration: Int) {
        val studentId = uiState.value.student.id

        val entries = uiState.value.studyCycleDTO.entries.toMutableList()

        val studyCycleEntry = StudyCycleEntryDTO()
        studyCycleEntry.subject = subject
        studyCycleEntry.durationInMinutes = duration

        entries.add(studyCycleEntry)

        studentRepository.postStudyCycle(studentId, entries)

        fetchStudyCycle()
    }

    suspend fun updateStudyCycleWeeklyGoal(weeklyGoal: Int) {
        val studentId = uiState.value.student.id

        studentRepository.updateStudyCycleWeeklyGoal(studentId, weeklyGoal)

        fetchStudyCycle()
    }

    suspend fun fetchStudentStats() {
        val studentStatsDTO = studentRepository.fetchStudentStats(
            uiState.value.student.id, LocalDate.now()
        )

        _uiState.update {
            it.copy(studentStats = StudentStats(studentStatisticsDTO = studentStatsDTO))
        }
    }

    suspend fun fetchScheduledSubjectsForDay() {
        val studentId = uiState.value.student.id

        val scheduledSubjects = studentRepository.fetchScheduledSubjects(studentId, LocalDate.now())
        val subjects = scheduledSubjects.map { Subject(subjectDTO = it) }

        _uiState.update {
            it.copy(subjects = subjects)
        }
    }

    suspend fun updateSelectedSubjectDailyStats(updatedStats: List<Float>) {
        // TODO: Check if it's equal before making the request.

        val studentId = uiState.value.student.id
        val subjectId = uiState.value.selectedSubject.subjectDTO.id

        // TODO: Remove this ugly thing. Make a map of StatisticType to index.
        val stats = updatedStats.mapIndexed { index, value ->
            WriteStatisticDTO(
                convertToStatisticType(index), value
            )
        }

        studentRepository.postSubjectStats(studentId, subjectId, stats)

        fetchStudentStats()
        fetchScheduledSubjectsForDay()
    }

    fun updateSelectedSubjectName(subjectName: String) {
        val currentName = uiState.value.selectedSubject.subjectDTO.name
        if (currentName == subjectName) {
            return
        }

        // TODO: Make API request.
    }

    suspend fun updateSelectedSubjectAlltimeGoals(goals: List<Float>) {
        // TODO: Check if it's equal before making the request.

        updateSubjectGoals(goals, DateRangeType.ALL_TIME)
    }

    suspend fun updateSelectedSubjectWeeklyGoals(goals: List<Float>) {
        // TODO: Check if it's equal before making the request.

        updateSubjectGoals(goals, DateRangeType.WEEKLY)
    }

    suspend fun nextSubjectInStudyCycle(questionsDone: Int, reviewsDone: Int) {
        val studentId = uiState.value.student.id

        studentRepository.nextSubjectInStudyCycle(studentId)

        val currentEntryDuration = uiState.value.studyCycleDTO.currentEntry.durationInMinutes

        val stats = listOf(
            WriteStatisticDTO(StatisticType.HOURS, currentEntryDuration / 60f),
            WriteStatisticDTO(StatisticType.QUESTIONS, questionsDone.toFloat()),
            WriteStatisticDTO(StatisticType.REVIEWS, reviewsDone.toFloat())
        )

        studentRepository.postSubjectStats(studentId, uiState.value.studyCycleDTO.currentEntry.subject.id, stats)

        fetchStudyCycle()
        fetchStudentStats()
    }

    suspend fun updateStudyCycleEntries(entriesIds: List<Long>) {
        Log.d("StudyCycle", "Updating study cycle entries: $entriesIds")

        val entries = uiState.value.studyCycleDTO.entries
        Log.d("StudyCycle", "Current entries: ${entries.map { it.id }}")

        val newEntries = mutableListOf<StudyCycleEntryDTO>()
        for (entryId in entriesIds) {
            val entry = entries.find { it.id == entryId } ?: continue
            newEntries.add(entry)
        }

        studentRepository.postStudyCycle(uiState.value.student.id, newEntries)

        fetchStudyCycle()
    }

    fun selectSubject(subject: Subject) {
        _uiState.update {
            it.copy(selectedSubject = subject)
        }
    }

    suspend fun fetchServerCurrentTime(): LocalDateTime {
        if (_timeDifferenceBetweenLocalAndServer == null) {
            val serverTime = studentRepository.fetchCurrentServerTime()
            val localTime = LocalDateTime.now()

            _timeDifferenceBetweenLocalAndServer = serverTime.minusNanos(localTime.nano.toLong())
        }

        return LocalDateTime.now().plusNanos(_timeDifferenceBetweenLocalAndServer!!.nano.toLong())
    }

    private suspend fun fetchStudent(studentId: Long) {
        val studentDTO = studentRepository.fetchStudent(studentId)

        _uiState.update {
            it.copy(student = studentDTO)
        }
    }

    private suspend fun login(studentDTO: StudentDTO) {
        dataStoreManager.setValue(DataStoreKeys.studentIdKey, studentDTO.id)

        _uiState.update {
            it.copy(student = studentDTO)
        }
    }

    private suspend fun updateSubjectGoals(goals: List<Float>, dateRangeType: DateRangeType) {
        val studentId = uiState.value.student.id
        val subjectId = uiState.value.selectedSubject.subjectDTO.id

        val allTimeGoals = mutableListOf<WriteGoalDTO>()
        for (index in goals.indices) {
            val statisticType = convertToStatisticType(index, dateRangeType)
            if (statisticType == StatisticType.HOURS) continue

            allTimeGoals.add(WriteGoalDTO(statisticType, goals[index]))
        }

        studentRepository.postSubjectGoals(
            studentId, subjectId, dateRangeType, allTimeGoals
        )
    }

    private fun findSubjectById(subjectId: Long): Subject {
        return uiState.value.subjects.find { it.subjectDTO.id == subjectId }
            ?: throw IllegalArgumentException("Subject not found.")
    }

    private fun convertToStatisticType(
        index: Int, dateRangeType: DateRangeType = DateRangeType.WEEKLY
    ): StatisticType {
        if (dateRangeType == DateRangeType.ALL_TIME) {
            return when (index) {
                0 -> StatisticType.QUESTIONS
                else -> throw IllegalArgumentException("Invalid index.")
            }
        }

        return when (index) {
            0 -> StatisticType.HOURS
            1 -> StatisticType.QUESTIONS
            2 -> StatisticType.REVIEWS
            else -> throw IllegalArgumentException("Invalid index.")
        }
    }

    private suspend fun updateStartupScreen(name: String) {
        dataStoreManager.setValue(DataStoreKeys.startupScreenKey, name)
    }

    private fun pivotEntriesAtCurrentEntry(entries: List<StudyCycleEntryDTO>, currentEntryId: Long): List<StudyCycleEntryDTO> {
        val currentEntryIndex = entries.indexOfFirst { it.id == currentEntryId }
        if (currentEntryIndex == -1) {
            return entries
        }

        val entriesBeforeCurrent = entries.subList(0, currentEntryIndex)
        val entriesAfterCurrent = entries.subList(currentEntryIndex, entries.size)

        Log.d("StudyCycle", "Current entry: ${entries.find { it.id == currentEntryId }?.subject?.name}")

        Log.d("StudyCycle", "Entries before current: ${entriesBeforeCurrent.map { it.subject.name}}")
        Log.d("StudyCycle", "Entries after current: ${entriesAfterCurrent.map { it.subject.name }}")

        return entriesAfterCurrent + entriesBeforeCurrent
    }

    companion object {
        fun factory(dataStoreManager: DataStoreManager): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    StudentViewModel(dataStoreManager)
                }
            }
        }
    }
}

data class StudentUiState(
    val subjects: List<Subject> = emptyList(),
    val schedule: Schedule = Schedule(),
    val studyCycleDTO: StudyCycleDTO = StudyCycleDTO(),
    val selectedSubject: Subject = Subject(),
    val student: StudentDTO = StudentDTO(),
    val studentStats: StudentStats = StudentStats(),
)

sealed class FetchState {
    data object Empty : FetchState()
    data object Loading : FetchState()
    data object Loaded: FetchState()
    data class Error(val message: String) : FetchState()
}