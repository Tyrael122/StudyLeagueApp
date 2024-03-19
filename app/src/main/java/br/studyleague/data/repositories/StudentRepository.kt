package br.studyleague.data.repositories

import br.studyleague.data.NetworkRequestManager
import br.studyleague.util.debug
import br.studyleague.util.error
import dtos.SubjectDTO
import dtos.signin.CredentialDTO
import dtos.signin.SignUpStudentData
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import enums.DateRangeType
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

private val retrofit = RetrofitBuilder.buildRetrofit()

class StudentRepository {
    private val retrofitService: StudyLeagueAPI by lazy {
        retrofit.create(StudyLeagueAPI::class.java)
    }

    private val networkRequestManager = NetworkRequestManager()

    suspend fun login(credential: CredentialDTO): StudentDTO {
        return parseEntityFromNetworkRequest { retrofitService.login(credential) }
    }

    suspend fun postStudent(signUpStudent: SignUpStudentData): StudentDTO {
        return parseEntityFromNetworkRequest { retrofitService.postStudent(signUpStudent) }
    }

    suspend fun fetchStudent(studentId: Long): StudentDTO {
        return parseEntityFromNetworkRequest { retrofitService.fetchStudent(studentId) }
    }

    suspend fun fetchStudentStats(studentId: Long, date: LocalDate): StudentStatisticsDTO {
        return parseEntityFromNetworkRequest { retrofitService.fetchStudentStats(studentId, date) }
    }

    suspend fun postSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        doNetworkRequest { retrofitService.postSubjects(studentId, subjects) }
    }

    suspend fun deleteSubjects(studentId: Long, subjects: List<SubjectDTO>) {
        doNetworkRequest { retrofitService.deleteSubjects(studentId, subjects) }
    }

    suspend fun fetchAllSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return parseEntityFromNetworkRequest { retrofitService.fetchAllSubjects(studentId, date) }
    }

    suspend fun fetchScheduledSubjects(studentId: Long, date: LocalDate): List<SubjectDTO> {
        return parseEntityFromNetworkRequest {
            retrofitService.fetchScheduledSubjects(
                studentId, date
            )
        }
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
        return parseEntityFromNetworkRequest { retrofitService.fetchSchedule(studentId) }
    }

    suspend fun fetchCurrentServerTime(): LocalDateTime {
        return parseEntityFromNetworkRequest { retrofitService.fetchCurrentServerTime() }
    }

    private suspend inline fun <T> parseEntityFromNetworkRequest(crossinline request: suspend () -> Response<T>): T {
        return doNetworkRequest { request() }
            ?: throw RuntimeException("Resposta vazia do servidor")
    }

    private suspend inline fun <K> doNetworkRequest(crossinline request: suspend () -> Response<K>): K? {
        return networkRequestManager.doNetworkRequestWithCancellation { randomId ->
            val idMessage = "ID $randomId"

            debug("Starting network request with $idMessage.")

            val response = request()

            if (!response.isSuccessful) {
                error(
                    "Finishing network request with $idMessage with error.",
                    HttpException(response)
                )

                throwHttpException(response, parseErrorBodyDetailMessage(response.errorBody()))
            }

            debug(
                "Finished network request successfully with $idMessage with response $response"
            )

            response.body()
        }
    }

    private fun <T> throwHttpException(response: Response<T>, errorMessage: String) {
        val exception = RuntimeException(errorMessage)

        error(errorMessage, HttpException(response))

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