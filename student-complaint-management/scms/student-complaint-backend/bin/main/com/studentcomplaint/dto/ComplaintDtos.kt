package com.studentcomplaint.dto

import com.studentcomplaint.entity.Category
import com.studentcomplaint.entity.ComplaintStatus
import com.studentcomplaint.entity.Priority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ComplaintRequest(
    @field:NotBlank(message = "Complaint title is required")
    @field:Size(max = 150, message = "Title must be at most 150 characters")
    val title: String = "",

    @field:NotBlank(message = "Complaint description is required")
    val description: String = "",

    @field:NotBlank(message = "Category is required")
    @field:Pattern(
        regexp = "ACADEMIC|HOSTEL|ELECTRICAL|CLEANING|MAINTENANCE|OTHER",
        message = "Category must be ACADEMIC, HOSTEL, ELECTRICAL, CLEANING, MAINTENANCE or OTHER"
    )
    val category: String = "",

    @field:NotBlank(message = "Priority is required")
    @field:Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM or HIGH")
    val priority: String = ""
)

data class StatusUpdateRequest(
    @field:NotBlank(message = "Status is required")
    @field:Pattern(
        regexp = "OPEN|ASSIGNED|IN_PROGRESS|RESOLVED|REJECTED",
        message = "Status must be OPEN, ASSIGNED, IN_PROGRESS, RESOLVED or REJECTED"
    )
    val status: String = "",

    val remarks: String? = null
)

data class ComplaintResponse(
    val id: Long,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val status: ComplaintStatus,
    val resolutionRemarks: String?,
    val createdAt: LocalDateTime,
    val resolvedAt: LocalDateTime?,
    val studentId: Long,
    val studentName: String,
    val studentCode: String,
    val assignedToId: Long?,
    val assignedToName: String?
)

data class ComplaintUpdateResponse(
    val id: Long,
    val complaintId: Long,
    val updatedByName: String,
    val updatedByRole: String,
    val oldStatus: ComplaintStatus?,
    val newStatus: ComplaintStatus?,
    val remarks: String?,
    val updatedAt: LocalDateTime
)
