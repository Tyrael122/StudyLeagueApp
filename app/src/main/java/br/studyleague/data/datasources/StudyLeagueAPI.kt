package br.studyleague.data.datasources

import dtos.SubjectDTO
import dtos.statistic.WriteStatisticDTO
import dtos.student.StudentDTO
import dtos.student.StudentStatisticsDTO
import dtos.student.goals.WriteGoalDTO
import dtos.student.schedule.ScheduleDTO
import enums.DateRangeType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import util.EndpointPrefixes
import java.time.LocalDate

interface StudyLeagueAPI {
    @POST(EndpointPrefixes.STUDENT)
    suspend fun postStudent(@Body student: StudentDTO) : StudentDTO

    @GET(EndpointPrefixes.STUDENT_ID)
    suspend fun fetchStudent(@Path("studentId") studentId: Long) : StudentDTO

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.STATS)
    suspend fun fetchStudentStats(@Path("studentId") studentId: Long, @Query("date") date: LocalDate) : StudentStatisticsDTO

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    suspend fun postSubjects(@Path("studentId") studentId: Long, @Body subjects: List<SubjectDTO>)

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT)
    suspend fun fetchAllSubjects(@Path("studentId") studentId: Long, @Query("date") date: LocalDate) : List<SubjectDTO>

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULED_SUBJECT)
    suspend fun fetchScheduledSubjects(@Path("studentId") studentId: Long, @Query("date") date: LocalDate) : List<SubjectDTO>

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.GOALS)
    suspend fun postSubjectGoals(@Path("studentId") studentId: Long, @Path("subjectId") subjectId: Long, @Query("dateRangeType") dateRangeType: DateRangeType, @Body goals: List<WriteGoalDTO>)

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SUBJECT_ID + EndpointPrefixes.STATS)
    suspend fun postSubjectStats(@Path("studentId") studentId: Long, @Path("subjectId") subjectId: Long, @Body stats: List<WriteStatisticDTO>)

    @POST(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULE)
    suspend fun postSchedule(@Path("studentId") studentId: Long, @Body schedule: ScheduleDTO)

    @GET(EndpointPrefixes.STUDENT_ID + EndpointPrefixes.SCHEDULE)
    suspend fun fetchSchedule(@Path("studentId") studentId: Long) : ScheduleDTO
}
