# Student Complaint Management System - Project Documentation

## 1. Project Abstract
The Student Complaint Management System (SCMS) is a web application that lets college students report problems with campus facilities and services (hostel, electrical, cleaning, maintenance, academic and others) and track their progress until they are resolved. Students register, log in and submit complaints. An administrator reviews all complaints and assigns each one to a staff member. The staff member updates the status (IN_PROGRESS, RESOLVED) and adds resolution remarks. The system uses a Kotlin + Spring Boot REST backend secured with JWT and BCrypt, a MySQL 8 database, and a React (Vite) frontend.

## 2. Problem Statement
In many colleges complaints are given on paper or verbally. They get lost, nobody knows who is responsible, and students cannot find out whether anything was done. There is no record for the administration to review. SCMS replaces this with one online system where every complaint is recorded, assigned to a responsible person, tracked through clear stages and stored with its full history.

## 3. Objectives
- Provide a simple online way for students to submit complaints.
- Let the admin assign each complaint to a staff member.
- Let staff update the status and record resolution remarks.
- Let students track status and read the resolution.
- Keep a history of every status change (audit trail).
- Protect data with login (JWT), encrypted passwords (BCrypt) and role-based access.

## 4. Scope
**In scope:** registration/login, three roles (STUDENT, ADMIN, STAFF), complaint submission and tracking, assignment, status workflow, remarks, dashboards with counts, REST APIs, MySQL storage.
**Out of scope:** e-mail/SMS notifications, file attachments, payments, cloud deployment, mobile app, password reset.

## 5. Functional Requirements
1. A student can register and log in.
2. A student can view a dashboard with complaint counts.
3. A student can submit a complaint (title, description, category, priority).
4. A student can view own complaints, complaint details, status and resolution remarks.
5. An admin can log in, view dashboard counts, all students and all complaints.
6. An admin can assign a complaint to a staff member and change complaint status.
7. A staff member can log in and view complaints assigned to them.
8. A staff member can change status (ASSIGNED -> IN_PROGRESS -> RESOLVED) and add resolution remarks.
9. Every status change is stored in complaint history.
10. Users can only see the data allowed for their role.

## 6. Non-Functional Requirements
- **Security:** BCrypt password hashing, JWT for every protected API, role-based authorization.
- **Usability:** simple, responsive UI with clear status badges.
- **Performance:** responds in under a second for college-scale data.
- **Reliability:** input validation and clear JSON error messages.
- **Maintainability:** layered code (controller, service, repository, entity).
- **Portability:** runs on any Windows laptop with JDK 17, MySQL 8 and Node.js.

## 7. Hardware Requirements
- Processor: Intel i3 / Ryzen 3 or better
- RAM: 8 GB (4 GB minimum)
- Storage: 5 GB free
- Windows 10/11 laptop with a browser

## 8. Software Requirements
- JDK 17, Kotlin 1.9 (via Gradle), Spring Boot 3.2
- Gradle (IntelliJ IDEA handles it) 
- MySQL 8 + MySQL Workbench
- Node.js 18 or newer, npm
- React 18, Vite 5, Axios, React Router
- IntelliJ IDEA (backend), VS Code (frontend), Postman (API testing), Chrome/Edge

## 9. System Architecture
```
 Browser (React + Vite, port 5173)
        |  Axios HTTP + JSON  (Authorization: Bearer <JWT>)
        v
 Spring Boot REST API (port 8081)
   JwtAuthFilter -> Spring Security (roles)
   Controller -> Service -> Repository (Spring Data JPA / Hibernate)
        |  JDBC
        v
 MySQL 8  (student_complaint_management)
```
Layers: **Controller** receives HTTP requests, **Service** holds business rules (workflow, permissions), **Repository** talks to the database, **Entity** maps to tables, **DTO** shapes JSON in/out, **Security** handles JWT and roles.

## 10. Database Design
| Table | Important columns |
|---|---|
| users | id, name, email (unique), password (BCrypt), role, phone, created_at |
| students | id, user_id (unique FK), student_id (unique), course, year, department, created_at |
| staff | id, user_id (unique FK), staff_id (unique), department, designation, created_at |
| complaints | id, student_id (FK), assigned_to (FK, null), title, description, category, priority, status, resolution_remarks, created_at, resolved_at |
| complaint_updates | id, complaint_id (FK), updated_by (FK users), old_status, new_status, remarks, updated_at |

## 11. ER Diagram Description
- USERS 1 --- 1 STUDENTS (a student profile belongs to one user account)
- USERS 1 --- 1 STAFF (a staff profile belongs to one user account)
- STUDENTS 1 --- N COMPLAINTS (a student can submit many complaints)
- STAFF 1 --- N COMPLAINTS (a staff member can be assigned many complaints)
- COMPLAINTS 1 --- N COMPLAINT_UPDATES (each complaint has many history rows)
- USERS 1 --- N COMPLAINT_UPDATES (a user makes many updates)

```
USERS --1:1--> STUDENTS --1:N--> COMPLAINTS <--N:1-- STAFF <--1:1-- USERS
                                     |
                                    1:N
                                     v
                             COMPLAINT_UPDATES --N:1--> USERS
```
Draw this in draw.io: rectangles for tables, crow's-foot lines for the "N" side.

