package com.example.studyleague.data.datasources

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dtos.student.StudentDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


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
}