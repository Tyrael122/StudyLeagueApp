package br.studyleague.data.repositories

import br.studyleague.data.datasources.RemoteDataSource
import dtos.SubjectDTO
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import enums.DateRangeType
import java.time.LocalDate
import java.time.LocalDateTime

class StudentRepository(
    private val dataSource: RemoteDataSource
) {
    suspend fun createStudent(student: StudentDTO): StudentDTO = dataSource.postStudent(student)
    suspend fun fetchStudent(studentId: Long): StudentDTO = dataSource.fetchStudent(studentId)
    suspend fun fetchStudentStats(studentId: Long, date: LocalDate): StudentStatisticsDTO = dataSource.fetchStudentStats(studentId, date)
    suspend fun addSubjects(studentId: Long, subjects: List<SubjectDTO>) = dataSource.postSubjects(studentId, subjects)
    suspend fun removeSubjects(studentId: Long, subjects: List<SubjectDTO>) = dataSource.deleteSubjects(studentId, subjects)
    suspend fun fetchAllSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> = dataSource.fetchAllSubjects(studentId, date)
    suspend fun fetchScheduledSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> = dataSource.fetchScheduledSubjects(studentId, date)
    suspend fun postSubjectGoals(studentId: Long, subjectId: Long, dateRangeType: DateRangeType, goals: List<WriteGoalDTO>) = dataSource.postSubjectGoals(studentId, subjectId, dateRangeType, goals)
    suspend fun postSubjectStats(studentId: Long, subjectId: Long, stats: List<WriteStatisticDTO>) = dataSource.postSubjectStats(studentId, subjectId, stats)
    suspend fun updateSchedule(studentId: Long, schedule: ScheduleDTO) = dataSource.postSchedule(studentId, schedule)
    suspend fun fetchSchedule(studentId: Long): ScheduleDTO = dataSource.fetchSchedule(studentId)
    suspend fun fetchCurrentServerTime(): LocalDateTime = dataSource.fetchCurrentServerTime()
}