/*
 * University ERP - Complete Seed Data Script
 * Generates schema and data for 100 users (1 Admin, 15 Instructors, 84 Students)
 * Includes real IIITD courses and specific B.Tech programs.
 * UPDATED: Password hash replaced.
 */

-- --------------------------------------------------------
-- 1. SETUP AUTH DATABASE
-- --------------------------------------------------------
DROP DATABASE IF EXISTS auth_db;
CREATE DATABASE auth_db;
USE auth_db;

CREATE TABLE users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    role ENUM('Student', 'Instructor', 'Admin') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    last_login TIMESTAMP NULL,
    failed_attempts INT DEFAULT 0,
    lockout_time TIMESTAMP NULL DEFAULT NULL
);

-- --------------------------------------------------------
-- 2. SETUP ERP DATABASE
-- --------------------------------------------------------
DROP DATABASE IF EXISTS erp_db;
CREATE DATABASE erp_db;
USE erp_db;

-- Settings Table
CREATE TABLE settings (
  setting_key varchar(50) NOT NULL,
  setting_value varchar(100) DEFAULT NULL,
  PRIMARY KEY (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Admins Table
CREATE TABLE admins (
  user_id int NOT NULL,
  full_name varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Instructors Table
CREATE TABLE instructors (
  user_id int NOT NULL,
  department varchar(100) DEFAULT NULL,
  full_name varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Students Table
CREATE TABLE students (
  user_id int NOT NULL,
  roll_no varchar(20) NOT NULL,
  full_name varchar(100) DEFAULT NULL,
  program varchar(100) DEFAULT NULL,
  year int DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY roll_no (roll_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Courses Table
CREATE TABLE courses (
  code varchar(20) NOT NULL,
  title varchar(100) NOT NULL,
  credits int NOT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sections Table
CREATE TABLE sections (
  section_id int NOT NULL AUTO_INCREMENT,
  course_id varchar(20) NOT NULL,
  instructor_id int DEFAULT NULL,
  day_time varchar(50) DEFAULT NULL,
  room varchar(30) DEFAULT NULL,
  capacity int NOT NULL,
  semester varchar(20) DEFAULT NULL,
  year int DEFAULT NULL,
  reg_deadline DATE DEFAULT NULL,
  drop_deadline DATE DEFAULT NULL,
  PRIMARY KEY (section_id),
  KEY course_id (course_id),
  KEY instructor_id (instructor_id),
  CONSTRAINT sections_ibfk_1 FOREIGN KEY (course_id) REFERENCES courses (code) ON DELETE CASCADE,
  CONSTRAINT sections_ibfk_2 FOREIGN KEY (instructor_id) REFERENCES instructors (user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Enrollments Table
CREATE TABLE enrollments (
  enrollment_id int NOT NULL AUTO_INCREMENT,
  student_id int NOT NULL,
  section_id int NOT NULL,
  status varchar(20) DEFAULT 'enrolled',
  PRIMARY KEY (enrollment_id),
  UNIQUE KEY unique_enrollment (student_id,section_id),
  KEY section_id (section_id),
  CONSTRAINT enrollments_ibfk_1 FOREIGN KEY (student_id) REFERENCES students (user_id) ON DELETE CASCADE,
  CONSTRAINT enrollments_ibfk_2 FOREIGN KEY (section_id) REFERENCES sections (section_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Grades Table
CREATE TABLE grades (
  grade_id int NOT NULL AUTO_INCREMENT,
  enrollment_id int NOT NULL,
  component varchar(50) NOT NULL,
  score double DEFAULT NULL,
  final_grade varchar(2) DEFAULT NULL,
  PRIMARY KEY (grade_id),
  UNIQUE KEY unique_grade (enrollment_id,component),
  CONSTRAINT grades_ibfk_1 FOREIGN KEY (enrollment_id) REFERENCES enrollments (enrollment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications Table
CREATE TABLE notifications (
  notification_id int NOT NULL AUTO_INCREMENT,
  user_id int DEFAULT NULL,
  role_target varchar(20) DEFAULT NULL,
  message text NOT NULL,
  is_read tinyint(1) DEFAULT '0',
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (notification_id),
  KEY user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- --------------------------------------------------------
-- 3. SEED DATA GENERATION
-- --------------------------------------------------------

-- ========================================================
-- A. AUTH USERS (Total 100: 1 Admin, 15 Inst, 84 Students)
--    Hash updated as requested.
-- ========================================================
USE auth_db;

-- 1 Admin (User ID 1)
INSERT INTO users_auth (user_id, username, role, password_hash) VALUES
(1, 'admin1', 'Admin', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK');

-- 15 Instructors (User IDs 2-16)
INSERT INTO users_auth (user_id, username, role, password_hash) VALUES
(2, 'inst1', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(3, 'inst2', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(4, 'inst3', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(5, 'inst4', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(6, 'inst5', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(7, 'inst6', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(8, 'inst7', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(9, 'inst8', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(10, 'inst9', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(11, 'inst10', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(12, 'inst11', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(13, 'inst12', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(14, 'inst13', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(15, 'inst14', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(16, 'inst15', 'Instructor', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK');

-- 84 Students (User IDs 17-100)
INSERT INTO users_auth (user_id, username, role, password_hash) VALUES
(17, 'stu1', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(18, 'stu2', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(19, 'stu3', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(20, 'stu4', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(21, 'stu5', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(22, 'stu6', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(23, 'stu7', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(24, 'stu8', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(25, 'stu9', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(26, 'stu10', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(27, 'stu11', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(28, 'stu12', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(29, 'stu13', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(30, 'stu14', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(31, 'stu15', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(32, 'stu16', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(33, 'stu17', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(34, 'stu18', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(35, 'stu19', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(36, 'stu20', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(37, 'stu21', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(38, 'stu22', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(39, 'stu23', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(40, 'stu24', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(41, 'stu25', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(42, 'stu26', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(43, 'stu27', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(44, 'stu28', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(45, 'stu29', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(46, 'stu30', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(47, 'stu31', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(48, 'stu32', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(49, 'stu33', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(50, 'stu34', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(51, 'stu35', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(52, 'stu36', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(53, 'stu37', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(54, 'stu38', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(55, 'stu39', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(56, 'stu40', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(57, 'stu41', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(58, 'stu42', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(59, 'stu43', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(60, 'stu44', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(61, 'stu45', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(62, 'stu46', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(63, 'stu47', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(64, 'stu48', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(65, 'stu49', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(66, 'stu50', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(67, 'stu51', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(68, 'stu52', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(69, 'stu53', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(70, 'stu54', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(71, 'stu55', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(72, 'stu56', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(73, 'stu57', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(74, 'stu58', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(75, 'stu59', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(76, 'stu60', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(77, 'stu61', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(78, 'stu62', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(79, 'stu63', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(80, 'stu64', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(81, 'stu65', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(82, 'stu66', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(83, 'stu67', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(84, 'stu68', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(85, 'stu69', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(86, 'stu70', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(87, 'stu71', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(88, 'stu72', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(89, 'stu73', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(90, 'stu74', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(91, 'stu75', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(92, 'stu76', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(93, 'stu77', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(94, 'stu78', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(95, 'stu79', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(96, 'stu80', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(97, 'stu81', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(98, 'stu82', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(99, 'stu83', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK'),
(100, 'stu84', 'Student', '$2a$10$gPXGQCq1eWuqjsXbEJqbu.dnNZ.EimkrRCjnZGb8EdbwSIuk4UCXK');

-- ========================================================
-- B. ERP PROFILES
-- ========================================================
USE erp_db;

-- Settings
INSERT INTO settings (setting_key, setting_value) VALUES
('maintenance_on', 'false'),
('current_semester', 'Monsoon'),
('current_year', '2025'),
('drop_deadline', '2025-11-15'),
('registration_deadline', '2025-09-15');

-- Admins Profile
INSERT INTO admins (user_id, full_name) VALUES
(1, 'System Administrator');

-- Instructors Profiles
INSERT INTO instructors (user_id, department, full_name) VALUES
(2, 'Computer Science', 'Dr. Emily Carter'),
(3, 'Electronics', 'Dr. Sarah Connor'),
(4, 'Maths', 'Dr. John Doe'),
(5, 'Admin', 'Admin Two'),
(6, 'Computer Science', 'Instructor 2'),
(7, 'Mathematics', 'Instructor 3'),
(8, 'Electronics', 'Instructor 4'),
(9, 'Social Sciences', 'Instructor 5'),
(10, 'Computer Science', 'Instructor 6'),
(11, 'Computer Science', 'Instructor 7'),
(12, 'Mathematics', 'Instructor 8'),
(13, 'Electronics', 'Instructor 9'),
(14, 'Computational Biology', 'Instructor 10'),
(15, 'Computer Science', 'Instructor 11'),
(16, 'Design', 'Instructor 12');

-- Students Profiles (IDs 17-100)
-- Assigning programs in a cycle
INSERT INTO students (user_id, roll_no, full_name, program, year) VALUES 
(17, '2025-017', 'Student 1', 'B.Tech CSE', 1),
(18, '2025-018', 'Student 2', 'B.Tech ECE', 1),
(19, '2025-019', 'Student 3', 'B.Tech CSAM', 1),
(20, '2025-020', 'Student 4', 'B.Tech CSAI', 2),
(21, '2025-021', 'Student 5', 'B.Tech CSSS', 2),
(22, '2025-022', 'Student 6', 'B.Tech CSB', 2),
(23, '2025-023', 'Student 7', 'B.Tech CSEcon', 3),
(24, '2025-024', 'Student 8', 'B.Tech CSD', 3),
(25, '2025-025', 'Student 9', 'B.Tech EVE', 3),
(26, '2025-026', 'Student 10', 'B.Tech CSE', 4),
(27, '2025-027', 'Student 11', 'B.Tech ECE', 4),
(28, '2025-028', 'Student 12', 'B.Tech CSAM', 4),
(29, '2025-029', 'Student 13', 'B.Tech CSAI', 1),
(30, '2025-030', 'Student 14', 'B.Tech CSSS', 1),
(31, '2025-031', 'Student 15', 'B.Tech CSB', 1),
(32, '2025-032', 'Student 16', 'B.Tech CSEcon', 2),
(33, '2025-033', 'Student 17', 'B.Tech CSD', 2),
(34, '2025-034', 'Student 18', 'B.Tech EVE', 2),
(35, '2025-035', 'Student 19', 'B.Tech CSE', 3),
(36, '2025-036', 'Student 20', 'B.Tech ECE', 3),
(37, '2025-037', 'Student 21', 'B.Tech CSAM', 3),
(38, '2025-038', 'Student 22', 'B.Tech CSAI', 4),
(39, '2025-039', 'Student 23', 'B.Tech CSSS', 4),
(40, '2025-040', 'Student 24', 'B.Tech CSB', 4),
(41, '2025-041', 'Student 25', 'B.Tech CSEcon', 1),
(42, '2025-042', 'Student 26', 'B.Tech CSD', 1),
(43, '2025-043', 'Student 27', 'B.Tech EVE', 1),
(44, '2025-044', 'Student 28', 'B.Tech CSE', 2),
(45, '2025-045', 'Student 29', 'B.Tech ECE', 2),
(46, '2025-046', 'Student 30', 'B.Tech CSAM', 2),
(47, '2025-047', 'Student 31', 'B.Tech CSAI', 3),
(48, '2025-048', 'Student 32', 'B.Tech CSSS', 3),
(49, '2025-049', 'Student 33', 'B.Tech CSB', 3),
(50, '2025-050', 'Student 34', 'B.Tech CSEcon', 4),
(51, '2025-051', 'Student 35', 'B.Tech CSD', 4),
(52, '2025-052', 'Student 36', 'B.Tech EVE', 4),
(53, '2025-053', 'Student 37', 'B.Tech CSE', 1),
(54, '2025-054', 'Student 38', 'B.Tech ECE', 1),
(55, '2025-055', 'Student 39', 'B.Tech CSAM', 1),
(56, '2025-056', 'Student 40', 'B.Tech CSAI', 2),
(57, '2025-057', 'Student 41', 'B.Tech CSSS', 2),
(58, '2025-058', 'Student 42', 'B.Tech CSB', 2),
(59, '2025-059', 'Student 43', 'B.Tech CSEcon', 3),
(60, '2025-060', 'Student 44', 'B.Tech CSD', 3),
(61, '2025-061', 'Student 45', 'B.Tech EVE', 3),
(62, '2025-062', 'Student 46', 'B.Tech CSE', 4),
(63, '2025-063', 'Student 47', 'B.Tech ECE', 4),
(64, '2025-064', 'Student 48', 'B.Tech CSAM', 4),
(65, '2025-065', 'Student 49', 'B.Tech CSAI', 1),
(66, '2025-066', 'Student 50', 'B.Tech CSSS', 1),
(67, '2025-067', 'Student 51', 'B.Tech CSB', 1),
(68, '2025-068', 'Student 52', 'B.Tech CSEcon', 2),
(69, '2025-069', 'Student 53', 'B.Tech CSD', 2),
(70, '2025-070', 'Student 54', 'B.Tech EVE', 2),
(71, '2025-071', 'Student 55', 'B.Tech CSE', 3),
(72, '2025-072', 'Student 56', 'B.Tech ECE', 3),
(73, '2025-073', 'Student 57', 'B.Tech CSAM', 3),
(74, '2025-074', 'Student 58', 'B.Tech CSAI', 4),
(75, '2025-075', 'Student 59', 'B.Tech CSSS', 4),
(76, '2025-076', 'Student 60', 'B.Tech CSB', 4),
(77, '2025-077', 'Student 61', 'B.Tech CSEcon', 1),
(78, '2025-078', 'Student 62', 'B.Tech CSD', 1),
(79, '2025-079', 'Student 63', 'B.Tech EVE', 1),
(80, '2025-080', 'Student 64', 'B.Tech CSE', 2),
(81, '2025-081', 'Student 65', 'B.Tech ECE', 2),
(82, '2025-082', 'Student 66', 'B.Tech CSAM', 2),
(83, '2025-083', 'Student 67', 'B.Tech CSAI', 3),
(84, '2025-084', 'Student 68', 'B.Tech CSSS', 3),
(85, '2025-085', 'Student 69', 'B.Tech CSB', 3),
(86, '2025-086', 'Student 70', 'B.Tech CSEcon', 4),
(87, '2025-087', 'Student 71', 'B.Tech CSD', 4),
(88, '2025-088', 'Student 72', 'B.Tech EVE', 4),
(89, '2025-089', 'Student 73', 'B.Tech CSE', 1),
(90, '2025-090', 'Student 74', 'B.Tech ECE', 1),
(91, '2025-091', 'Student 75', 'B.Tech CSAM', 1),
(92, '2025-092', 'Student 76', 'B.Tech CSAI', 2),
(93, '2025-093', 'Student 77', 'B.Tech CSSS', 2),
(94, '2025-094', 'Student 78', 'B.Tech CSB', 2),
(95, '2025-095', 'Student 79', 'B.Tech CSEcon', 3),
(96, '2025-096', 'Student 80', 'B.Tech CSD', 3),
(97, '2025-097', 'Student 81', 'B.Tech EVE', 3),
(98, '2025-098', 'Student 82', 'B.Tech CSE', 4),
(99, '2025-099', 'Student 83', 'B.Tech ECE', 4),
(100, '2025-100', 'Student 84', 'B.Tech CSAM', 4);


-- ========================================================
-- C. COURSES (Real IIITD Courses)
-- ========================================================
INSERT INTO courses (code, title, credits) VALUES 
('CSE101', 'Introduction to Programming', 4),
('CSE102', 'Data Structures and Algorithms', 4),
('CSE201', 'Advanced Programming', 4),
('CSE202', 'Fundamentals of Database Management System', 4),
('CSE222', 'Algorithm Design and Analysis', 4),
('CSE231', 'Operating Systems', 4),
('CSE232', 'Computer Networks', 4),
('CSE322', 'Theory of Computation', 4),
('CSE343', 'Machine Learning', 4),
('CSE556', 'Natural Language Processing', 4),
('ECE111', 'Digital Circuits', 4),
('ECE230', 'Fields and Waves', 4),
('MTH100', 'Linear Algebra', 4),
('MTH201', 'Probability and Statistics', 4),
('BIO101', 'Foundations of Biology', 4),
('DES201', 'Design Perspectives', 4),
('ECO201', 'Macroeconomics', 4),
('COM101', 'Communication Skills', 4),
('SSH101', 'Introduction to Social Sciences', 4),
('CSE504', 'Artificial Intelligence', 4);


-- ========================================================
-- D. SECTIONS
-- ========================================================
INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
('CSE101', 2, 'Mon/Wed 09:00-10:30', 'C01', 100, 'Monsoon', 2025),
('CSE102', 6, 'Tue/Thu 11:00-12:30', 'C02', 100, 'Winter', 2025),
('CSE201', 2, 'Mon/Wed 14:00-15:30', 'C11', 60, 'Monsoon', 2025),
('CSE202', 10, 'Tue/Thu 09:30-11:00', 'C12', 60, 'Monsoon', 2025),
('CSE222', 10, 'Fri 10:00-13:00', 'Lab1', 60, 'Winter', 2025),
('CSE231', 13, 'Mon/Wed 16:00-17:30', 'C21', 60, 'Monsoon', 2025),
('CSE232', 2, 'Tue/Thu 16:00-17:30', 'C22', 60, 'Winter', 2025),
('CSE343', 6, 'Mon/Wed 11:00-12:30', 'C01', 100, 'Monsoon', 2025),
('ECE111', 3, 'Tue/Thu 09:00-10:30', 'E01', 60, 'Monsoon', 2025),
('ECE230', 11, 'Mon/Wed 14:00-15:30', 'E02', 60, 'Winter', 2025),
('MTH100', 7, 'Tue/Thu 11:00-12:30', 'M01', 60, 'Monsoon', 2025),
('MTH201', 12, 'Mon/Wed 09:00-10:30', 'M02', 60, 'Winter', 2025),
('BIO101', 14, 'Fri 14:00-17:00', 'BioLab', 40, 'Monsoon', 2025),
('DES201', 15, 'Mon/Wed 16:00-17:30', 'Studio', 30, 'Monsoon', 2025),
('ECO201', 16, 'Tue/Thu 14:00-15:30', 'S01', 60, 'Monsoon', 2025),
('COM101', 9, 'Fri 09:00-10:30', 'L01', 30, 'Monsoon', 2025),
('CSE504', 13, 'Tue/Thu 11:00-12:30', 'C11', 40, 'Monsoon', 2025),
('CSE556', 2, 'Mon/Wed 11:00-12:30', 'C12', 40, 'Winter', 2025);


-- ========================================================
-- E. ENROLLMENTS
--    Randomly enrolling students (IDs 17-100) into sections (IDs 1-18)
-- ========================================================

-- Section 1 (CSE101) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (17, 1), (18, 1), (19, 1), (20, 1), (21, 1);
-- Section 3 (CSE201) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (22, 3), (23, 3), (24, 3), (25, 3), (26, 3);
-- Section 8 (CSE343) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (27, 8), (28, 8), (29, 8), (30, 8), (31, 8);
-- Section 9 (ECE111) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (32, 9), (33, 9), (34, 9), (35, 9), (36, 9);
-- Section 11 (MTH100) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (37, 11), (38, 11), (39, 11), (40, 11), (41, 11);
-- Section 15 (ECO201) - 5 students
INSERT INTO enrollments (student_id, section_id) VALUES (42, 15), (43, 15), (44, 15), (45, 15), (46, 15);

-- More enrollments for variety
INSERT INTO enrollments (student_id, section_id) VALUES 
(47, 1), (48, 3), (49, 8), (50, 9), (51, 11), (52, 15),
(53, 2), (54, 4), (55, 5), (56, 6), (57, 7), (58, 10),
(59, 12), (60, 13), (61, 14), (62, 16), (63, 17), (64, 18);

-- Add grades for some enrollments (IDs generated above start at 1)
INSERT INTO grades (enrollment_id, component, score) VALUES
(1, 'Quiz', 85.0), (1, 'Midterm', 78.0),
(2, 'Quiz', 90.0), (2, 'Midterm', 88.5),
(3, 'Quiz', 70.0), (3, 'Midterm', 65.0),
(6, 'Quiz', 95.0), (6, 'Midterm', 92.0);

-- Add Notifications
INSERT INTO notifications (user_id, role_target, message) VALUES
(NULL, 'All', 'Welcome to the new ERP system!'),
(NULL, 'Student', 'Registration for Monsoon 2025 closes on Sep 15.'),
(NULL, 'Instructor', 'Please submit mid-term grades by Oct 20.');