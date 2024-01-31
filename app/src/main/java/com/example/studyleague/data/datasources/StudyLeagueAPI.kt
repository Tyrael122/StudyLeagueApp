package com.example.studyleague.data.datasources

import br.studyleague.util.EndpointPrefixes
import dtos.StudentDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyLeagueAPI {
    @POST(EndpointPrefixes.STUDENT)
    suspend fun postStudent(@Body student: StudentDTO) : StudentDTO

    @GET(EndpointPrefixes.STUDENT_ID)
    suspend fun fetchStudent(@Path("studentId") studentId: Long) : StudentDTO
}