## 12. API Description
| Area | Endpoints | Access |
|---|---|---|
| Auth | POST /api/auth/register, POST /api/auth/login | Public |
| Students | GET /api/students, GET/PUT /api/students/{id}, GET /api/students/me, POST, DELETE | List/POST/DELETE: ADMIN. {id}/PUT: ADMIN or the student themself. me: STUDENT |
| Staff | GET /api/staff, GET/PUT /api/staff/{id}, GET /api/staff/me, POST, DELETE | List/POST/DELETE: ADMIN. {id}/PUT: ADMIN or that staff. me: STAFF |
| Complaints | GET /api/complaints | ADMIN all, STUDENT own, STAFF assigned |
| | GET /api/complaints/{id} | ADMIN any, STUDENT own, STAFF assigned |
| | POST /api/complaints | STUDENT |
| | PUT, DELETE /api/complaints/{id} | ADMIN, or STUDENT (own, only while OPEN) |
| | GET /api/complaints/student/{studentId} | ADMIN, or that STUDENT |
| | GET /api/complaints/staff/{staffId} | ADMIN, or that STAFF |
| | PUT /api/complaints/{id}/assign/{staffId} | ADMIN |
| | PUT /api/complaints/{id}/status | ADMIN, or assigned STAFF |
| | GET /api/complaints/{id}/updates | Same as view access |
| Dashboard | GET /api/dashboard/stats | Any logged-in user (numbers depend on role) |

HTTP codes: 200 OK, 201 Created, 400 Bad Request (validation/business rule), 401 Unauthorized (no/invalid token), 403 Forbidden (wrong role/not your data), 404 Not Found.

**Status rules:** OPEN -> (admin assigns) ASSIGNED -> (staff) IN_PROGRESS -> (staff, remarks required) RESOLVED. Admin can also set REJECTED (remarks required) or correct any status.

## 13. Module Description
- **Authentication module:** register (student), login, JWT generation, BCrypt hashing, JwtAuthFilter.
- **Student module:** student profile, own profile update, admin student list and delete.
- **Staff module:** staff create/list/update/delete by admin.
- **Complaint module:** submit, list, view, edit, delete, assign, change status.
- **History module:** stores an entry in complaint_updates for every submit/assign/status change.
- **Dashboard module:** counts per role.
- **Frontend module:** React pages, protected routes by role, Axios service with JWT interceptor.

## 14. Testing
- **API testing (Postman):** see POSTMAN_GUIDE.md - positive flow (register -> login -> complaint -> assign -> in progress -> resolved -> history) and negative tests (401, 403, 400, 404).
- **Validation testing:** empty title, invalid email, invalid priority/category.
- **Security testing:** access without token, access with wrong role, access to another student's complaint.
- **UI testing:** log in with each of the three roles and complete the full workflow in the browser.
- **Database check:** verify rows in MySQL Workbench (SELECT * FROM complaints; SELECT * FROM complaint_updates;).

## 15. Future Scope
- E-mail/SMS notifications when status changes
- File/photo attachments
- Complaint feedback and rating after resolution
- Reports and charts for the admin
- Password reset and profile pictures
- Deployment on a college server
- Automatic escalation of overdue complaints

## 16. Conclusion
SCMS gives the college a transparent and organised way to handle student complaints. The workflow, role-based security and history make every complaint traceable from submission to resolution. The layered Kotlin/Spring Boot backend with a React frontend is easy to understand, test and extend.

---

# 10 Viva Questions and Short Answers

1. **What is JWT and why did you use it?** JSON Web Token is a signed token given after login. The client sends it with each request, so the server does not need sessions and can identify the user and role.
2. **How are passwords stored?** As BCrypt hashes using BCryptPasswordEncoder. BCrypt adds a random salt and is slow on purpose, so passwords cannot be read from the database.
3. **What is Spring Data JPA / Hibernate?** JPA maps Kotlin classes to tables. Hibernate is the implementation. Repositories like `JpaRepository` give ready-made save, find and delete methods without writing SQL.
4. **Explain the layers of your backend.** Controller (HTTP), Service (business logic), Repository (database), Entity (table mapping), DTO (JSON shape), Security (JWT/roles).
5. **How does role-based access work?** The JWT identifies the user, the filter loads the role (ROLE_ADMIN etc.), and `@PreAuthorize` checks the role. Services also check ownership, e.g. a student cannot open another student's complaint.
6. **What is CORS and how did you configure it?** Browsers block calls between different origins. The React app runs on port 5173 and the API on 8081, so the backend allows origin `http://localhost:5173`.
7. **What is the complaint workflow?** OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED, with optional REJECTED.
8. **Why do you have a complaint_updates table?** To keep a history of who changed the status, when, and with which remarks.
9. **What is the difference between authentication and authorization?** Authentication checks who you are (login). Authorization checks what you are allowed to do (role and ownership).
10. **What relationships exist in the database?** users-students 1:1, users-staff 1:1, students-complaints 1:N, staff-complaints 1:N, complaints-complaint_updates 1:N, users-complaint_updates 1:N.

---

# Explaining the Project to Your Teacher (2-3 minutes)

"Good morning, ma'am/sir. My project is the **Student Complaint Management System**. In our college, complaints about hostels, electricity or cleaning are given verbally or on paper and are often forgotten. My system makes this online and trackable.

There are three roles. A **student** registers, logs in, submits a complaint and can see its status and the final remarks. An **admin** sees all students and complaints, assigns each complaint to a staff member and can change status. A **staff member** sees only complaints assigned to them, marks them IN_PROGRESS and finally RESOLVED with remarks.

Technically, the backend is built in **Kotlin with Spring Boot**. It exposes REST APIs, uses **Spring Data JPA** with **MySQL**, and is secured with **Spring Security and JWT**. Passwords are stored using **BCrypt**. The frontend is built with **React and Vite**, using Axios for API calls and React Router for pages.

Let me demonstrate: I log in as a student and submit a complaint. Now I log in as admin, assign it to a staff member. As staff, I open it, start work, and resolve it with remarks. Back as student, I can see the RESOLVED status, the remarks, and the full history. I have also tested all APIs in Postman, including security cases like using no token, which gives 401, and a student calling an admin API, which gives 403.

Thank you. I am ready for questions."
