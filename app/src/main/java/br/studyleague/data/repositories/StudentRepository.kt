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
import retrofit2.HttpException
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
        return doNetworkRequest { retrofitService.login(credential) }
    }

    suspend fun postStudent(signUpStudent: SignUpStudentData): StudentDTO {
        return doNetworkRequest { retrofitService.postStudent(signUpStudent) }
    }

    suspend fun fetchStudent(studentId: Long): StudentDTO {
        return doNetworkRequest { retrofitService.fetchStudent(studentId) }
    }

    suspend fun fetchStudentStats(studentId: Long, date: LocalDate): StudentStatisticsDTO {
        return doNetworkRequest { retrofitService.fetchStudentStats(studentId, date) }
    }

    suspend fun postSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        doNetworkRequest { retrofitService.postSubjects(studentId, subjects) }
    }

    suspend fun deleteSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        doNetworkRequest { retrofitService.deleteSubjects(studentId, subjects) }
    }

    suspend fun fetchAllSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return doNetworkRequest { retrofitService.fetchAllSubjects(studentId, date) }
    }

    suspend fun fetchScheduledSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return doNetworkRequest { retrofitService.fetchScheduledSubjects(studentId, date) }
    }

    suspend fun postSubjectGoals(
        studentId: Long, subjectId: Long, dateRangeType: DateRangeType, goals: List<WriteGoalDTO>
    ) {
        doNetworkRequest {
            retrofitService.postSubjectGoals(
                studentId, subjectId, dateRangeType, goals
            )
        }
    }

    suspend fun postSubjectStats(studentId: Long, subjectId: Long, stats: List<WriteStatisticDTO>) {
        doNetworkRequest { retrofitService.postSubjectStats(studentId, subjectId, stats) }
    }

    suspend fun postSchedule(studentId: Long, schedule: ScheduleDTO) {
        doNetworkRequest { retrofitService.postSchedule(studentId, schedule) }
    }

    suspend fun fetchSchedule(studentId: Long): ScheduleDTO {
        return doNetworkRequest { retrofitService.fetchSchedule(studentId) }
    }

    suspend fun fetchCurrentServerTime(): LocalDateTime {
        return doNetworkRequest { retrofitService.fetchCurrentServerTime() }
    }

    private suspend fun <T> doNetworkRequest(request: suspend () -> Response<T>): T {
        return withContext(ioDispatcher) {
            val response = request()

            parseEntityInBodyOrThrow(response)
        }
    }

    private fun <T> parseEntityInBodyOrThrow(response: Response<T>): T {
        if (!response.isSuccessful) throwHttpException(response, parseErrorBodyDetailMessage(response.errorBody()))

        if (response.body() == null) throwHttpException(response, "Resposta vazia do servidor")

        return response.body()!!
    }

    private fun <T> throwHttpException(response: Response<T>, errorMessage: String) {
        val exception = RuntimeException(errorMessage)

        CustomLogger.e("StudentRepository", errorMessage, HttpException(response))

        throw exception
    }

    private fun parseErrorBodyDetailMessage(responseBody: ResponseBody?): String {
        if (responseBody == null) {
            return "Erro desconhecido"
        }

        val errorBody = responseBody.string()
        return jsonSerializer.parseToJsonElement(errorBody).jsonObject["message"].toString()
    }
}