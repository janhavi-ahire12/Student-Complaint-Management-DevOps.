package com.studentcomplaint.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Enter a valid email address")
    val email: String = "",

    @field:NotBlank(message = "Password is required")
    val password: String = ""
)

/** Used for POST /api/auth/register and POST /api/students */
data class RegisterRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must be at most 100 characters")
    val name: String = "",

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Enter a valid email address")
    val email: String = "",

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String = "",

    @field:Size(max = 15, message = "Phone must be at most 15 characters")
    val phone: String? = null,

    @field:NotBlank(message = "Student ID is required")
    @field:Size(max = 30, message = "Student ID must be at most 30 characters")
    val studentId: String = "",

    @field:NotBlank(message = "Course is required")
    val course: String = "",

    @field:Min(value = 1, message = "Year must be between 1 and 6")
    @field:Max(value = 6, message = "Year must be between 1 and 6")
    val year: Int = 0,

    val department: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val name: String,
    val email: String,
    val role: String,
    /** students.id for STUDENT, staff.id for STAFF, null for ADMIN */
    val profileId: Long?
)
