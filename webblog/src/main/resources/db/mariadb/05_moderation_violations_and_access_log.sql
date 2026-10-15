-- =============================================================================
-- webblog - 등록 차단 시도 기록(moderation_violations) / 접속 로그(access_log) 테이블
-- =============================================================================
-- moderation_violations : pub_blog      (ModerationMapper.xml, AdminSearchMapper.xml)
-- access_log            : pub_blog_log  (log-mappers/LogMapper.xml, logDataSource)
-- 실행 방법 예시:
--   mysql -u root -p < 05_moderation_violations_and_access_log.sql
-- =============================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 1) 금지어/개인정보 포함 글 등록 차단 시도 기록
-- ---------------------------------------------------------------------------
USE pub_blog;

-- 카카오 가입자가 role='USER' 로 저장되던 버그 데이터 정리
UPDATE blog_user SET role = 'BLOG_MEMBER' WHERE role = 'USER';

CREATE TABLE IF NOT EXISTS moderation_violations (
  violation_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id           BIGINT UNSIGNED DEFAULT NULL,
  board_id          BIGINT UNSIGNED DEFAULT NULL,
  user_id           BIGINT UNSIGNED DEFAULT NULL,
  writer_ip         VARCHAR(45) DEFAULT NULL,
  violation_type    VARCHAR(30) NOT NULL,
  matched_word      VARCHAR(200) DEFAULT NULL,
  attempted_title   VARCHAR(500) DEFAULT NULL,
  attempted_snippet VARCHAR(1000) DEFAULT NULL,
  created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (violation_id),
  KEY idx_violation_type (violation_type),
  KEY idx_violation_created (created_at),
  KEY idx_violation_blog (blog_id),
  KEY idx_violation_user (user_id),
  CONSTRAINT fk_violation_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_violation_board
    FOREIGN KEY (board_id) REFERENCES board (board_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_violation_user
    FOREIGN KEY (user_id) REFERENCES blog_user (user_id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 2) 로그인/로그아웃/가입 접속 로그 (별도 DB)
-- ---------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS pub_blog_log
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

GRANT ALL PRIVILEGES ON pub_blog_log.* TO 'pub_blog_users'@'10.50.%.%';
FLUSH PRIVILEGES;

USE pub_blog_log;

CREATE TABLE IF NOT EXISTS access_log (
  log_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id     BIGINT UNSIGNED DEFAULT NULL,
  event_type  VARCHAR(30) NOT NULL,
  ip_address  VARCHAR(45) DEFAULT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (log_id),
  KEY idx_access_log_user (user_id),
  KEY idx_access_log_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
