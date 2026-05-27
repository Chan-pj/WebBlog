# Word 보고서 템플릿 (`word_template.docx`)

`src/main/resources/template/word_template.docx` 파일을 직접 만든 뒤, 본문에 아래 플레이스홀더 문자열을 **그대로** 넣으세요.  
보고서 생성 시 `ReportService`가 각 항목을 실제 값으로 치환합니다.

## 플레이스홀더 목록

| 플레이스홀더 | 설명 |
|-------------|------|
| `${REPORT_TITLE}` | 보고서 제목 |
| `${REPORT_DATE}` | 생성 일시 |
| `${TOTAL_USERS}` | 전체 가입 회원 수 |
| `${TOTAL_BLOGS}` | 개설 블로그 수 |
| `${ACTIVE_MEMBERS}` | ACTIVE 블로그 멤버 수 |
| `${BLACKLIST_WORD_COUNT}` | 등록 금지어 수 |
| `${POSTS_WITH_FORBIDDEN}` | 게시글 스캔 시 금지어 포함 건수 |
| `${VIOLATION_TOTAL}` | 등록 차단 시도 전체 건수 |
| `${VIOLATION_BLOCKED_WORD}` | 금지어 차단 건수 |
| `${VIOLATION_PERSONAL_INFO}` | 개인정보 차단 건수 |
| `${MEMBER_SUMMARY}` | 블로그 가입자 현황 (여러 줄) |
| `${FORBIDDEN_STATS}` | 금지어 사용 통계 (여러 줄) |
| `${VIOLATION_DETAIL}` | 위반 시도 상세: 회원, IP, 일시, 단어 (여러 줄) |
| `${GENERATED_BY}` | 생성 시스템 표기 |

## 예시 문서 구조

```
${REPORT_TITLE}

작성일: ${REPORT_DATE}

1. 블로그 가입자 현황
${MEMBER_SUMMARY}

2. 금지어 사용 통계
${FORBIDDEN_STATS}

3. 금지어·개인정보 등록 시도 (회원 / IP / 일시)
${VIOLATION_DETAIL}

---
${GENERATED_BY}
```

템플릿이 없으면 애플리케이션이 POI로 기본 docx를 생성합니다.