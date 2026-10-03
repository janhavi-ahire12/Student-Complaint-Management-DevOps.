package com.studentcomplaint.service

import com.studentcomplaint.dto.AuthResponse
import com.studentcomplaint.dto.LoginRequest
import com.studentcomplaint.dto.RegisterRequest
import com.studentcomplaint.dto.StudentResponse
import com.studentcomplaint.entity.Role
import com.studentcomplaint.exception.UnauthorizedException
import com.studentcomplaint.repository.StaffRepository
import com.studentcomplaint.repository.StudentRepository
import com.studentcomplaint.repository.UserRepository
import com.studentcomplaint.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val staffRepository: StaffRepository,
    private val studentService: StudentService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    /** Public registration is only for students. */
    fun register(req: RegisterRequest): StudentResponse = studentService.create(req)

    @Transactional(readOnly = true)
    fun login(req: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(req.email.trim().lowercase())
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw UnauthorizedException("Invalid email or password")
        }

        val profileId: Long? = when (user.role) {
            Role.STUDENT -> studentRepository.findByUserId(user.id!!)?.id
            Role.STAFF -> staffRepository.findByUserId(user.id!!)?.id
            Role.ADMIN -> null
        }

        val token = jwtUtil.generateToken(user.email, user.role.name)
        return AuthResponse(
            token = token,
            userId = user.id!!,
            name = user.name,
            email = user.email,
            role = user.role.name,
            profileId = profileId
        )
    }
}
