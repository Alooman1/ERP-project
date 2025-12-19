-- DISABLE FOREIGN KEYS TO ALLOW DROPPING TABLES
SET FOREIGN_KEY_CHECKS = 0;

-- Database: erp_db
CREATE DATABASE IF NOT EXISTS `erp_db`;
USE `erp_db`;

-- 1. Settings Table
DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `setting_key` varchar(50) NOT NULL,
  `setting_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `settings` WRITE;
INSERT INTO `settings` VALUES ('maintenance_on','false');
UNLOCK TABLES;

-- 2. Admins Table
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins` (
  `user_id` int NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `admins` WRITE;
INSERT INTO `admins` VALUES (1,'System Administrator');
UNLOCK TABLES;

-- 3. Students Table
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `user_id` int NOT NULL,
  `roll_no` varchar(20) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `program` varchar(100) DEFAULT NULL,
  `year` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `roll_no` (`roll_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `students` WRITE;
INSERT INTO `students` VALUES 
(3,'2025-001','Student One','B.Tech CS',1),
(4,'2025-002','Alex Johnson','B.Tech ECE',1);
UNLOCK TABLES;

-- 4. Instructors Table
DROP TABLE IF EXISTS `instructors`;
CREATE TABLE `instructors` (
  `user_id` int NOT NULL,
  `department` varchar(100) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `instructors` WRITE;
INSERT INTO `instructors` VALUES 
(2,'Computer Science','Dr. Instructor'),
(11,'Mathematics','Prof. Subhajit');
UNLOCK TABLES;

-- 5. Courses Table
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `code` varchar(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `credits` int NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `courses` WRITE;
INSERT INTO `courses` VALUES 
('CSE101','Intro to Programming',4),
('MTH210','Discrete Mathematics',4),
('SSH224','Intro to Sociology',4);
UNLOCK TABLES;

-- 6. Sections Table
DROP TABLE IF EXISTS `sections`;
CREATE TABLE `sections` (
  `section_id` int NOT NULL AUTO_INCREMENT,
  `course_id` varchar(20) NOT NULL,
  `instructor_id` int DEFAULT NULL,
  `day_time` varchar(50) DEFAULT NULL,
  `room` varchar(30) DEFAULT NULL,
  `capacity` int NOT NULL,
  `semester` varchar(20) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `reg_deadline` DATE DEFAULT NULL,
  `drop_deadline` DATE DEFAULT NULL,
  PRIMARY KEY (`section_id`),
  KEY `course_id` (`course_id`),
  KEY `instructor_id` (`instructor_id`),
  CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`code`) ON DELETE CASCADE,
  CONSTRAINT `sections_ibfk_2` FOREIGN KEY (`instructor_id`) REFERENCES `instructors` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

LOCK TABLES `sections` WRITE;
INSERT INTO `sections` VALUES 
(10,'CSE101',11,'Mon/Wed 09:00-10:00','C101',300,'Fall',2025,'2025-12-31','2026-01-15'),
(11,'MTH210',2,'Mon/Wed 10:00-11:00','C102',60,'Fall',2025,'2025-12-31','2026-01-15');
UNLOCK TABLES;

-- 7. Enrollments Table
DROP TABLE IF EXISTS `enrollments`;
CREATE TABLE `enrollments` (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `section_id` int NOT NULL,
  `status` varchar(20) DEFAULT 'enrolled',
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `unique_enrollment` (`student_id`,`section_id`),
  KEY `section_id` (`section_id`),
  CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;

LOCK TABLES `enrollments` WRITE;
INSERT INTO `enrollments` VALUES (23,3,11,'enrolled');
UNLOCK TABLES;

-- 8. Grades Table
DROP TABLE IF EXISTS `grades`;
CREATE TABLE `grades` (
  `grade_id` int NOT NULL AUTO_INCREMENT,
  `enrollment_id` int NOT NULL,
  `component` varchar(50) NOT NULL,
  `score` double DEFAULT NULL,
  `final_grade` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`grade_id`),
  UNIQUE KEY `unique_grade` (`enrollment_id`,`component`),
  CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`enrollment_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4;

LOCK TABLES `grades` WRITE;
INSERT INTO `grades` VALUES 
(26,23,'quiz',18,NULL),
(27,23,'midsem',25,NULL),
(28,23,'endsem',40,NULL);
UNLOCK TABLES;

-- 9. Notifications Table
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4;

LOCK TABLES `notifications` WRITE;
INSERT INTO `notifications` VALUES (1,1,'System initialization complete.',0,'2025-11-18 10:14:07');
UNLOCK TABLES;

-- RE-ENABLE FOREIGN KEYS
SET FOREIGN_KEY_CHECKS = 1;