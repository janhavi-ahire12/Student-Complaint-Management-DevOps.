package com.studentcomplaint.repository

import com.studentcomplaint.entity.Staff
import org.springframework.data.jpa.repository.JpaRepository

interface StaffRepository : JpaRepository<Staff, Long> {
    fun findByUserId(userId: Long): Staff?
    fun existsByStaffId(staffId: String): Boolean
}
