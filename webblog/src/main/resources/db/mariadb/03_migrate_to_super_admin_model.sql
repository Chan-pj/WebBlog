-- =============================================================================
-- webblog - 기존 스키마를 SUPER_ADMIN/BLOG_ADMIN/BLOG_MEMBER 모델로 마이그레이션
-- =============================================================================
-- 대상: 이미 01_create_tables.sql(구버전) 기반으로 운영 중인 DB
-- 실행 전: 반드시 백업 후 적용
-- 실행 방법 예시:
--   mysql -u pub_blog_users -p pub_blog < 03_migrate_to_super_admin_model.sql
-- =============================================================================

USE pub_blog;

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 1) blog_user 확장 컬럼 추가
-- ---------------------------------------------------------------------------
ALTER TABLE blog_user
  ADD COLUMN IF NOT EXISTS is_approved TINYINT(1) NOT NULL DEFAULT 1 AFTER role,
  ADD COLUMN IF NOT EXISTS can_create_blog TINYINT(1) NOT NULL DEFAULT 0 AFTER is_approved,
  ADD COLUMN IF NOT EXISTS can_create_board TINYINT(1) NOT NULL DEFAULT 0 AFTER can_create_blog,
  ADD COLUMN IF NOT EXISTS board_create_quota INT NOT NULL DEFAULT 0 AFTER can_create_board,
  ADD COLUMN IF NOT EXISTS image_upload_limit_count INT NOT NULL DEFAULT 5 AFTER board_create_quota,
  ADD COLUMN IF NOT EXISTS image_size_limit_bytes BIGINT NOT NULL DEFAULT 5242880 AFTER image_upload_limit_count,
  ADD COLUMN IF NOT EXISTS management_blocked TINYINT(1) NOT NULL DEFAULT 0 AFTER image_size_limit_bytes,
  ADD COLUMN IF NOT EXISTS profile_restricted TINYINT(1) NOT NULL DEFAULT 0 AFTER management_blocked,
  ADD COLUMN IF NOT EXISTS phone_number VARCHAR(50) DEFAULT NULL AFTER profile_restricted,
  ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500) DEFAULT NULL AFTER phone_number,
  ADD COLUMN IF NOT EXISTS profile_blog_title VARCHAR(200) DEFAULT NULL AFTER profile_image_url,
  ADD COLUMN IF NOT EXISTS profile_etc VARCHAR(1000) DEFAULT NULL AFTER profile_blog_title;

-- ---------------------------------------------------------------------------
-- 2) blog 확장 컬럼 추가
-- ---------------------------------------------------------------------------
ALTER TABLE blog
  ADD COLUMN IF NOT EXISTS image_limit_count INT NOT NULL DEFAULT 5 AFTER board_limit,
  ADD COLUMN IF NOT EXISTS image_size_limit_bytes BIGINT NOT NULL DEFAULT 5242880 AFTER image_limit_count;

-- ---------------------------------------------------------------------------
-- 3) role 데이터 정규화
--   - 기존 ADMIN -> SUPER_ADMIN
--   - 기존 USER  -> BLOG_MEMBER
--   - 그 외/NULL  -> BLOG_MEMBER
-- ---------------------------------------------------------------------------
UPDATE blog_user
SET role = 'SUPER_ADMIN'
WHERE role = 'ADMIN';

UPDATE blog_user
SET role = 'BLOG_MEMBER'
WHERE role = 'USER' OR role IS NULL OR role = '';

-- ---------------------------------------------------------------------------
-- 4) 기존 SUPER_ADMIN 권한 기본 보정
-- ---------------------------------------------------------------------------
UPDATE blog_user
SET is_approved = 1,
    can_create_blog = 1,
    can_create_board = 1
WHERE role = 'SUPER_ADMIN';

-- ---------------------------------------------------------------------------
-- 5) BLOG_ADMIN 기본 보정
--   - 승인 전 관리자 신청자: 권한 0 유지
-- ---------------------------------------------------------------------------
UPDATE blog_user
SET is_approved = IFNULL(is_approved, 0)
WHERE role = 'BLOG_ADMIN';

-- ---------------------------------------------------------------------------
-- 6) 컬럼 타입/기본값 정합성 보정(구 role 기본값 USER -> BLOG_MEMBER)
-- ---------------------------------------------------------------------------
ALTER TABLE blog_user
  MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'BLOG_MEMBER';

-- =============================================================================
-- 완료
-- =============================================================================
