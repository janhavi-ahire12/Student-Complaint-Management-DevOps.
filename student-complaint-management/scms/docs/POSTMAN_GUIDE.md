# Postman Testing Guide

Base URL: `http://localhost:8081`
All protected requests need the header:  `Authorization: Bearer <JWT_TOKEN>`
All requests with a body need:  `Content-Type: application/json`

## 0. One-time Postman setup (saves a lot of copy-pasting)

1. Create a Collection named **SCMS**.
2. Collection > Variables: add `baseUrl` = `http://localhost:8081` and `token` (leave empty).
3. Collection > Authorization: Type **Bearer Token**, value `{{token}}`.
4. In each **Login** request open the **Tests** tab and paste:
   ```js
   pm.collectionVariables.set("token", pm.response.json().token);
   ```
   Now every login stores the JWT automatically.

Sample accounts (created by the SQL script):

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | Admin@123 |
| STAFF | staff@example.com | Staff@123 |
| STAFF | staff2@example.com | Staff@123 |
| STUDENT | shravani.student@example.com | Student@123 |

Sample data IDs: student profile id = 1, staff profile ids = 1 and 2, complaint ids = 1 to 4.

---

## Correct order of testing

### 1. Register a new student
- **POST** `{{baseUrl}}/api/auth/register` (no token needed)
- Body:
```json
{
  "name": "Rahul Sharma",
  "email": "rahul.student@example.com",
  "password": "Rahul@123",
  "phone": "9876543210",
  "studentId": "CSE2024002",
  "course": "B.E. CSE (AI & ML)",
  "year": 2,
  "department": "Computer Science"
}
```
- Expected **201 Created**:
```json
{ "id": 2, "userId": 5, "name": "Rahul Sharma", "email": "rahul.student@example.com",
  "phone": "9876543210", "studentId": "CSE2024002", "course": "B.E. CSE (AI & ML)",
  "year": 2, "department": "Computer Science", "createdAt": "2026-09-30T10:15:00" }
```
- Register the same email again: **400** `{"status":400,"message":"Email is already registered"}`

### 2. Login as the new student
- **POST** `{{baseUrl}}/api/auth/login`
- Body:
```json
{ "email": "rahul.student@example.com", "password": "Rahul@123" }
```
- Expected **200 OK**:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "userId": 5, "name": "Rahul Sharma",
  "email": "rahul.student@example.com", "role": "STUDENT", "profileId": 2 }
```
- Wrong password: **401** `{"status":401,"message":"Invalid email or password"}`

### 3. Copy the JWT
If you added the Tests script, `{{token}}` is already set. Otherwise copy the `token` value into the collection variable.

### 4. Student profile
- **GET** `{{baseUrl}}/api/students/me` -> **200** with the student JSON above.
- **GET** `{{baseUrl}}/api/students/2` -> **200** (own profile).
- **GET** `{{baseUrl}}/api/students/1` -> **403** (another student's profile).
- **PUT** `{{baseUrl}}/api/students/2`
```json
{ "name": "Rahul Sharma", "phone": "9876500000", "studentId": "CSE2024002",
  "course": "B.E. CSE (AI & ML)", "year": 2, "department": "Computer Science" }
```
-> **200** with updated profile.
- **GET** `{{baseUrl}}/api/dashboard/stats` -> **200** `{"totalComplaints":0,"open":0,"assigned":0,"inProgress":0,"resolved":0,"rejected":0}`

### 5. Create a complaint (as student)
- **POST** `{{baseUrl}}/api/complaints`
```json
{
  "title": "Wi-Fi not working in library",
  "description": "The Wi-Fi in the second floor of the library has been down since Monday.",
  "category": "MAINTENANCE",
  "priority": "HIGH"
}
```
- Expected **201 Created**:
```json
{ "id": 5, "title": "Wi-Fi not working in library", "description": "The Wi-Fi in the second floor ...",
  "category": "MAINTENANCE", "priority": "HIGH", "status": "OPEN", "resolutionRemarks": null,
  "createdAt": "2026-09-30T10:20:00", "resolvedAt": null, "studentId": 2,
  "studentName": "Rahul Sharma", "studentCode": "CSE2024002",
  "assignedToId": null, "assignedToName": null }
```
- Valid categories: `ACADEMIC, HOSTEL, ELECTRICAL, CLEANING, MAINTENANCE, OTHER`. Priorities: `LOW, MEDIUM, HIGH`.

### 6. View complaints (as student)
- **GET** `{{baseUrl}}/api/complaints` -> **200** list of your own complaints only.
- **GET** `{{baseUrl}}/api/complaints/5` -> **200** one complaint.
- **GET** `{{baseUrl}}/api/complaints/1` -> **403** (belongs to Shravani).
- **GET** `{{baseUrl}}/api/complaints/student/2` -> **200** (your profile id).
- **PUT** `{{baseUrl}}/api/complaints/5` (allowed only while status is OPEN)
```json
{ "title": "Wi-Fi not working in library (2nd floor)", "description": "Wi-Fi down since Monday, cannot download study material.",
  "category": "MAINTENANCE", "priority": "HIGH" }
