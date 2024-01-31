package com.example.studyleague.data.datasources

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dtos.SubjectDTO
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import enums.DateRangeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.time.LocalDate


private const val BASE_URL = "http://192.168.0.11:8080/"

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL).build()


class RemoteDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val retrofitService: StudyLeagueAPI by lazy {
        retrofit.create(StudyLeagueAPI::class.java)
    }

    suspend fun postStudent(student: StudentDTO): StudentDTO {
        return withContext(ioDispatcher) {
            retrofitService.postStudent(student)
        }
    }

    suspend fun fetchStudent(studentId: Long): StudentDTO {
        return withContext(ioDispatcher) {
            retrofitService.fetchStudent(studentId)
        }
    }

    suspend fun fetchStudentStats(studentId: Long): StudentStatisticsDTO {
        return withContext(ioDispatcher) {
            retrofitService.fetchStudentStats(studentId)
        }
    }

    suspend fun postSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        return withContext(ioDispatcher) {
            retrofitService.postSubjects(studentId, subjects)
        }
    }

    suspend fun fetchAllSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return withContext(ioDispatcher) {
            retrofitService.fetchAllSubjects(studentId, date)
        }
    }

    suspend fun fetchScheduledSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return withContext(ioDispatcher) {
            retrofitService.fetchScheduledSubjects(studentId, date)
        }
    }

    suspend fun postSubjectGoals(studentId: Long, subjectId: Long, dateRangeType: DateRangeType, goals: List<WriteGoalDTO>) {
        return withContext(ioDispatcher) {
            retrofitService.postSubjectGoals(studentId, subjectId, dateRangeType, goals)
        }
    }

    suspend fun postSubjectStats(studentId: Long, subjectId: Long, stats: List<WriteStatisticDTO>) {
        return withContext(ioDispatcher) {
            retrofitService.postSubjectStats(studentId, subjectId, stats)
        }
    }

    suspend fun postSchedule(studentId: Long, schedule: ScheduleDTO) {
        return withContext(ioDispatcher) {
            retrofitService.postSchedule(studentId, schedule)
        }
    }

    suspend fun fetchSchedule(studentId: Long): ScheduleDTO {
        return withContext(ioDispatcher) {
            retrofitService.fetchSchedule(studentId)
        }
    }
}