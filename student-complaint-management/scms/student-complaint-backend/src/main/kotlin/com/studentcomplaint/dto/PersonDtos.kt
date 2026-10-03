package com.studentcomplaint.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class StudentUpdateRequest(
    @field:NotBlank(message = "Name is required")
    val name: String = "",

    @field:Size(max = 15, message = "Phone must be at most 15 characters")
    val phone: String? = null,

    @field:NotBlank(message = "Student ID is required")
    val studentId: String = "",

    @field:NotBlank(message = "Course is required")
    val course: String = "",

    @field:Min(value = 1, message = "Year must be between 1 and 6")
    @field:Max(value = 6, message = "Year must be between 1 and 6")
    val year: Int = 0,

    val department: String? = null
)

data class StaffRequest(
    @field:NotBlank(message = "Name is required")
    val name: String = "",

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Enter a valid email address")
    val email: String = "",

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String = "",

    @field:Size(max = 15, message = "Phone must be at most 15 characters")
    val phone: String? = null,

    @field:NotBlank(message = "Staff ID is required")
    @field:Size(max = 30, message = "Staff ID must be at most 30 characters")
    val staffId: String = "",

    val department: String? = null,
    val designation: String? = null
)

data class StaffUpdateRequest(
    @field:NotBlank(message = "Name is required")
    val name: String = "",

    @field:Size(max = 15, message = "Phone must be at most 15 characters")
    val phone: String? = null,

    @field:NotBlank(message = "Staff ID is required")
    val staffId: String = "",

    val department: String? = null,
    val designation: String? = null
)

data class StudentResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val studentId: String,
    val course: String,
    val year: Int,
    val department: String?,
    val createdAt: LocalDateTime
)

data class StaffResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val staffId: String,
    val department: String?,
    val designation: String?,
    val createdAt: LocalDateTime
)
