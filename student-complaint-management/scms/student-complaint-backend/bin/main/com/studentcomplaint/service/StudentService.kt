package com.studentcomplaint.service

import com.studentcomplaint.dto.RegisterRequest
import com.studentcomplaint.dto.StudentResponse
import com.studentcomplaint.dto.StudentUpdateRequest
import com.studentcomplaint.dto.toResponse
import com.studentcomplaint.entity.Role
import com.studentcomplaint.entity.Student
import com.studentcomplaint.entity.User
import com.studentcomplaint.exception.BadRequestException
import com.studentcomplaint.exception.ForbiddenException
import com.studentcomplaint.exception.NotFoundException
import com.studentcomplaint.exception.UnauthorizedException
import com.studentcomplaint.repository.StudentRepository
import com.studentcomplaint.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudentService(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun create(req: RegisterRequest): StudentResponse {
        val email = req.email.trim().lowercase()
        if (userRepository.existsByEmail(email)) throw BadRequestException("Email is already registered")
        if (studentRepository.existsByStudentId(req.studentId.trim())) throw BadRequestException("Student ID already exists")

        val user = userRepository.save(
            User(
                name = req.name.trim(),
                email = email,
                password = passwordEncoder.encode(req.password),
                role = Role.STUDENT,
                phone = req.phone
            )
        )
        val student = studentRepository.save(
            Student(
                user = user,
                studentId = req.studentId.trim(),
                course = req.course.trim(),
                year = req.year,
                department = req.department
            )
        )
        return student.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAll(): List<StudentResponse> = studentRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: Long, email: String): StudentResponse {
        val student = findStudent(id)
        checkAccess(student, currentUser(email))
        return student.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMe(email: String): StudentResponse {
        val user = currentUser(email)
        val student = studentRepository.findByUserId(user.id!!)
            ?: throw NotFoundException("Student profile not found")
        return student.toResponse()
    }

    fun update(id: Long, req: StudentUpdateRequest, email: String): StudentResponse {
        val student = findStudent(id)
        checkAccess(student, currentUser(email))

        val newCode = req.studentId.trim()
        if (newCode != student.studentId && studentRepository.existsByStudentId(newCode)) {
            throw BadRequestException("Student ID already exists")
        }

        student.user.name = req.name.trim()
        student.user.phone = req.phone
        student.studentId = newCode
        student.course = req.course.trim()
        student.year = req.year
        student.department = req.department
        userRepository.save(student.user)
        return studentRepository.save(student).toResponse()
    }

    fun delete(id: Long) {
        val student = findStudent(id)
        studentRepository.delete(student)
        userRepository.delete(student.user)
    }

    // ---------- helpers ----------
    private fun findStudent(id: Long): Student =
        studentRepository.findById(id).orElseThrow { NotFoundException("Student not found with id $id") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException("User not found")

    private fun checkAccess(student: Student, user: User) {
        if (user.role == Role.ADMIN) return
        if (student.user.id != user.id) throw ForbiddenException("You can only access your own profile")
    }
}
