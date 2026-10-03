package com.studentcomplaint.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "complaints")
class Complaint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    var assignedTo: Staff? = null,

    @Column(name = "title", nullable = false, length = 150)
    var title: String = "",

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    var description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    var category: Category = Category.OTHER,

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    var priority: Priority = Priority.MEDIUM,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ComplaintStatus = ComplaintStatus.OPEN,

    @Column(name = "resolution_remarks", columnDefinition = "TEXT")
    var resolutionRemarks: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "resolved_at")
    var resolvedAt: LocalDateTime? = null
)
