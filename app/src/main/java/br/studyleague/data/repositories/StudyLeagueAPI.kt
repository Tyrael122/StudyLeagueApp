package br.studyleague.data.repositories

import dtos.SubjectDTO
import dtos.signin.CredentialDTO
import dtos.signin.SignUpStudentData
import dtos.student.ScheduleDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.StudyCycleDTO
import dtos.student.StudyCycleEntryDTO
import dtos.student.WriteGoalDTO
import dtos.student.WriteStatisticDTO
import enums.DateRangeType
import enums.StudySchedulingMethods
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import util.EndpointPrefixes
import java.time.LocalDate
import java.time.LocalDateTime

interface StudyLeagueAPI {
    @POST(EndpointPrefixes.LOGIN)
    suspend fun login(@Body student: CredentialDTO): Response<StudentDTO>

    @POST(EndpointPrefixes.STUDENT)
    suspend fun postStudent(@Body student: SignUpStudentData): Response<StudentDTO>

    @GET(EndpointPrefixes.STUDENT_ID)
    suspend fun fetchStudent(@Path("studentId") studentId: Long): Response<StudentDTO>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STATS)
    suspend fun fetchStudentStats(@Path("studentId") studentId: Long, @Query("date") date: LocalDate): Response<StudentStatisticsDTO>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    suspend fun postSubjects(@Path("studentId") studentId: Long, @Body subjects: List<SubjectDTO>): Response<Unit>

    @HTTP(method = "DELETE", path = EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT, hasBody = true)
    suspend fun deleteSubjects(@Path("studentId") studentId: Long, @Body subjects: List<SubjectDTO>): Response<Unit>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    suspend fun fetchAllSubjects(@Path("studentId") studentId: Long, @Query("date") date: LocalDate): Response<List<SubjectDTO>>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULED_SUBJECT)
    suspend fun fetchScheduledSubjects(@Path("studentId") studentId: Long, @Query("date") date: LocalDate): Response<List<SubjectDTO>>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    suspend fun postSubjectGoals(@Path("studentId") studentId: Long, @Path("subjectId") subjectId: Long, @Query("dateRangeType") dateRangeType: DateRangeType, @Body goals: List<WriteGoalDTO>): Response<Unit>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    suspend fun postSubjectStats(@Path("studentId") studentId: Long, @Path("subjectId") subjectId: Long, @Body stats: List<WriteStatisticDTO>): Response<Unit>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULE)
    suspend fun postSchedule(@Path("studentId") studentId: Long, @Body schedule: ScheduleDTO): Response<Unit>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULE)
    suspend fun fetchSchedule(@Path("studentId") studentId: Long): Response<ScheduleDTO>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE)
    suspend fun postStudyCycle(@Path("studentId") studentId: Long, @Body entries: List<StudyCycleEntryDTO>): Response<Unit>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE)
    suspend fun fetchStudyCycle(@Path("studentId") studentId: Long): Response<StudyCycleDTO>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE_NEXT)
    suspend fun nextSubjectInStudyCycle(@Path("studentId") studentId: Long): Response<Unit>

    @GET(EndpointPrefixes.CURRENT_TIME)
    suspend fun fetchCurrentServerTime(): Response<LocalDateTime>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.CHANGE_SCHEDULE_METHOD)
    suspend fun changeScheduleMethod(@Path("studentId") studentId: Long, @Query("newMethod") newMethod: StudySchedulingMethods): Response<Unit>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STUDY_CYCLE + EndpointPrefixes.GOALS)
    suspend fun updateStudyCycleWeeklyGoal(@Path("studentId") studentId: Long, @Body weeklyGoal: Int): Response<Unit>
}
