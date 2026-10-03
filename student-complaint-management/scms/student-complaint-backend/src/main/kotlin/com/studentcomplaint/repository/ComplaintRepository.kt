package com.studentcomplaint.repository

import com.studentcomplaint.entity.Complaint
import com.studentcomplaint.entity.ComplaintStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintRepository : JpaRepository<Complaint, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Complaint>
    fun findByStudentIdOrderByCreatedAtDesc(studentId: Long): List<Complaint>
    fun findByAssignedToIdOrderByCreatedAtDesc(staffId: Long): List<Complaint>

    fun countByStatus(status: ComplaintStatus): Long
    fun countByStudentId(studentId: Long): Long
    fun countByStudentIdAndStatus(studentId: Long, status: ComplaintStatus): Long
    fun countByAssignedToId(staffId: Long): Long
    fun countByAssignedToIdAndStatus(staffId: Long, status: ComplaintStatus): Long
}
