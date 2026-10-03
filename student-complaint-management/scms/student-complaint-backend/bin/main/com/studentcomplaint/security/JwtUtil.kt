package com.studentcomplaint.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtUtil(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(email: String, role: String): String =
        Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

    fun getEmail(token: String): String = parse(token).subject

    fun isValid(token: String): Boolean =
        try {
            parse(token)
            true
        } catch (e: Exception) {
            false
        }

    private fun parse(token: String) =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
}
