package com.studentcomplaint.controller

import com.studentcomplaint.dto.RegisterRequest
import com.studentcomplaint.dto.StudentResponse
import com.studentcomplaint.dto.StudentUpdateRequest
import com.studentcomplaint.service.StudentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/students")
class StudentController(private val studentService: StudentService) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(): ResponseEntity<List<StudentResponse>> = ResponseEntity.ok(studentService.getAll())

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    fun getMe(auth: Authentication): ResponseEntity<StudentResponse> =
        ResponseEntity.ok(studentService.getMe(auth.name))

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long, auth: Authentication): ResponseEntity<StudentResponse> =
        ResponseEntity.ok(studentService.getById(id, auth.name))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody req: RegisterRequest): ResponseEntity<StudentResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(req))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @Valid @RequestBody req: StudentUpdateRequest,
        auth: Authentication
    ): ResponseEntity<StudentResponse> = ResponseEntity.ok(studentService.update(id, req, auth.name))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Map<String, String>> {
        studentService.delete(id)
        return ResponseEntity.ok(mapOf("message" to "Student deleted successfully"))
    }
}
