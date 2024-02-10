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

class StudentRepository(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun createStudent(student: StudentDTO): StudentDTO = remoteDataSource.postStudent(student)
    suspend fun fetchStudent(studentId: Long): StudentDTO = remoteDataSource.fetchStudent(studentId)
    suspend fun fetchStudentStats(studentId: Long, date: LocalDate): StudentStatisticsDTO = remoteDataSource.fetchStudentStats(studentId, date)
    suspend fun addSubjects(studentId: Long, subjects: List<SubjectDTO>) = remoteDataSource.postSubjects(studentId, subjects)
    suspend fun fetchAllSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> = remoteDataSource.fetchAllSubjects(studentId, date)
    suspend fun fetchScheduledSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> = remoteDataSource.fetchScheduledSubjects(studentId, date)
    suspend fun postSubjectGoals(studentId: Long, subjectId: Long, dateRangeType: DateRangeType, goals: List<WriteGoalDTO>) = remoteDataSource.postSubjectGoals(studentId, subjectId, dateRangeType, goals)
    suspend fun postSubjectStats(studentId: Long, subjectId: Long, stats: List<WriteStatisticDTO>) = remoteDataSource.postSubjectStats(studentId, subjectId, stats)
    suspend fun updateSchedule(studentId: Long, schedule: ScheduleDTO) = remoteDataSource.postSchedule(studentId, schedule)
    suspend fun fetchSchedule(studentId: Long): ScheduleDTO = remoteDataSource.fetchSchedule(studentId)
}