package com.studentcomplaint.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.studentcomplaint.security.JwtAuthFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    writeError(response, 401, "Unauthorized: please login and send a valid JWT token")
                }
                it.accessDeniedHandler { _, response, _ ->
                    writeError(response, 403, "Access denied: you do not have permission for this action")
                }
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    private fun writeError(response: HttpServletResponse, status: Int, message: String) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write(objectMapper.writeValueAsString(mapOf("status" to status, "message" to message)))
    }
}
