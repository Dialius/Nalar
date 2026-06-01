package com.davinza.nalar.data.repository

import com.davinza.nalar.data.remote.ApiService
import com.davinza.nalar.data.remote.model.Course
import com.davinza.nalar.data.remote.model.Module

class CourseRepository(private val apiService: ApiService) {
    suspend fun getCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getCourses()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getModules(courseId: Int): Result<List<Module>> {
        return try {
            val response = apiService.getModules(courseId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch modules"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
