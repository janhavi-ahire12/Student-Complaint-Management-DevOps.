package com.studentcomplaint.service

import com.studentcomplaint.dto.ComplaintRequest
import com.studentcomplaint.dto.ComplaintResponse
import com.studentcomplaint.dto.ComplaintUpdateResponse
import com.studentcomplaint.dto.StatusUpdateRequest
import com.studentcomplaint.dto.toResponse
import com.studentcomplaint.entity.Category
import com.studentcomplaint.entity.Complaint
import com.studentcomplaint.entity.ComplaintStatus
import com.studentcomplaint.entity.ComplaintUpdate
import com.studentcomplaint.entity.Priority
import com.studentcomplaint.entity.Role
import com.studentcomplaint.entity.Student
import com.studentcomplaint.entity.User
import com.studentcomplaint.exception.BadRequestException
import com.studentcomplaint.exception.ForbiddenException
import com.studentcomplaint.exception.NotFoundException
import com.studentcomplaint.exception.UnauthorizedException
import com.studentcomplaint.repository.ComplaintRepository
import com.studentcomplaint.repository.ComplaintUpdateRepository
import com.studentcomplaint.repository.StaffRepository
import com.studentcomplaint.repository.StudentRepository
import com.studentcomplaint.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ComplaintService(
    private val complaintRepository: ComplaintRepository,
    private val updateRepository: ComplaintUpdateRepository,
    private val studentRepository: StudentRepository,
    private val staffRepository: StaffRepository,
    private val userRepository: UserRepository
) {

    // ---------------- READ ----------------

    /** ADMIN: all complaints, STUDENT: own complaints, STAFF: assigned complaints */
    @Transactional(readOnly = true)
    fun getAll(email: String): List<ComplaintResponse> {
        val user = currentUser(email)
        val list = when (user.role) {
            Role.ADMIN -> complaintRepository.findAllByOrderByCreatedAtDesc()
            Role.STUDENT -> complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentOf(user).id!!)
            Role.STAFF -> {
                val staff = staffRepository.findByUserId(user.id!!) ?: throw NotFoundException("Staff profile not found")
                complaintRepository.findByAssignedToIdOrderByCreatedAtDesc(staff.id!!)
            }
        }
        return list.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, email: String): ComplaintResponse {
        val complaint = findComplaint(id)
        checkCanView(complaint, currentUser(email))
        return complaint.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByStudent(studentId: Long, email: String): List<ComplaintResponse> {
        val user = currentUser(email)
        if (user.role == Role.STUDENT && studentOf(user).id != studentId) {
            throw ForbiddenException("You can only view your own complaints")
        }
        if (!studentRepository.existsById(studentId)) throw NotFoundException("Student not found with id $studentId")
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getByStaff(staffId: Long, email: String): List<ComplaintResponse> {
        val user = currentUser(email)
        if (user.role == Role.STAFF) {
            val me = staffRepository.findByUserId(user.id!!) ?: throw NotFoundException("Staff profile not found")
            if (me.id != staffId) throw ForbiddenException("You can only view your own assigned complaints")
        }
        if (!staffRepository.existsById(staffId)) throw NotFoundException("Staff not found with id $staffId")
        return complaintRepository.findByAssignedToIdOrderByCreatedAtDesc(staffId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getUpdates(id: Long, email: String): List<ComplaintUpdateResponse> {
        val complaint = findComplaint(id)
        checkCanView(complaint, currentUser(email))
        return updateRepository.findByComplaintIdOrderByUpdatedAtAscIdAsc(id).map { it.toResponse() }
    }

    // ---------------- CREATE / UPDATE / DELETE ----------------

    fun create(req: ComplaintRequest, email: String): ComplaintResponse {
        val user = currentUser(email)
        val student = studentOf(user)

        val complaint = complaintRepository.save(
            Complaint(
                student = student,
                title = req.title.trim(),
                description = req.description.trim(),
                category = Category.valueOf(req.category),
                priority = Priority.valueOf(req.priority),
                status = ComplaintStatus.OPEN
            )
        )
        logUpdate(complaint, user, null, ComplaintStatus.OPEN, "Complaint submitted")
        return complaint.toResponse()
    }

    /** ADMIN can edit any complaint. STUDENT can edit own complaint only while it is OPEN. */
    fun update(id: Long, req: ComplaintRequest, email: String): ComplaintResponse {
        val user = currentUser(email)
        val complaint = findComplaint(id)
        checkOwnerOrAdminForEdit(complaint, user)

        complaint.title = req.title.trim()
        complaint.description = req.description.trim()
        complaint.category = Category.valueOf(req.category)
        complaint.priority = Priority.valueOf(req.priority)
        return complaintRepository.save(complaint).toResponse()
    }

    fun delete(id: Long, email: String) {
        val user = currentUser(email)
        val complaint = findComplaint(id)
        checkOwnerOrAdminForEdit(complaint, user)
        updateRepository.deleteByComplaintId(id)
        complaintRepository.delete(complaint)
    }

    // ---------------- WORKFLOW ----------------

    fun assign(id: Long, staffId: Long, email: String): ComplaintResponse {
        val admin = currentUser(email)
        val complaint = findComplaint(id)
        val staff = staffRepository.findById(staffId)
            .orElseThrow { NotFoundException("Staff not found with id $staffId") }

        if (complaint.status == ComplaintStatus.RESOLVED || complaint.status == ComplaintStatus.REJECTED) {
            throw BadRequestException("Cannot assign a complaint that is already ${complaint.status}")
        }

        val old = complaint.status
        complaint.assignedTo = staff
        complaint.status = ComplaintStatus.ASSIGNED
        complaintRepository.save(complaint)
        logUpdate(complaint, admin, old, ComplaintStatus.ASSIGNED, "Assigned to ${staff.user.name}")
        return complaint.toResponse()
    }

    fun changeStatus(id: Long, req: StatusUpdateRequest, email: String): ComplaintResponse {
        val user = currentUser(email)
        val complaint = findComplaint(id)
        val newStatus = ComplaintStatus.valueOf(req.status)
        val remarks = req.remarks?.trim()?.takeIf { it.isNotEmpty() }

        when (user.role) {
            Role.STAFF -> {
                if (complaint.assignedTo?.user?.id != user.id) {
                    throw ForbiddenException("This complaint is not assigned to you")
                }
                when (newStatus) {
                    ComplaintStatus.IN_PROGRESS ->
                        if (complaint.status != ComplaintStatus.ASSIGNED)
                            throw BadRequestException("Only an ASSIGNED complaint can be moved to IN_PROGRESS")
                    ComplaintStatus.RESOLVED -> {
                        if (complaint.status != ComplaintStatus.IN_PROGRESS)
                            throw BadRequestException("Only an IN_PROGRESS complaint can be RESOLVED")
                        if (remarks == null)
                            throw BadRequestException("Resolution remarks are required to resolve a complaint")
                    }
                    else -> throw BadRequestException("Staff can only set IN_PROGRESS or RESOLVED")
                }
            }
            Role.ADMIN -> {
                val needsStaff = newStatus == ComplaintStatus.ASSIGNED ||
                        newStatus == ComplaintStatus.IN_PROGRESS ||
                        newStatus == ComplaintStatus.RESOLVED
                if (needsStaff && complaint.assignedTo == null) {
                    throw BadRequestException("Assign a staff member before setting status to $newStatus")
                }
                if ((newStatus == ComplaintStatus.REJECTED || newStatus == ComplaintStatus.RESOLVED) && remarks == null) {
                    throw BadRequestException("Remarks are required for $newStatus")
                }
            }
            Role.STUDENT -> throw ForbiddenException("Students cannot change complaint status")
        }

        val old = complaint.status
        complaint.status = newStatus
        if (newStatus == ComplaintStatus.RESOLVED) {
            complaint.resolvedAt = LocalDateTime.now()
        } else {
            complaint.resolvedAt = null
        }
        if ((newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.REJECTED) && remarks != null) {
            complaint.resolutionRemarks = remarks
        }
        complaintRepository.save(complaint)
        logUpdate(complaint, user, old, newStatus, remarks)
        return complaint.toResponse()
    }

    // ---------------- helpers ----------------

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException("User not found")

    private fun studentOf(user: User): Student =
        studentRepository.findByUserId(user.id!!) ?: throw NotFoundException("Student profile not found")

    private fun findComplaint(id: Long): Complaint =
        complaintRepository.findById(id).orElseThrow { NotFoundException("Complaint not found with id $id") }

    private fun checkCanView(complaint: Complaint, user: User) {
        when (user.role) {
            Role.ADMIN -> {}
            Role.STUDENT ->
                if (complaint.student.user.id != user.id) throw ForbiddenException("You cannot access this complaint")
            Role.STAFF ->
                if (complaint.assignedTo?.user?.id != user.id) throw ForbiddenException("You cannot access this complaint")
        }
    }

    private fun checkOwnerOrAdminForEdit(complaint: Complaint, user: User) {
        when (user.role) {
            Role.ADMIN -> {}
            Role.STUDENT -> {
                if (complaint.student.user.id != user.id) throw ForbiddenException("You cannot modify this complaint")
                if (complaint.status != ComplaintStatus.OPEN) {
                    throw BadRequestException("Only OPEN complaints can be edited or deleted")
                }
            }
            Role.STAFF -> throw ForbiddenException("Staff cannot modify complaint details")
        }
    }

    private fun logUpdate(
        complaint: Complaint,
        by: User,
        old: ComplaintStatus?,
        new: ComplaintStatus?,
        remarks: String?
    ) {
        updateRepository.save(
            ComplaintUpdate(
                complaint = complaint,
                updatedBy = by,
                oldStatus = old,
                newStatus = new,
                remarks = remarks
            )
        )
    }
}
