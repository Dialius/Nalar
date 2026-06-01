package com.davinza.nalar.data.remote.model

data class Course(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String,
    val level: String,
    val image_url: String?,
    val created_at: String
)

data class Module(
    val id: Int,
    val course_id: Int,
    val title: String,
    val content: String,
    val points_reward: Int,
    val order_index: Int,
    val created_at: String
)
