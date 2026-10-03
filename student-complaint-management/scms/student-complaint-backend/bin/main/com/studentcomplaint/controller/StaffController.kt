package com.studentcomplaint.controller

import com.studentcomplaint.dto.StaffRequest
import com.studentcomplaint.dto.StaffResponse
import com.studentcomplaint.dto.StaffUpdateRequest
import com.studentcomplaint.service.StaffService
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
@RequestMapping("/api/staff")
class StaffController(private val staffService: StaffService) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(): ResponseEntity<List<StaffResponse>> = ResponseEntity.ok(staffService.getAll())

    @GetMapping("/me")
    @PreAuthorize("hasRole('STAFF')")
    fun getMe(auth: Authentication): ResponseEntity<StaffResponse> =
        ResponseEntity.ok(staffService.getMe(auth.name))

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long, auth: Authentication): ResponseEntity<StaffResponse> =
        ResponseEntity.ok(staffService.getById(id, auth.name))

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody req: StaffRequest): ResponseEntity<StaffResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(req))

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @Valid @RequestBody req: StaffUpdateRequest,
        auth: Authentication
    ): ResponseEntity<StaffResponse> = ResponseEntity.ok(staffService.update(id, req, auth.name))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Map<String, String>> {
        staffService.delete(id)
        return ResponseEntity.ok(mapOf("message" to "Staff deleted successfully"))
    }
}
