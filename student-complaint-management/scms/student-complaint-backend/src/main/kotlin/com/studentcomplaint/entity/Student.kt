package com.studentcomplaint.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "students")
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "student_id", nullable = false, length = 30)
    var studentId: String = "",

    @Column(name = "course", nullable = false, length = 100)
    var course: String = "",

    @Column(name = "year", nullable = false)
    var year: Int = 1,

    @Column(name = "department", length = 100)
    var department: String? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)
