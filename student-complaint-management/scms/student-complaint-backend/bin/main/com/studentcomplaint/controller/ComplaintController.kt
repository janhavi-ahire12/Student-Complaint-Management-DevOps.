package com.studentcomplaint.controller

import com.studentcomplaint.dto.ComplaintRequest
import com.studentcomplaint.dto.ComplaintResponse
import com.studentcomplaint.dto.ComplaintUpdateResponse
import com.studentcomplaint.dto.StatusUpdateRequest
import com.studentcomplaint.service.ComplaintService
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
@RequestMapping("/api/complaints")
class ComplaintController(private val complaintService: ComplaintService) {

    /** ADMIN: all, STUDENT: own, STAFF: assigned to me */
    @GetMapping
    fun getAll(auth: Authentication): ResponseEntity<List<ComplaintResponse>> =
        ResponseEntity.ok(complaintService.getAll(auth.name))

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long, auth: Authentication): ResponseEntity<ComplaintResponse> =
        ResponseEntity.ok(complaintService.getById(id, auth.name))

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    fun create(@Valid @RequestBody req: ComplaintRequest, auth: Authentication): ResponseEntity<ComplaintResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(complaintService.create(req, auth.name))

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    fun update(
        @PathVariable("id") id: Long,
        @Valid @RequestBody req: ComplaintRequest,
        auth: Authentication
    ): ResponseEntity<ComplaintResponse> = ResponseEntity.ok(complaintService.update(id, req, auth.name))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    fun delete(@PathVariable("id") id: Long, auth: Authentication): ResponseEntity<Map<String, String>> {
        complaintService.delete(id, auth.name)
        return ResponseEntity.ok(mapOf("message" to "Complaint deleted successfully"))
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    fun byStudent(
        @PathVariable("studentId") studentId: Long,
        auth: Authentication
    ): ResponseEntity<List<ComplaintResponse>> = ResponseEntity.ok(complaintService.getByStudent(studentId, auth.name))

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    fun byStaff(
        @PathVariable("staffId") staffId: Long,
        auth: Authentication
    ): ResponseEntity<List<ComplaintResponse>> = ResponseEntity.ok(complaintService.getByStaff(staffId, auth.name))

    @PutMapping("/{id}/assign/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun assign(
        @PathVariable("id") id: Long,
        @PathVariable("staffId") staffId: Long,
        auth: Authentication
    ): ResponseEntity<ComplaintResponse> = ResponseEntity.ok(complaintService.assign(id, staffId, auth.name))

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    fun changeStatus(
        @PathVariable("id") id: Long,
        @Valid @RequestBody req: StatusUpdateRequest,
        auth: Authentication
    ): ResponseEntity<ComplaintResponse> = ResponseEntity.ok(complaintService.changeStatus(id, req, auth.name))

    @GetMapping("/{id}/updates")
    fun updates(@PathVariable("id") id: Long, auth: Authentication): ResponseEntity<List<ComplaintUpdateResponse>> =
        ResponseEntity.ok(complaintService.getUpdates(id, auth.name))
}
