package com.example.studyleague.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studyleague.data.DataStoreManager
import com.example.studyleague.data.datasources.RemoteDataSource
import com.example.studyleague.data.repositories.StudentRepository
import com.example.studyleague.model.Schedule
import com.example.studyleague.model.Student
import com.example.studyleague.model.StudentStats
import com.example.studyleague.model.Subject
import com.example.studyleague.ui.components.ScheduleEntryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class StudentViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState = _uiState.asStateFlow()

    private val studentRepository = StudentRepository(RemoteDataSource())

    init {
        viewModelScope.launch {
//            val studentId = dataStoreManager.getValueFromDataStore(DataStoreKeys.studentIdKey)
//            if (studentId == null) {
//                createStudent()
//            } else {
//                fetchStudent(studentId)
//            }

            fetchStudent(1)
        }
    }

    fun updateSubjects(subjects: List<Subject>) {
        // TODO: Make a request to the API.
    }

    fun updateScheduleEntries(scheduleEntries: List<ScheduleEntryData>) {
        val currentScheduleEntries = getScheduleEntries()
        if (currentScheduleEntries.containsAll(scheduleEntries) && currentScheduleEntries.size == scheduleEntries.size) {
            return
        }

        // TODO: Make a request to the API.
    }

    fun fetchSubjectsOfDay(date: LocalDate): List<Subject> {
        // TODO: Make a request to the API.
        return emptyList()
    }

    fun updateSubjectDailyStats(updatedStats: List<Float>) {
        // TODO: Check if it's equal before making the request.

        // TODO: Make a request to the API.
    }

    fun updateSelectedSubjectName(subjectName: String?) {
        val currentName = uiState.value.selectedSubject.subjectDTO.name
        if (currentName == subjectName) {
            return
        }

        // TODO: Make API request.
    }

    fun updateSelectedSubjectAlltimeGoals(goals: List<Float>) {
        // TODO: Check if it's equal before making the request.

        // TODO: Make API request
    }

    fun updateSelectedSubjectWeeklyGoals(goals: List<Float>) {
        // TODO: Check if it's equal before making the request.

        // TODO: Make API request
    }

    fun selectSubject(subject: Subject) {
        _uiState.update {
            it.copy(selectedSubject = subject)
        }
    }

    fun getScheduleEntries(): List<ScheduleEntryData> {
        val listOfScheduleEntries = mutableListOf<ScheduleEntryData>()

        for (day in uiState.value.schedule.scheduleDTO.days) {
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

    private fun findSubjectById(subjectId: Long): Subject {
        return uiState.value.subjects.find { it.subjectDTO.id == subjectId }
            ?: throw IllegalArgumentException("Subject not found.")
    }

    private fun createStudent() {

    }

    private fun fetchStudent(studentId: Long) {
        viewModelScope.launch {
            val studentDTO = studentRepository.fetchStudent(studentId)
            _uiState.update {
                it.copy(student = Student(studentDTO = studentDTO))
            }
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
    val subjects: List<Subject> = listOf(),
    val schedule: Schedule = Schedule(),
    val selectedSubject: Subject = Subject(),
    val student: Student = Student(),
    val studentStats: StudentStats = StudentStats()
)