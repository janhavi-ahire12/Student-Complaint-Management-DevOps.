package com.studentcomplaint.controller

import com.studentcomplaint.service.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(private val dashboardService: DashboardService) {

    @GetMapping("/stats")
    fun stats(auth: Authentication): ResponseEntity<Map<String, Long>> =
        ResponseEntity.ok(dashboardService.stats(auth.name))
}
