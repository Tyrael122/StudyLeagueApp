package com.example.studyleague.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studyleague.data.DataStoreKeys
import com.example.studyleague.data.DataStoreManager
import com.example.studyleague.data.datasources.RemoteDataSource
import com.example.studyleague.data.repositories.StudentRepository
import com.example.studyleague.model.Schedule
import com.example.studyleague.model.Student
import com.example.studyleague.model.StudentStats
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.ScheduleEntryData
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import dtos.student.schedule.StudyDayDTO
import enums.DateRangeType
import enums.StatisticType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class StudentViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState = _uiState.asStateFlow()

    private val studentRepository = StudentRepository(RemoteDataSource())

    init {
        runBlocking {
//            val studentId = dataStoreManager.getValueFromDataStore(DataStoreKeys.studentIdKey)
//            if (studentId == null) {
//                createStudent()
//            } else {
//                fetchStudent(studentId)
//            }

            fetchStudent(1)
        }
    }

    suspend fun addSubjects(subjects: List<Subject>) {
        if (subjects.isEmpty()) {
            return
        }

        val subjectsDto = subjects.map { it.subjectDTO }
        val studentId = uiState.value.student.studentDTO.id

        studentRepository.addSubjects(studentId, subjectsDto)
    }

    suspend fun fetchAllSubjects() {
        val studentId = uiState.value.student.studentDTO.id
        val currentDate = uiState.value.currentDate

        val selectedSubjectId = uiState.value.selectedSubject.subjectDTO.id

        val subjectsDto = studentRepository.fetchAllSubjects(studentId, currentDate)
        val subjects = subjectsDto.map { Subject(subjectDTO = it) }

        val newSelectedSubject =
            subjects.find { it.subjectDTO.id == selectedSubjectId } ?: Subject()

        _uiState.update {
            it.copy(subjects = FetchState.Loaded(subjects), selectedSubject = newSelectedSubject)
        }
    }

    suspend fun updateScheduleEntries(scheduleEntries: List<ScheduleEntryData>) {
        fetchSchedule()
        val currentScheduleEntries = getScheduleEntries()

        if (currentScheduleEntries.containsAll(scheduleEntries) && currentScheduleEntries.size == scheduleEntries.size) {
            return
        }

        val subjects = uiState.value.subjects

        val studyDays = scheduleEntries.groupBy { it.dayOfWeek }
        val studyDaysDto = studyDays.map { (day, entries) ->
            StudyDayDTO(day, entries.map { it.toScheduleEntryDTO(subjects.getLoadedValue()) })
        }

        val scheduleDto = ScheduleDTO(studyDaysDto)

        studentRepository.updateSchedule(uiState.value.student.studentDTO.id, scheduleDto)
    }

    suspend fun fetchSchedule() {
        fetchAllSubjects()

        _uiState.update {
            it.copy(schedule = FetchState.Loading)
        }

        val scheduleDTO = studentRepository.fetchSchedule(uiState.value.student.studentDTO.id)
        _uiState.update {
            it.copy(schedule = FetchState.Loaded(Schedule(scheduleDTO = scheduleDTO)))
        }
    }

    fun getScheduleEntries(): List<ScheduleEntryData> {
        val listOfScheduleEntries = mutableListOf<ScheduleEntryData>()

        for (day in uiState.value.schedule.getLoadedValue().scheduleDTO.days) {
            for (scheduleEntry in day.schedule) {
                listOfScheduleEntries.add(
                    ScheduleEntryData(
                        content = findSubjectById(scheduleEntry.subjectId).subjectDTO.name,
                        startTime = scheduleEntry.start,
                        endTime = scheduleEntry.end,
                        dayOfWeek = day.dayOfWeek
                    )
                )
            }
        }

        return listOfScheduleEntries
    }

    suspend fun fetchStudentStats() {
        val studentStatsDTO = studentRepository.fetchStudentStats(
            uiState.value.student.studentDTO.id, uiState.value.currentDate
        )
        _uiState.update {
            it.copy(studentStats = FetchState.Loaded(StudentStats(studentStatisticsDTO = studentStatsDTO)))
        }
    }

    suspend fun fetchScheduledSubjectsForDay() {
        val studentId = uiState.value.student.studentDTO.id

        val scheduledSubjects =
            studentRepository.fetchScheduledSubjects(studentId, uiState.value.currentDate)
        val subjects = scheduledSubjects.map { Subject(subjectDTO = it) }

        _uiState.update {
            it.copy(subjects = FetchState.Loaded(subjects))
        }
    }

    suspend fun updateSelectedSubjectDailyStats(updatedStats: List<Float>) {
        // TODO: Check if it's equal before making the request.

        val studentId = uiState.value.student.studentDTO.id
        val subjectId = uiState.value.selectedSubject.subjectDTO.id

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

    private suspend fun updateSubjectGoals(goals: List<Float>, dateRangeType: DateRangeType) {
        val studentId = uiState.value.student.studentDTO.id
        val subjectId = uiState.value.selectedSubject.subjectDTO.id

        val allTimeGoals = mutableListOf<WriteGoalDTO>()
        for (index in goals.indices) {
            val statisticType = convertToStatisticType(index)
            if (statisticType == StatisticType.HOURS) continue

            allTimeGoals.add(WriteGoalDTO(statisticType, goals[index]))
        }

        studentRepository.postSubjectGoals(
            studentId, subjectId, dateRangeType, allTimeGoals
        )
    }

    fun selectSubject(subject: Subject) {
        _uiState.update {
            it.copy(selectedSubject = subject)
        }
    }

    private fun findSubjectById(subjectId: Long): Subject {
        return uiState.value.subjects.getLoadedValue().find { it.subjectDTO.id == subjectId }
            ?: throw IllegalArgumentException("Subject not found.")
    }

    private suspend fun createStudent() {
        val studentDTO = studentRepository.createStudent(StudentDTO())

        _uiState.update {
            it.copy(student = Student(studentDTO = studentDTO))
        }

        dataStoreManager.setDataStoreValue(DataStoreKeys.studentIdKey, studentDTO.id)
    }

    private suspend fun fetchStudent(studentId: Long) {
        val studentDTO = studentRepository.fetchStudent(studentId)
        _uiState.update {
            it.copy(student = Student(studentDTO = studentDTO))
        }
    }

    private fun convertToStatisticType(index: Int): StatisticType {
        return when (index) {
            0 -> StatisticType.HOURS
            1 -> StatisticType.QUESTIONS
            2 -> StatisticType.REVIEWS
            else -> throw IllegalArgumentException("Invalid index.")
        }
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
    val subjects: FetchState<List<Subject>> = FetchState.Empty,
    val schedule: FetchState<Schedule> = FetchState.Empty,
    val selectedSubject: Subject = Subject(),
    val student: Student = Student(),
    val studentStats: FetchState<StudentStats> = FetchState.Empty,
    val currentDate: LocalDate = LocalDate.now()
)

// Out makes this accept subtypes of T,
// so if I say I want a FetchState<String>, I can pass a FetchResult.Empty to it.
sealed class FetchState<out T> {
    data object Empty : FetchState<Nothing>()
    data object Loading : FetchState<Nothing>()
    data class Loaded<T>(val value: T) : FetchState<T>()
    data object Error : FetchState<Nothing>()

    fun getLoadedValue(): T {
        return when (this) {
            is Loaded -> this.value
            else -> throw IllegalStateException("No items loaded. Current state: $this")
        }
    }
}