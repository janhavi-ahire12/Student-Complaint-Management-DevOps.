package com.studentcomplaint.controller

import com.studentcomplaint.dto.AuthResponse
import com.studentcomplaint.dto.LoginRequest
import com.studentcomplaint.dto.RegisterRequest
import com.studentcomplaint.dto.StudentResponse
import com.studentcomplaint.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest): ResponseEntity<StudentResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req))

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.login(req))
}
