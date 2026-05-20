-- =============================================================================
-- webblog - MariaDB: 데이터베이스 및 애플리케이션 계정 생성
-- =============================================================================
-- 실행 계정: MariaDB 관리자(root 등, CREATE USER / GRANT 권한 보유)
-- 실행 방법 예시:
--   mysql -u root -p < 00_create_database_and_user.sql
-- 또는 클라이언트에서 복사 후 실행
--
-- 아래 비밀번호·호스트는 환경에 맞게 수정하세요.
-- 애플리케이션 설정과 동일한 값으로 맞추면 됩니다.
--   - database.properties / ${user.home}/.webblog/database.properties
-- =============================================================================

-- 데이터베이스
CREATE DATABASE IF NOT EXISTS pub_blog
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

-- 애플리케이션 전용 계정 (로컬 개발: localhost 권장)
-- 비밀번호는 반드시 변경하세요.
-- (MariaDB 10.6 미만에서는 CREATE USER IF NOT EXISTS 미지원일 수 있음 →
--  DROP USER IF EXISTS 후 CREATE USER 로 대체하세요.)
-- CREATE USER IF NOT EXISTS 'pub_blog_users'@'localhost'
--  IDENTIFIED BY 'pub_blog!234';

-- 원격 접속이 필요한 경우에만 사용 (보안상 필요한 호스트로 제한 권장)
CREATE USER IF NOT EXISTS 'pub_blog_users'@'10.50.%.%' IDENTIFIED BY 'pub_blog!234';

-- 스키마에 대한 권한 (DDL은 보통 관리자가 수행하고, 앱 계정은 DML 위주인 경우
-- SELECT, INSERT, UPDATE, DELETE, EXECUTE 만 부여해도 됩니다. 아래는 개발 편의상 ALL)
-- GRANT ALL PRIVILEGES ON pub_blog.* TO 'pub_blog_users'@'localhost';

-- 원격 사용자를 만든 경우 동일하게 부여
 GRANT ALL PRIVILEGES ON pub_blog.* TO 'pub_blog_users'@'10.50.%.%';

FLUSH PRIVILEGES;
