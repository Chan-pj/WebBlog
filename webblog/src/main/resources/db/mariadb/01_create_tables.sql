-- =============================================================================
-- webblog - MariaDB: 테이블 생성 (MyBatis 매퍼 기준)
-- =============================================================================
-- 실행 계정: pub_blog_users (또는 테이블 생성 권한이 있는 계정)
-- 실행 전: USE pub_blog;
-- 실행 방법 예시:
--   mysql -u pub_blog_users -p pub_blog < 01_create_tables.sql
-- =============================================================================

USE pub_blog;

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 회원
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blog_user (
  user_id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email             VARCHAR(255) NOT NULL,
  nickname          VARCHAR(100) NOT NULL,
  role              VARCHAR(20) NOT NULL DEFAULT 'BLOG_MEMBER',
  is_approved       TINYINT(1) NOT NULL DEFAULT 0,
  can_create_blog   TINYINT(1) NOT NULL DEFAULT 0,
  can_create_board  TINYINT(1) NOT NULL DEFAULT 0,
  board_create_quota INT NOT NULL DEFAULT 0,
  image_upload_limit_count INT NOT NULL DEFAULT 5,
  image_size_limit_bytes BIGINT NOT NULL DEFAULT 5242880,
  management_blocked TINYINT(1) NOT NULL DEFAULT 0,
  profile_restricted TINYINT(1) NOT NULL DEFAULT 0,
  phone_number      VARCHAR(50) DEFAULT NULL,
  profile_image_url VARCHAR(500) DEFAULT NULL,
  profile_blog_title VARCHAR(200) DEFAULT NULL,
  profile_etc       VARCHAR(1000) DEFAULT NULL,
  use_kakao_auth    TINYINT(1) NOT NULL DEFAULT 0,
  kakao_id          VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_blog_user_email (email),
  UNIQUE KEY uk_blog_user_kakao_id (kakao_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 블로그
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blog (
  blog_id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  owner_user_id           BIGINT UNSIGNED NOT NULL,
  blog_name               VARCHAR(200) NOT NULL,
  is_public               TINYINT(1) NOT NULL DEFAULT 1,
  board_limit             INT NOT NULL DEFAULT 5,
  image_limit_count       INT NOT NULL DEFAULT 5,
  image_size_limit_bytes  BIGINT NOT NULL DEFAULT 5242880,
  file_size_limit_bytes   BIGINT NOT NULL DEFAULT 10485760,
  total_capacity_bytes    BIGINT NOT NULL DEFAULT 314572800,
  PRIMARY KEY (blog_id),
  KEY idx_blog_owner (owner_user_id),
  CONSTRAINT fk_blog_owner_user
    FOREIGN KEY (owner_user_id) REFERENCES blog_user (user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 블로그 멤버
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blog_member (
  member_id     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id       BIGINT UNSIGNED NOT NULL,
  user_id       BIGINT UNSIGNED NOT NULL,
  member_role   VARCHAR(30) NOT NULL DEFAULT 'MEMBER',
  status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  joined_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (member_id),
  UNIQUE KEY uk_blog_member_blog_user (blog_id, user_id),
  KEY idx_blog_member_blog (blog_id),
  KEY idx_blog_member_user (user_id),
  CONSTRAINT fk_blog_member_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_blog_member_user
    FOREIGN KEY (user_id) REFERENCES blog_user (user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 게시판(블로그 하위)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS board (
  board_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id       BIGINT UNSIGNED NOT NULL,
  board_name    VARCHAR(200) NOT NULL,
  sort_order    INT NOT NULL DEFAULT 0,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (board_id),
  KEY idx_board_blog (blog_id),
  CONSTRAINT fk_board_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 게시글 (원글 / 블로그 관리자 답글: parent_post_id 가 NULL 이면 원글)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS post (
  post_id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id           BIGINT UNSIGNED NOT NULL,
  board_id          BIGINT UNSIGNED NOT NULL,
  parent_post_id    BIGINT UNSIGNED DEFAULT NULL,
  writer_user_id    BIGINT UNSIGNED NOT NULL,
  title             VARCHAR(500) NOT NULL,
  content           MEDIUMTEXT NOT NULL,
  PRIMARY KEY (post_id),
  KEY idx_post_blog (blog_id),
  KEY idx_post_board (board_id),
  KEY idx_post_parent (parent_post_id),
  KEY idx_post_writer (writer_user_id),
  CONSTRAINT fk_post_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_post_board
    FOREIGN KEY (board_id) REFERENCES board (board_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_post_parent
    FOREIGN KEY (parent_post_id) REFERENCES post (post_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_post_writer
    FOREIGN KEY (writer_user_id) REFERENCES blog_user (user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- 금지어 (전역: blog_id IS NULL / 블로그별: blog_id 지정)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blacklist_word (
  blacklist_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  blog_id       BIGINT UNSIGNED DEFAULT NULL,
  word          VARCHAR(200) NOT NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (blacklist_id),
  KEY idx_blacklist_blog (blog_id),
  CONSTRAINT fk_blacklist_blog
    FOREIGN KEY (blog_id) REFERENCES blog (blog_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
