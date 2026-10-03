-- =====================================================================
-- Student Complaint Management System - MySQL 8 script
-- Run this whole file once in MySQL Workbench (or mysql command line).
-- WARNING: it DROPS and re-creates the tables, so old data is deleted.
--
-- Sample login accounts (passwords are stored as BCrypt hashes):
--   admin@example.com            / Admin@123     (ADMIN)
--   staff@example.com            / Staff@123     (STAFF)
--   staff2@example.com           / Staff@123     (STAFF)
--   shravani.student@example.com / Student@123   (STUDENT)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS student_complaint_management;
USE student_complaint_management;

DROP TABLE IF EXISTS complaint_updates;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

-- ---------- 1. users ----------
CREATE TABLE users (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    phone      VARCHAR(15),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ---------- 2. students ----------
CREATE TABLE students (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL UNIQUE,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    course     VARCHAR(100) NOT NULL,
    year       INT NOT NULL,
    department VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ---------- 3. staff ----------
CREATE TABLE staff (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL UNIQUE,
    staff_id    VARCHAR(30) NOT NULL UNIQUE,
    department  VARCHAR(100),
    designation VARCHAR(100),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ---------- 4. complaints ----------
CREATE TABLE complaints (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id         BIGINT NOT NULL,
    assigned_to        BIGINT NULL,
    title              VARCHAR(150) NOT NULL,
    description        TEXT NOT NULL,
    category           VARCHAR(50),
    priority           VARCHAR(20) NOT NULL,
    status             VARCHAR(20) NOT NULL,
    resolution_remarks TEXT,
    created_at         DATETIME NOT NULL,
    resolved_at        DATETIME NULL,
    CONSTRAINT fk_complaints_student FOREIGN KEY (student_id)  REFERENCES students(id),
    CONSTRAINT fk_complaints_staff   FOREIGN KEY (assigned_to) REFERENCES staff(id)
);

-- ---------- 5. complaint_updates ----------
CREATE TABLE complaint_updates (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    updated_by   BIGINT NOT NULL,
    old_status   VARCHAR(20),
    new_status   VARCHAR(20),
    remarks      TEXT,
    updated_at   DATETIME NOT NULL,
    CONSTRAINT fk_updates_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
    CONSTRAINT fk_updates_user      FOREIGN KEY (updated_by)   REFERENCES users(id)
);

-- =====================================================================
-- SAMPLE DATA
-- =====================================================================

-- users: 1 = admin, 2 = staff, 3 = staff2, 4 = Shravani (student)
INSERT INTO users (id, name, email, password, role, phone, created_at) VALUES
(1, 'System Admin',   'admin@example.com',            '$2b$10$h/FYL2U7q9lVHwTYgkKzm.Apla9RjC87zA3xMRblgSsWRJ00jGalm', 'ADMIN',   '9000000001', NOW()),
(2, 'Ramesh Patil',   'staff@example.com',            '$2b$10$nRDPB8EMGKKDGXGLFWU6O.ssOwxjD3hDv9GHb.BmJdaayaA39x.n2', 'STAFF',   '9000000002', NOW()),
(3, 'Sunita Kulkarni','staff2@example.com',           '$2b$10$nRDPB8EMGKKDGXGLFWU6O.ssOwxjD3hDv9GHb.BmJdaayaA39x.n2', 'STAFF',   '9000000003', NOW()),
(4, 'Shravani',       'shravani.student@example.com', '$2b$10$.7Susk7g7QsNEsLt7bCJl.o9N/Xh9ItmM0LSTQ8oMsHFMXypqt9n6',  'STUDENT', '9000000004', NOW());

INSERT INTO students (id, user_id, student_id, course, year, department, created_at) VALUES
(1, 4, 'CSE2024001', 'B.E. CSE (AI & ML)', 2, 'Computer Science', NOW());

INSERT INTO staff (id, user_id, staff_id, department, designation, created_at) VALUES
(1, 2, 'STF001', 'Electrical',  'Electrician',        NOW()),
(2, 3, 'STF002', 'Maintenance', 'Maintenance Officer', NOW());

INSERT INTO complaints (id, student_id, assigned_to, title, description, category, priority, status, resolution_remarks, created_at, resolved_at) VALUES
(1, 1, NULL, 'Projector not working in Room 204',
    'The projector in Room 204 does not turn on. Our lab session was disturbed.',
    'ELECTRICAL', 'HIGH', 'OPEN', NULL, NOW() - INTERVAL 1 DAY, NULL),
(2, 1, 1, 'Fan making loud noise in hostel room',
    'The ceiling fan in hostel room B-12 makes a loud noise at night.',
    'HOSTEL', 'MEDIUM', 'ASSIGNED', NULL, NOW() - INTERVAL 3 DAY, NULL),
(3, 1, 2, 'Water leakage near library entrance',
    'Water is leaking from the roof near the library entrance when it rains.',
    'MAINTENANCE', 'HIGH', 'IN_PROGRESS', NULL, NOW() - INTERVAL 5 DAY, NULL),
(4, 1, 1, 'Broken tube light in classroom 101',
    'Two tube lights in classroom 101 are not working.',
    'ELECTRICAL', 'LOW', 'RESOLVED', 'Tube lights replaced with new ones.', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY);

INSERT INTO complaint_updates (complaint_id, updated_by, old_status, new_status, remarks, updated_at) VALUES
(1, 4, NULL,          'OPEN',        'Complaint submitted', NOW() - INTERVAL 1 DAY),
(2, 4, NULL,          'OPEN',        'Complaint submitted', NOW() - INTERVAL 3 DAY),
(2, 1, 'OPEN',        'ASSIGNED',    'Assigned to Ramesh Patil', NOW() - INTERVAL 2 DAY),
(3, 4, NULL,          'OPEN',        'Complaint submitted', NOW() - INTERVAL 5 DAY),
(3, 1, 'OPEN',        'ASSIGNED',    'Assigned to Sunita Kulkarni', NOW() - INTERVAL 4 DAY),
(3, 3, 'ASSIGNED',    'IN_PROGRESS', 'Inspection done, repair work started', NOW() - INTERVAL 3 DAY),
(4, 4, NULL,          'OPEN',        'Complaint submitted', NOW() - INTERVAL 7 DAY),
(4, 1, 'OPEN',        'ASSIGNED',    'Assigned to Ramesh Patil', NOW() - INTERVAL 7 DAY),
(4, 2, 'ASSIGNED',    'IN_PROGRESS', 'Going to the classroom today', NOW() - INTERVAL 7 DAY),
(4, 2, 'IN_PROGRESS', 'RESOLVED',    'Tube lights replaced with new ones.', NOW() - INTERVAL 6 DAY);

SELECT id, name, email, role FROM users;
