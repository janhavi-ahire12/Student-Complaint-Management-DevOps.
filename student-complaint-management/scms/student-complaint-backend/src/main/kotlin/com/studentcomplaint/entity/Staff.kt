package com.studentcomplaint.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "staff")
class Staff(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "staff_id", nullable = false, length = 30)
    var staffId: String = "",

    @Column(name = "department", length = 100)
    var department: String? = null,

    @Column(name = "designation", length = 100)
    var designation: String? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)
