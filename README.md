# WebBlog

전자정부 표준프레임워크(eGovFrame 4.3) 기반의 **멀티 블로그 플랫폼**입니다.
회원은 카카오 계정으로 로그인하고, 슈퍼 관리자의 승인을 받은 뒤 자신의 블로그를 운영하거나 다른 사람의 공개 블로그에 멤버로 참여해 글을 씁니다.

> 설치·설정·구조·변경 이력 등 자세한 내용은 **[webblog/README.md](webblog/README.md)** 를 참고하세요.

## 주요 기능

| 영역 | 내용 |
|---|---|
| 인증 | 카카오 OAuth 2.0 로그인(첫 로그인 시 계정 자동 생성), `state` 검증, 로그인 시 세션 ID 재발급 |
| 회원·권한 | 슈퍼 관리자 승인제, 블로그·게시판 생성 권한을 회원별로 부여, 권한 변경 즉시 반영 |
| 블로그 | 블로그 개설, 공개/비공개, 멤버 가입·상태 관리(ACTIVE / PENDING / BLOCKED) |
| 게시판·게시글 | 블로그별 게시판, 게시글 작성·수정·삭제, 블로그 소유자 전용 답글, 페이지 나누기 |
| 검열 | 전체·블로그별 금지어 및 개인정보(이메일·전화번호·계좌) 패턴 차단, 차단 시도 기록 |
| 관리자 | 회원·블로그 관리, 통합 검색(게시글·회원·블로그·금지어·위반 로그), Word/PDF 운영 보고서 |
| 보안 | CSRF 토큰, XSS 이스케이프, 소유 관계 검증(IDOR 방지), HttpOnly 세션 쿠키, 오류 정보 비노출 |

## 기술 스택

- Java 8, Spring Framework 5.3, eGovFrame RTE 4.3.0
- MyBatis 3.5, MariaDB
- JSP / JSTL, Apache Tiles 3
- Apache POI, PDFBox (보고서 생성)
- Maven (`war` 패키징), Tomcat

## 저장소 구조

```
WebBlog/
├─ README.md                  ← 지금 보고 있는 문서
└─ webblog/                   ← 애플리케이션 (Maven 프로젝트)
   ├─ README.md               ← 상세 문서
   ├─ pom.xml
   ├─ docs/                   ← PRD, 발표 자료
   └─ src/
      ├─ main/java/com/chan/  ← web(컨트롤러·인터셉터) / service / mapper / vo / util
      ├─ main/resources/
      │  ├─ db/mariadb/        ← DB 생성·마이그레이션 SQL (00 ~ 06)
      │  └─ egovframework/     ← Spring 설정(context-*.xml), MyBatis 매퍼 XML, 메시지
      ├─ main/webapp/          ← JSP, Tiles·dispatcher 설정, web.xml
      └─ test/java/            ← 통합 테스트
```

## 빠른 시작

1. **DB 준비**: `webblog/src/main/resources/db/mariadb/` 의 SQL 을 순서대로 실행합니다.
   - 새로 설치: `00` → `01` → `05`
   - 기존 DB 업그레이드: `03` → `04` → `05`
2. **DB 접속 정보**: `~/.webblog/database.properties` (또는 `src/main/resources/egovframework/properties/database.properties`) 에 작성합니다.
   ```properties
   db.driverClassName=org.mariadb.jdbc.Driver
   db.username=pub_blog_users
   db.password=change_me
   ```
3. **카카오 키**: 환경변수 `KAKAO_REST_API_KEY` 를 설정하고, 카카오 개발자 콘솔에 Redirect URI `{서버주소}/auth/kakao/callback.do` 를 등록합니다.
4. **빌드·배포**
   ```bash
   cd webblog
   mvn package
   ```
   생성된 `target/webblog-1.0.0.war` 를 Tomcat 에 배포합니다.
5. **슈퍼 관리자 지정**: 관리자로 쓸 카카오 계정으로 한 번 로그인한 뒤, `06_promote_super_admin.sql` 의 `@kakao_id` 를 바꿔 실행합니다.

DB·카카오 설정의 단계별 안내(Windows / Linux)는 상세 문서의 **6) 실행 전 필수 설정** 을 참고하세요.

## 테스트

실제 Spring 설정과 테스트 전용 DB(`test_webblog`)로 컨트롤러부터 DB까지 검증하는 통합 테스트가 있습니다. DB가 필요하므로 기본 빌드에서는 건너뜁니다.

```bash
cd webblog
mvn test -Dwebblog.it=true
```

테스트 DB 준비 방법은 상세 문서의 **10) 확인된 상태 → 통합 테스트 실행** 을 참고하세요.
