package com.example.studyleague.data.repositories

import com.example.studyleague.data.datasources.RemoteDataSource
import dtos.student.StudentDTO

class StudentRepository(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun createStudent(student: StudentDTO): StudentDTO = remoteDataSource.postStudent(student)
    suspend fun fetchStudent(studentId: Long): StudentDTO = remoteDataSource.fetchStudent(studentId)
}