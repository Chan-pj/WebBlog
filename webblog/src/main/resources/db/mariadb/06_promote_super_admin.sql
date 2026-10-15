-- =============================================================================
-- webblog - 카카오 계정을 슈퍼 관리자로 지정
-- =============================================================================
-- 로그인은 카카오만 지원합니다. 슈퍼 관리자로 쓸 카카오 계정으로 먼저 한 번 로그인하면
-- blog_user 에 행이 생깁니다(승인 대기 화면이 보이면 정상). 그 다음 이 스크립트를 실행하세요.
--
-- 1) 방금 로그인한 계정 확인
--      SELECT user_id, email, nickname, kakao_id, role FROM blog_user ORDER BY user_id DESC LIMIT 5;
-- 2) 아래 @kakao_id 값을 바꾼 뒤 실행
-- 3) 로그아웃 후 다시 카카오로 로그인 (세션에 이전 권한이 남아 있으므로)
-- =============================================================================

USE pub_blog;

SET @kakao_id := '여기에_카카오_ID';

UPDATE blog_user
SET role = 'SUPER_ADMIN',
    is_approved = 1,
    can_create_blog = 1,
    can_create_board = 1
WHERE kakao_id = @kakao_id;

SELECT user_id, email, nickname, kakao_id, role, is_approved
FROM blog_user
WHERE kakao_id = @kakao_id;
