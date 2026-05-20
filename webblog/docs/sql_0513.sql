-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        12.2.2-MariaDB - MariaDB Server
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.14.0.7165
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- pub_blog 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `pub_blog` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `pub_blog`;

-- 테이블 pub_blog.blacklist_word 구조 내보내기
CREATE TABLE IF NOT EXISTS `blacklist_word` (
  `blacklist_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) unsigned DEFAULT NULL,
  `word` varchar(200) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`blacklist_id`),
  KEY `idx_blacklist_blog` (`blog_id`),
  CONSTRAINT `fk_blacklist_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.blacklist_word:~0 rows (대략적) 내보내기
DELETE FROM `blacklist_word`;

-- 테이블 pub_blog.blog 구조 내보내기
CREATE TABLE IF NOT EXISTS `blog` (
  `blog_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_user_id` bigint(20) unsigned NOT NULL,
  `blog_name` varchar(200) NOT NULL,
  `is_public` tinyint(1) NOT NULL DEFAULT 1,
  `board_limit` int(11) NOT NULL DEFAULT 5,
  `image_limit_count` int(11) NOT NULL DEFAULT 5,
  `image_size_limit_bytes` bigint(20) NOT NULL DEFAULT 5242880,
  `file_size_limit_bytes` bigint(20) NOT NULL DEFAULT 10485760,
  `total_capacity_bytes` bigint(20) NOT NULL DEFAULT 314572800,
  PRIMARY KEY (`blog_id`),
  KEY `idx_blog_owner` (`owner_user_id`),
  CONSTRAINT `fk_blog_owner_user` FOREIGN KEY (`owner_user_id`) REFERENCES `blog_user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.blog:~1 rows (대략적) 내보내기
DELETE FROM `blog`;
INSERT INTO `blog` (`blog_id`, `owner_user_id`, `blog_name`, `is_public`, `board_limit`, `image_limit_count`, `image_size_limit_bytes`, `file_size_limit_bytes`, `total_capacity_bytes`) VALUES
	(1, 2, '영감박의 블로그', 1, 5, 5, 5242880, 10485760, 314572800);

-- 테이블 pub_blog.blog_board 구조 내보내기
CREATE TABLE IF NOT EXISTS `blog_board` (
  `board_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) unsigned NOT NULL,
  `board_name` varchar(200) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`board_id`),
  KEY `idx_blog_board_blog` (`blog_id`),
  CONSTRAINT `fk_blog_board_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.blog_board:~1 rows (대략적) 내보내기
DELETE FROM `blog_board`;
INSERT INTO `blog_board` (`board_id`, `blog_id`, `board_name`, `sort_order`) VALUES
	(1, 1, '기본 게시판', 0);

-- 테이블 pub_blog.blog_member 구조 내보내기
CREATE TABLE IF NOT EXISTS `blog_member` (
  `member_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `member_role` varchar(30) NOT NULL DEFAULT 'MEMBER',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `joined_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_blog_member_blog_user` (`blog_id`,`user_id`),
  KEY `idx_blog_member_blog` (`blog_id`),
  KEY `idx_blog_member_user` (`user_id`),
  CONSTRAINT `fk_blog_member_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_blog_member_user` FOREIGN KEY (`user_id`) REFERENCES `blog_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.blog_member:~0 rows (대략적) 내보내기
DELETE FROM `blog_member`;

-- 테이블 pub_blog.blog_user 구조 내보내기
CREATE TABLE IF NOT EXISTS `blog_user` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `nickname` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'BLOG_MEMBER',
  `is_approved` tinyint(1) NOT NULL DEFAULT 1,
  `can_create_blog` tinyint(1) NOT NULL DEFAULT 0,
  `can_create_board` tinyint(1) NOT NULL DEFAULT 0,
  `board_create_quota` int(11) NOT NULL DEFAULT 0,
  `image_upload_limit_count` int(11) NOT NULL DEFAULT 5,
  `image_size_limit_bytes` bigint(20) NOT NULL DEFAULT 5242880,
  `management_blocked` tinyint(1) NOT NULL DEFAULT 0,
  `profile_restricted` tinyint(1) NOT NULL DEFAULT 0,
  `phone_number` varchar(50) DEFAULT NULL,
  `profile_image_url` varchar(500) DEFAULT NULL,
  `profile_blog_title` varchar(200) DEFAULT NULL,
  `profile_etc` varchar(1000) DEFAULT NULL,
  `use_kakao_auth` tinyint(1) NOT NULL DEFAULT 0,
  `kakao_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_blog_user_email` (`email`),
  UNIQUE KEY `uk_blog_user_kakao_id` (`kakao_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.blog_user:~2 rows (대략적) 내보내기
DELETE FROM `blog_user`;
INSERT INTO `blog_user` (`user_id`, `email`, `nickname`, `role`, `is_approved`, `can_create_blog`, `can_create_board`, `board_create_quota`, `image_upload_limit_count`, `image_size_limit_bytes`, `management_blocked`, `profile_restricted`, `phone_number`, `profile_image_url`, `profile_blog_title`, `profile_etc`, `use_kakao_auth`, `kakao_id`) VALUES
	(1, 'admin@example.com', '관리자', 'SUPER_ADMIN', 1, 1, 1, 0, 5, 5242880, 0, 0, NULL, NULL, NULL, NULL, 0, NULL),
	(2, 'kakao_user_4858956721@kakao.local', '영감박', 'BLOG_MEMBER', 1, 1, 1, 2, 5, 5242880, 0, 0, NULL, NULL, NULL, NULL, 1, '4858956721');

-- 테이블 pub_blog.board 구조 내보내기
CREATE TABLE IF NOT EXISTS `board` (
  `board_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) unsigned NOT NULL,
  `board_name` varchar(200) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`board_id`),
  KEY `idx_board_blog` (`blog_id`),
  CONSTRAINT `fk_board_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.board:~1 rows (대략적) 내보내기
DELETE FROM `board`;
INSERT INTO `board` (`board_id`, `blog_id`, `board_name`, `sort_order`, `created_at`) VALUES
	(1, 1, '기본 게시판', 0, '2026-05-13 13:44:45');

-- 테이블 pub_blog.post 구조 내보내기
CREATE TABLE IF NOT EXISTS `post` (
  `post_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) unsigned NOT NULL,
  `board_id` bigint(20) unsigned NOT NULL,
  `parent_post_id` bigint(20) unsigned DEFAULT NULL,
  `writer_user_id` bigint(20) unsigned NOT NULL,
  `title` varchar(500) NOT NULL,
  `content` mediumtext NOT NULL,
  PRIMARY KEY (`post_id`),
  KEY `idx_post_blog` (`blog_id`),
  KEY `idx_post_writer` (`writer_user_id`),
  KEY `fk_post_board` (`board_id`),
  KEY `fk_post_parent` (`parent_post_id`),
  CONSTRAINT `fk_post_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_board` FOREIGN KEY (`board_id`) REFERENCES `blog_board` (`board_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_parent` FOREIGN KEY (`parent_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_writer` FOREIGN KEY (`writer_user_id`) REFERENCES `blog_user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pub_blog.post:~0 rows (대략적) 내보내기
DELETE FROM `post`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
