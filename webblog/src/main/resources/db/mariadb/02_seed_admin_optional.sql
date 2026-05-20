-- =============================================================================
-- webblog - (선택) 관리자 계정 샘플 데이터
-- =============================================================================
-- /admin/** 접근은 세션 사용자 role이 ADMIN 이어야 합니다.
-- 회원가입 화면은 기본 role=USER 이므로, 테스트용 관리자를 DB에 직접 넣을 때 사용합니다.
--
-- 이메일·비밀번호 필드는 앱에서 일반 로그인이 이메일만 검사하므로,
-- 관리자 이메일로 한 행만 추가하면 /auth/login.do 에서 해당 이메일로 로그인 후
-- 관리자 메뉴에 접근할 수 있습니다.
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
