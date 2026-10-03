package com.studentcomplaint.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    var name: String = "",

    @Column(name = "email", nullable = false, length = 150)
    var email: String = "",

    @Column(name = "password", nullable = false, length = 255)
    var password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: Role = Role.STUDENT,

    @Column(name = "phone", length = 15)
    var phone: String? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)
