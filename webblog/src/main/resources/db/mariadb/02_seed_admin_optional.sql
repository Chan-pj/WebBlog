-- =============================================================================
-- webblog - (선택) 관리자 계정 샘플 데이터
-- =============================================================================
-- [더 이상 로그인에 쓰이지 않음] 일반(이메일) 로그인이 제거되어 이 계정으로는 로그인할 수 없습니다.
-- 슈퍼 관리자는 카카오로 한 번 로그인한 뒤 06_promote_super_admin.sql 로 지정하세요.
-- =============================================================================

USE pub_blog;

INSERT INTO blog_user (email, nickname, role, use_kakao_auth, kakao_id)
VALUES (
  'admin@example.com',
  '관리자',
  'SUPER_ADMIN',
  0,
  NULL
)
ON DUPLICATE KEY UPDATE role = 'SUPER_ADMIN', is_approved = 1, can_create_blog = 1, can_create_board = 1;

-- 이미 같은 email 이 있으면 uk_blog_user_email 때문에 실패할 수 있습니다.
-- 그 경우 아래처럼 기존 사용자에게 ADMIN 부여:
-- UPDATE blog_user SET role = 'ADMIN' WHERE email = 'admin@example.com';
