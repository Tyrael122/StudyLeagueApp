package br.studyleague.data.repositories

import br.studyleague.util.CustomLogger
import dtos.SubjectDTO
import dtos.signin.CredentialDTO
import dtos.signin.SignUpStudentData
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import enums.DateRangeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

private val retrofit = RetrofitBuilder.buildRetrofit()

class StudentRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val retrofitService: StudyLeagueAPI by lazy {
        retrofit.create(StudyLeagueAPI::class.java)
    }

    suspend fun login(credential: CredentialDTO): StudentDTO {
        return withContext(ioDispatcher) {
            parseEntityInBodyOrThrow(retrofitService.login(credential))
        }
    }

    suspend fun postStudent(signUpStudent: SignUpStudentData): StudentDTO {
        return withContext(ioDispatcher) {
            parseEntityInBodyOrThrow(retrofitService.postStudent(signUpStudent))
        }
    }

    suspend fun fetchStudent(studentId: Long): StudentDTO {
        return withContext(ioDispatcher) {
            retrofitService.fetchStudent(studentId)
        }
    }

    suspend fun fetchStudentStats(studentId: Long, date: LocalDate): StudentStatisticsDTO {
        return withContext(ioDispatcher) {
            retrofitService.fetchStudentStats(studentId, date)
        }
    }

    suspend fun postSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        return withContext(ioDispatcher) {
            retrofitService.postSubjects(studentId, subjects)
        }
    }

    suspend fun deleteSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        return withContext(ioDispatcher) {
            retrofitService.deleteSubjects(studentId, subjects)
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

    suspend fun postSubjectGoals(
        studentId: Long, subjectId: Long, dateRangeType: DateRangeType, goals: List<WriteGoalDTO>
    ) {
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

    suspend fun fetchCurrentServerTime(): LocalDateTime {
        return withContext(ioDispatcher) {
            retrofitService.fetchCurrentServerTime()
        }
    }

    private fun <T> parseEntityInBodyOrThrow(response: Response<T>): T {
        if (!response.isSuccessful)
            throw Exception(parseErrorBodyDetailMessage(response.errorBody()))

        if (response.body() == null)
            throw Exception("Erro desconhecido")

        return response.body()!!
    }

    private fun parseErrorBodyDetailMessage(responseBody: ResponseBody?): String {
        if (responseBody == null) {
            return "Erro desconhecido"
        }

        val errorBody = responseBody.string()
        return jsonSerializer.parseToJsonElement(errorBody).jsonObject["message"].toString()
    }
}