```
-> **200** with updated complaint.

### 7. Login as ADMIN
- **POST** `{{baseUrl}}/api/auth/login`
```json
{ "email": "admin@example.com", "password": "Admin@123" }
```
-> **200** with `"role": "ADMIN"`, `"profileId": null`.
- **GET** `{{baseUrl}}/api/students` -> **200** list of all students.
- **GET** `{{baseUrl}}/api/staff` -> **200** list of staff.
- **GET** `{{baseUrl}}/api/complaints` -> **200** all complaints.
- **GET** `{{baseUrl}}/api/dashboard/stats` -> **200** `{"totalStudents":2,"totalStaff":2,"totalComplaints":5,"open":2,...}`

### 8. Create staff (as ADMIN)
- **POST** `{{baseUrl}}/api/staff`
```json
{
  "name": "Anil Deshmukh",
  "email": "anil.staff@example.com",
  "password": "Anil@123",
  "phone": "9123456780",
  "staffId": "STF003",
  "department": "IT Services",
  "designation": "Network Engineer"
}
```
-> **201 Created**:
```json
{ "id": 3, "userId": 6, "name": "Anil Deshmukh", "email": "anil.staff@example.com", "phone": "9123456780",
  "staffId": "STF003", "department": "IT Services", "designation": "Network Engineer",
  "createdAt": "2026-09-30T10:30:00" }
```
- **PUT** `{{baseUrl}}/api/staff/3`
```json
{ "name": "Anil Deshmukh", "phone": "9123456780", "staffId": "STF003", "department": "IT Services", "designation": "Senior Network Engineer" }
```
-> **200**.

### 9. Assign the complaint (as ADMIN)
- **PUT** `{{baseUrl}}/api/complaints/5/assign/3` (no body)
- Expected **200**: complaint JSON with `"status": "ASSIGNED"`, `"assignedToId": 3`, `"assignedToName": "Anil Deshmukh"`.
- Assigning a RESOLVED or REJECTED complaint -> **400**.
- Unknown staff id (e.g. 99) -> **404** `{"status":404,"message":"Staff not found with id 99"}`

### 10. Staff works on it
Login: **POST** `{{baseUrl}}/api/auth/login`
```json
{ "email": "anil.staff@example.com", "password": "Anil@123" }
```
(response has `"role": "STAFF"`, `"profileId": 3`)

- **GET** `{{baseUrl}}/api/complaints` -> **200** only complaints assigned to this staff.
- **GET** `{{baseUrl}}/api/complaints/staff/3` -> **200**.
- **GET** `{{baseUrl}}/api/complaints/1` -> **403** (not assigned to you).
- **PUT** `{{baseUrl}}/api/complaints/5/status` (ASSIGNED -> IN_PROGRESS)
```json
{ "status": "IN_PROGRESS", "remarks": "Checking the router on the second floor." }
```
-> **200** with `"status": "IN_PROGRESS"`.

### 11. Resolve the complaint (as STAFF)
- **PUT** `{{baseUrl}}/api/complaints/5/status`
```json
{ "status": "RESOLVED", "remarks": "Router replaced. Wi-Fi is working again." }
```
- Expected **200**: `"status": "RESOLVED"`, `"resolutionRemarks": "Router replaced. Wi-Fi is working again."`, `"resolvedAt"` filled.
- RESOLVED without remarks -> **400** `Resolution remarks are required to resolve a complaint`.
- Staff sending `"status": "REJECTED"` -> **400** `Staff can only set IN_PROGRESS or RESOLVED`.

### 12. View complaint history
- **GET** `{{baseUrl}}/api/complaints/5/updates` (works for the student who owns it, the assigned staff, or admin)
- Expected **200**:
```json
[
  { "id": 11, "complaintId": 5, "updatedByName": "Rahul Sharma", "updatedByRole": "STUDENT",
    "oldStatus": null, "newStatus": "OPEN", "remarks": "Complaint submitted", "updatedAt": "..." },
  { "id": 12, "complaintId": 5, "updatedByName": "System Admin", "updatedByRole": "ADMIN",
    "oldStatus": "OPEN", "newStatus": "ASSIGNED", "remarks": "Assigned to Anil Deshmukh", "updatedAt": "..." },
  { "id": 13, "complaintId": 5, "updatedByName": "Anil Deshmukh", "updatedByRole": "STAFF",
    "oldStatus": "ASSIGNED", "newStatus": "IN_PROGRESS", "remarks": "Checking the router on the second floor.", "updatedAt": "..." },
  { "id": 14, "complaintId": 5, "updatedByName": "Anil Deshmukh", "updatedByRole": "STAFF",
    "oldStatus": "IN_PROGRESS", "newStatus": "RESOLVED", "remarks": "Router replaced. Wi-Fi is working again.", "updatedAt": "..." }
]
```

### 13. Admin extras
- Reject a complaint: **PUT** `{{baseUrl}}/api/complaints/1/status`
```json
{ "status": "REJECTED", "remarks": "Duplicate of an existing complaint." }
```
-> **200** with `"status": "REJECTED"`.
- **GET** `{{baseUrl}}/api/complaints/staff/1` -> complaints assigned to staff 1.
- **DELETE** `{{baseUrl}}/api/complaints/1` -> **200** `{"message":"Complaint deleted successfully"}`
- **DELETE** `{{baseUrl}}/api/students/2` -> **400** (student has complaints) or **200** if none.

---

## Negative tests (show these to your teacher)

| Test | Request | Expected |
|---|---|---|
| No token | GET `/api/complaints` without Authorization | **401** |
| Wrong role | Student token, GET `/api/students` | **403** |
| Student assigns | Student token, PUT `/api/complaints/5/assign/1` | **403** |
| Invalid priority | POST `/api/complaints` with `"priority": "URGENT"` | **400** `Priority must be LOW, MEDIUM or HIGH` |
| Invalid category | POST `/api/complaints` with `"category": "FOOD"` | **400** |
| Empty title | POST `/api/complaints` with `"title": ""` | **400** `Complaint title is required` |
| Invalid email | POST `/api/auth/register` with `"email": "abc"` | **400** `Enter a valid email address` |
| Empty password | POST `/api/auth/login` with `"password": ""` | **400** `Password is required` |
| Not found | GET `/api/complaints/9999` (admin token) | **404** `Complaint not found with id 9999` |

Error JSON always looks like: `{ "status": 400, "message": "..." }`
