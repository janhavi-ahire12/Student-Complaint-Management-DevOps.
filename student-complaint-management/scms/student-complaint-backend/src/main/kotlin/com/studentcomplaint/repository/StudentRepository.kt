package com.studentcomplaint.repository

import com.studentcomplaint.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long> {
    fun findByUserId(userId: Long): Student?
    fun existsByStudentId(studentId: String): Boolean
}
