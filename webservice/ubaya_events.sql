-- Database Dump for UBAYA Campus Hub (Native Mobile Programming 1604C052 - Gasal 2026-2027)
-- Import this file into phpMyAdmin / MySQL / MariaDB

CREATE DATABASE IF NOT EXISTS `ubaya_events` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ubaya_events`;

DROP TABLE IF EXISTS `registrations`;
DROP TABLE IF EXISTS `events`;

CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `category` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `location` varchar(200) NOT NULL,
  `organizer` varchar(150) NOT NULL,
  `contact_phone` varchar(30) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `skkk_points` int(11) NOT NULL DEFAULT 10,
  `quota` int(11) NOT NULL DEFAULT 100,
  `website_url` varchar(255) NOT NULL DEFAULT 'https://www.ubaya.ac.id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `events` (`id`, `title`, `category`, `date`, `location`, `organizer`, `contact_phone`, `image_url`, `description`, `skkk_points`, `quota`, `website_url`) VALUES
(1, 'UBAYA Tech & AI Summit 2026', 'Seminar', '2026-11-20', 'Auditorium Perpustakaan Lt. 5, Kampus Tenggilis', 'Fakultas Teknik Informatika UBAYA', '0312981395', 'https://picsum.photos/seed/ubayatech/800/450', 'National seminar exploring Mobile Development, Artificial Intelligence, and Cloud Computing with industry practitioners and UBAYA alumni. Participants receive e-certificate and SKKK Penalaran points.', 15, 250, 'https://www.ubaya.ac.id'),
(2, 'Android Kotlin Workshop: Zero to Hero', 'Workshop', '2026-11-25', 'Lab Pemrograman TF 3.2, Kampus Tenggilis', 'KSM Informatika UBAYA', '0312981100', 'https://picsum.photos/seed/ubayakotlin/800/450', 'Hands-on intensive workshop building native Android apps with Kotlin, RecyclerView, Material 3, and Volley REST API integration.', 20, 60, 'https://www.ubaya.ac.id'),
(3, 'Industrial IoT & Robotics Hackathon', 'Competition', '2026-12-02', 'Gedung Serbaguna Fakultas Teknik, Kampus Tenggilis', 'BEM Fakultas Teknik UBAYA', '0312981150', 'https://picsum.photos/seed/ubayarobotics/800/450', '24-hour student innovation competition solving smart campus and manufacturing challenges. Open for teams across all faculties.', 30, 120, 'https://www.ubaya.ac.id'),
(4, 'Creative Entrepreneurship & Startup Expo', 'Exhibition', '2026-12-10', 'Plaza Fakultas Bisnis & Ekonomika, Kampus Tenggilis', 'Fakultas Bisnis dan Ekonomika UBAYA', '0312981200', 'https://picsum.photos/seed/ubayastartup/800/450', 'Annual showcase of student startups, culinary innovations, and digital business prototypes with seed funding pitch sessions.', 10, 300, 'https://www.ubaya.ac.id'),
(5, 'UI/UX Material Design 3 Masterclass', 'Workshop', '2026-12-14', 'Studio Industri Kreatif, Kampus Tenggilis', 'Fakultas Industri Kreatif UBAYA', '0312981380', 'https://picsum.photos/seed/ubayadesign/800/450', 'Learn color schemes, typography hierarchy, accessibility, and dynamic theming using Material Design 3 guidelines.', 15, 80, 'https://www.ubaya.ac.id'),
(6, 'National Cyber Security Capture The Flag', 'Competition', '2026-12-18', 'Lab Jaringan Komputer TC 2.1, Kampus Tenggilis', 'KSM Cyber Security UBAYA', '0312981399', 'https://picsum.photos/seed/ubayacyber/800/450', 'Test your web exploitation, cryptography, and reverse engineering skills in UBAYA annual CTF tournament.', 25, 100, 'https://www.ubaya.ac.id');

CREATE TABLE `registrations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `event_title` varchar(150) NOT NULL,
  `student_name` varchar(100) NOT NULL,
  `nrp` varchar(20) NOT NULL,
  `faculty` varchar(100) NOT NULL,
  `attendance_mode` varchar(50) NOT NULL,
  `extras` varchar(200) NOT NULL,
  `preferred_date` date NOT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_event_id` (`event_id`),
  CONSTRAINT `fk_event_id` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
