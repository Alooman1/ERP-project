-- DISABLE FOREIGN KEYS TO ALLOW DROPPING TABLES
SET FOREIGN_KEY_CHECKS = 0;

-- Database: auth_db
CREATE DATABASE IF NOT EXISTS `auth_db`;
USE `auth_db`;

-- Table structure for table `users_auth`
DROP TABLE IF EXISTS `users_auth`;
CREATE TABLE `users_auth` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `role` enum('Student','Instructor','Admin') NOT NULL,
  `password_hash` varchar(100) NOT NULL,
  `status` varchar(20) DEFAULT 'active',
  `last_login` timestamp NULL DEFAULT NULL,
  `failed_attempts` int DEFAULT '0',
  `lockout_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `users_auth`
LOCK TABLES `users_auth` WRITE;
INSERT INTO `users_auth` VALUES 
(1,'admin1','Admin','$2a$10$ea59iMyX4wo6kCBRT7xjBuf06XME9JGUp3KW6VHzjjCGVXa4Woks6','active',NULL,0,NULL),
(2,'inst1','Instructor','$2a$10$RuERnAPFzW0Yr6/lJUtFkeAi.egNfWbp1ZDGWchA6DPyBvOPJ2IuS','active',NULL,0,NULL),
(3,'stu1','Student','$2a$10$ee3IHEg6LKg6LFvd5rNxr.r6RBW2GIeSItFGLQz0tIWVLoVboBxqO','active',NULL,0,NULL),
(4,'stu2','Student','$2a$10$wjvswTvMBhW882JoEPfH4.88Y3eoTL5lN.PqswzqCUBh6XiOqka2u','active',NULL,0,NULL),
(11,'inst2','Instructor','$2a$10$e8CVQpNSnW5dAqzkm9hcxOueFfjIfyMrJ/dQhi/5LDTyGWo7DMMCu','active',NULL,0,NULL);
UNLOCK TABLES;

-- RE-ENABLE FOREIGN KEYS
SET FOREIGN_KEY_CHECKS = 1;