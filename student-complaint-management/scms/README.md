# Student Complaint Management System

Kotlin + Spring Boot + JWT + MySQL 8 backend, React + Vite frontend.

```
scms/
├── README.md
├── database/
│   └── student_complaint_management.sql       <- run this in MySQL first
├── docs/
│   ├── DOCUMENTATION.md                       <- abstract, SRS, viva, teacher script
│   └── POSTMAN_GUIDE.md
├── student-complaint-backend/                 <- open this folder in IntelliJ IDEA
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/main/
│       ├── resources/application.properties   <- put your MySQL password here
│       └── kotlin/com/studentcomplaint/
│           ├── StudentComplaintApplication.kt
│           ├── entity/       Enums, User, Student, Staff, Complaint, ComplaintUpdate
│           ├── repository/   User, Student, Staff, Complaint, ComplaintUpdate repositories
│           ├── service/      Auth, Student, Staff, Complaint, Dashboard services
│           ├── controller/   Auth, Student, Staff, Complaint, Dashboard controllers
│           ├── security/     JwtUtil, JwtAuthFilter, CustomUserDetailsService
│           ├── config/       SecurityConfig (security rules, BCrypt, CORS)
│           ├── dto/          request/response classes + mappers
│           └── exception/    custom exceptions + GlobalExceptionHandler
└── student-complaint-frontend/                <- open this folder in VS Code
    ├── package.json, vite.config.js, index.html
    └── src/
        ├── main.jsx, App.jsx, index.css
        ├── components/  Navbar, ProtectedRoute, StatusBadge, StatCard, ComplaintView
        ├── pages/       Login, Register, StudentDashboard, StudentProfile, SubmitComplaint,
        │                MyComplaints, ComplaintDetails, AdminDashboard, AdminComplaints,
        │                AdminStudents, StaffDashboard, StaffComplaintDetails
        └── services/    api.js (Axios + JWT), auth.js, utils.js
```

Sample logins (from the SQL script):

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | Admin@123 |
| STAFF | staff@example.com | Staff@123 |
| STUDENT | shravani.student@example.com | Student@123 |

Ports: backend http://localhost:8081, frontend http://localhost:5173
