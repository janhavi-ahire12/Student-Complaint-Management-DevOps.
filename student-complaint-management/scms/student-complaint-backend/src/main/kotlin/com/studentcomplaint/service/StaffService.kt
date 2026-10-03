package com.studentcomplaint.service

import com.studentcomplaint.dto.StaffRequest
import com.studentcomplaint.dto.StaffResponse
import com.studentcomplaint.dto.StaffUpdateRequest
import com.studentcomplaint.dto.toResponse
import com.studentcomplaint.entity.Role
import com.studentcomplaint.entity.Staff
import com.studentcomplaint.entity.User
import com.studentcomplaint.exception.BadRequestException
import com.studentcomplaint.exception.ForbiddenException
import com.studentcomplaint.exception.NotFoundException
import com.studentcomplaint.exception.UnauthorizedException
import com.studentcomplaint.repository.StaffRepository
import com.studentcomplaint.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StaffService(
    private val staffRepository: StaffRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun create(req: StaffRequest): StaffResponse {
        val email = req.email.trim().lowercase()
        if (userRepository.existsByEmail(email)) throw BadRequestException("Email is already registered")
        if (staffRepository.existsByStaffId(req.staffId.trim())) throw BadRequestException("Staff ID already exists")

        val user = userRepository.save(
            User(
                name = req.name.trim(),
                email = email,
                password = passwordEncoder.encode(req.password),
                role = Role.STAFF,
                phone = req.phone
            )
        )
        val staff = staffRepository.save(
            Staff(
                user = user,
                staffId = req.staffId.trim(),
                department = req.department,
                designation = req.designation
            )
        )
        return staff.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAll(): List<StaffResponse> = staffRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: Long, email: String): StaffResponse {
        val staff = findStaff(id)
        checkAccess(staff, currentUser(email))
        return staff.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMe(email: String): StaffResponse {
        val user = currentUser(email)
        val staff = staffRepository.findByUserId(user.id!!) ?: throw NotFoundException("Staff profile not found")
        return staff.toResponse()
    }

    fun update(id: Long, req: StaffUpdateRequest, email: String): StaffResponse {
        val staff = findStaff(id)
        checkAccess(staff, currentUser(email))

        val newCode = req.staffId.trim()
        if (newCode != staff.staffId && staffRepository.existsByStaffId(newCode)) {
            throw BadRequestException("Staff ID already exists")
        }

        staff.user.name = req.name.trim()
        staff.user.phone = req.phone
        staff.staffId = newCode
        staff.department = req.department
        staff.designation = req.designation
        userRepository.save(staff.user)
        return staffRepository.save(staff).toResponse()
    }

    fun delete(id: Long) {
        val staff = findStaff(id)
        staffRepository.delete(staff)
        userRepository.delete(staff.user)
    }

    // ---------- helpers ----------
    private fun findStaff(id: Long): Staff =
        staffRepository.findById(id).orElseThrow { NotFoundException("Staff not found with id $id") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException("User not found")

    private fun checkAccess(staff: Staff, user: User) {
        if (user.role == Role.ADMIN) return
        if (staff.user.id != user.id) throw ForbiddenException("You can only access your own profile")
    }
}
