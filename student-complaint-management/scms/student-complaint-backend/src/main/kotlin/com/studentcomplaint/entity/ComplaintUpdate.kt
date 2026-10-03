package com.studentcomplaint.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "complaint_updates")
class ComplaintUpdate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "complaint_id", nullable = false)
    var complaint: Complaint,

    @ManyToOne
    @JoinColumn(name = "updated_by", nullable = false)
    var updatedBy: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    var oldStatus: ComplaintStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20)
    var newStatus: ComplaintStatus? = null,

    @Column(name = "remarks", columnDefinition = "TEXT")
    var remarks: String? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
