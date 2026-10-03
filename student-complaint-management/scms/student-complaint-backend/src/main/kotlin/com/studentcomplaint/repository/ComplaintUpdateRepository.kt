package com.studentcomplaint.repository

import com.studentcomplaint.entity.ComplaintUpdate
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintUpdateRepository : JpaRepository<ComplaintUpdate, Long> {
    fun findByComplaintIdOrderByUpdatedAtAscIdAsc(complaintId: Long): List<ComplaintUpdate>
    fun deleteByComplaintId(complaintId: Long)
}
