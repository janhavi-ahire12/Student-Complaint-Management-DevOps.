package com.studentcomplaint.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            try {
                if (jwtUtil.isValid(token) && SecurityContextHolder.getContext().authentication == null) {
                    val email = jwtUtil.getEmail(token)
                    val userDetails = userDetailsService.loadUserByUsername(email)
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: Exception) {
                // Invalid token or unknown user: continue without authentication -> 401 later
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
