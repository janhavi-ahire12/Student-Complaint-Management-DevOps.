package com.studentcomplaint.service

import com.studentcomplaint.entity.ComplaintStatus
import com.studentcomplaint.entity.Role
import com.studentcomplaint.exception.NotFoundException
import com.studentcomplaint.exception.UnauthorizedException
import com.studentcomplaint.repository.ComplaintRepository
import com.studentcomplaint.repository.StaffRepository
import com.studentcomplaint.repository.StudentRepository
import com.studentcomplaint.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DashboardService(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val staffRepository: StaffRepository,
    private val complaintRepository: ComplaintRepository
) {

    fun stats(email: String): Map<String, Long> {
        val user = userRepository.findByEmail(email) ?: throw UnauthorizedException("User not found")

        return when (user.role) {
            Role.ADMIN -> linkedMapOf(
                "totalStudents" to studentRepository.count(),
                "totalStaff" to staffRepository.count(),
                "totalComplaints" to complaintRepository.count(),
                "open" to complaintRepository.countByStatus(ComplaintStatus.OPEN),
                "assigned" to complaintRepository.countByStatus(ComplaintStatus.ASSIGNED),
                "inProgress" to complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS),
                "resolved" to complaintRepository.countByStatus(ComplaintStatus.RESOLVED),
                "rejected" to complaintRepository.countByStatus(ComplaintStatus.REJECTED)
            )

            Role.STUDENT -> {
                val sid = studentRepository.findByUserId(user.id!!)?.id
                    ?: throw NotFoundException("Student profile not found")
                linkedMapOf(
                    "totalComplaints" to complaintRepository.countByStudentId(sid),
                    "open" to complaintRepository.countByStudentIdAndStatus(sid, ComplaintStatus.OPEN),
                    "assigned" to complaintRepository.countByStudentIdAndStatus(sid, ComplaintStatus.ASSIGNED),
                    "inProgress" to complaintRepository.countByStudentIdAndStatus(sid, ComplaintStatus.IN_PROGRESS),
                    "resolved" to complaintRepository.countByStudentIdAndStatus(sid, ComplaintStatus.RESOLVED),
                    "rejected" to complaintRepository.countByStudentIdAndStatus(sid, ComplaintStatus.REJECTED)
                )
            }

            Role.STAFF -> {
                val sid = staffRepository.findByUserId(user.id!!)?.id
                    ?: throw NotFoundException("Staff profile not found")
                linkedMapOf(
                    "totalComplaints" to complaintRepository.countByAssignedToId(sid),
                    "assigned" to complaintRepository.countByAssignedToIdAndStatus(sid, ComplaintStatus.ASSIGNED),
                    "inProgress" to complaintRepository.countByAssignedToIdAndStatus(sid, ComplaintStatus.IN_PROGRESS),
                    "resolved" to complaintRepository.countByAssignedToIdAndStatus(sid, ComplaintStatus.RESOLVED)
                )
            }
        }
    }
}
