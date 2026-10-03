package com.studentcomplaint.dto

import com.studentcomplaint.entity.Complaint
import com.studentcomplaint.entity.ComplaintUpdate
import com.studentcomplaint.entity.Staff
import com.studentcomplaint.entity.Student

fun Student.toResponse() = StudentResponse(
    id = id!!,
    userId = user.id!!,
    name = user.name,
    email = user.email,
    phone = user.phone,
    studentId = studentId,
    course = course,
    year = year,
    department = department,
    createdAt = createdAt
)

fun Staff.toResponse() = StaffResponse(
    id = id!!,
    userId = user.id!!,
    name = user.name,
    email = user.email,
    phone = user.phone,
    staffId = staffId,
    department = department,
    designation = designation,
    createdAt = createdAt
)

fun Complaint.toResponse() = ComplaintResponse(
    id = id!!,
    title = title,
    description = description,
    category = category,
    priority = priority,
    status = status,
    resolutionRemarks = resolutionRemarks,
    createdAt = createdAt,
    resolvedAt = resolvedAt,
    studentId = student.id!!,
    studentName = student.user.name,
    studentCode = student.studentId,
    assignedToId = assignedTo?.id,
    assignedToName = assignedTo?.user?.name
)

fun ComplaintUpdate.toResponse() = ComplaintUpdateResponse(
    id = id!!,
    complaintId = complaint.id!!,
    updatedByName = updatedBy.name,
    updatedByRole = updatedBy.role.name,
    oldStatus = oldStatus,
    newStatus = newStatus,
    remarks = remarks,
    updatedAt = updatedAt
)
