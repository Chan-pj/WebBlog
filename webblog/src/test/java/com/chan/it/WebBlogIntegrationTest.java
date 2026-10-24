package com.chan.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.chan.service.AdminSearchService;
import com.chan.service.ReportService;
import com.chan.service.UserService;
import com.chan.vo.AdminSearchResultVO;
import com.chan.vo.BlogUserVO;
import com.chan.web.AuthController;

/**
 * 실제 Spring XML 설정 + 테스트 DB(test_webblog)로 컨트롤러·서비스·매퍼를 함께 검증하는 통합 테스트.
 * <p>
 * DB 가 필요하므로 기본 빌드에서는 건너뛰고, 다음처럼 명시적으로 실행한다.
 * <pre>mvn test -Dwebblog.it=true [-Dwebblog.it.db=test_webblog]</pre>
 * 테스트 DB 는 01_create_tables.sql, 05_moderation_violations_and_access_log.sql 로 만든다.
 * 매 테스트마다 테이블을 비우므로 반드시 test_ 로 시작하는 DB 에서만 실행된다.
 */
@EnabledIfSystemProperty(named = "webblog.it", matches = "true")
@ExtendWith(SpringExtension.class)
@WebAppConfiguration("src/main/webapp")
@ContextHierarchy({
    @ContextConfiguration(name = "root", locations = {
        "classpath*:egovframework/spring/context-*.xml",
        "classpath:webblog-it-datasource.xml"}),
    @ContextConfiguration(name = "servlet", locations = {
        "file:src/main/webapp/WEB-INF/config/egovframework/springmvc/dispatcher-servlet.xml"})
})
class WebBlogIntegrationTest {

    private static final String CSRF = "test-csrf-token";

    @Resource
    private WebApplicationContext wac;

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Resource
    private UserService userService;

    @Resource
    private AdminSearchService adminSearchService;

    @Resource
    private ReportService reportService;

    private MockMvc mvc;
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        jdbc = new JdbcTemplate(dataSource);
        String db = jdbc.queryForObject("SELECT DATABASE()", String.class);
        if (db == null || !db.startsWith("test_")) {
            throw new IllegalStateException("통합 테스트는 test_ 로 시작하는 DB 에서만 실행합니다: " + db);
        }
        jdbc.update("DELETE FROM moderation_violations");
        jdbc.update("DELETE FROM post");
        jdbc.update("DELETE FROM blacklist_word");
        jdbc.update("DELETE FROM blog_member");
        jdbc.update("DELETE FROM board");
        jdbc.update("DELETE FROM blog");
        jdbc.update("DELETE FROM blog_user");
    }

    // ------------------------------------------------------------------ fixtures

    private long user(String email, String role, boolean approved, boolean canCreateBlog, boolean canCreateBoard) {
        jdbc.update("INSERT INTO blog_user (email, nickname, role, is_approved, can_create_blog, can_create_board) "
                + "VALUES (?, ?, ?, ?, ?, ?)", email, email.split("@")[0], role, approved, canCreateBlog, canCreateBoard);
        return jdbc.queryForObject("SELECT user_id FROM blog_user WHERE email = ?", Long.class, email);
    }

    private long member(String email) {
        return user(email, "BLOG_MEMBER", true, false, false);
    }

    private long blog(long ownerId, String name) {
        jdbc.update("INSERT INTO blog (owner_user_id, blog_name, is_public) VALUES (?, ?, 1)", ownerId, name);
        return jdbc.queryForObject("SELECT blog_id FROM blog WHERE owner_user_id = ?", Long.class, ownerId);
    }

    private long board(long blogId, String name) {
        jdbc.update("INSERT INTO board (blog_id, board_name, sort_order) VALUES (?, ?, 0)", blogId, name);
        return jdbc.queryForObject("SELECT MAX(board_id) FROM board WHERE blog_id = ?", Long.class, blogId);
    }

    private long insertPost(long blogId, long boardId, long writerId, String title, String content, Long parentId) {
        jdbc.update("INSERT INTO post (blog_id, board_id, parent_post_id, writer_user_id, title, content) "
                + "VALUES (?, ?, ?, ?, ?, ?)", blogId, boardId, parentId, writerId, title, content);
        return jdbc.queryForObject("SELECT MAX(post_id) FROM post", Long.class);
    }

    private long membership(long blogId, long userId, String status) {
        jdbc.update("INSERT INTO blog_member (blog_id, user_id, status) VALUES (?, ?, ?)", blogId, userId, status);
        return jdbc.queryForObject("SELECT member_id FROM blog_member WHERE blog_id = ? AND user_id = ?",
                Long.class, blogId, userId);
    }

    private int count(String sql, Object... args) {
        return jdbc.queryForObject(sql, Integer.class, args);
    }

    /** DB 의 사용자로 로그인된 세션(+CSRF 토큰). */
    private MockHttpSession loginAs(long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", userService.getUserById(userId));
        session.setAttribute("csrfToken", CSRF);
        return session;
    }

    private MockHttpServletRequestBuilder postWithCsrf(String url, MockHttpSession session) {
        return post(url).session(session).param("_csrf", CSRF);
    }

    // ------------------------------------------------------------------ CSRF (#5)

    @Test
    void csrf_postWithoutTokenIsRejected() throws Exception {
        long owner = user("owner@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "b");
        MockHttpSession s = loginAs(owner);

        mvc.perform(post("/blog/" + blogId + "/board/add.do").session(s).param("boardName", "x"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/blog/" + blogId + "/board/add.do").session(s).param("boardName", "x")
                .param("_csrf", "wrong"))
                .andExpect(status().isForbidden());
        assertEquals(0, count("SELECT COUNT(*) FROM board WHERE blog_id = ?", blogId));

        mvc.perform(postWithCsrf("/blog/" + blogId + "/board/add.do", s).param("boardName", "x"))
                .andExpect(redirectedUrl("/blog/" + blogId + "/boards.do"));
        assertEquals(1, count("SELECT COUNT(*) FROM board WHERE blog_id = ?", blogId));
    }

    @Test
    void csrf_tokenIssuedOnGet() throws Exception {
        MockHttpSession s = new MockHttpSession();
        mvc.perform(get("/auth/login.do").session(s)).andExpect(status().isOk());
        assertNotNull(s.getAttribute("csrfToken"));
    }

    // ------------------------------------------------------------------ 이메일 로그인/가입 제거 (#1)

    @Test
    void emailLoginAndRegisterAreGone() throws Exception {
        user("admin@t.com", "SUPER_ADMIN", true, true, true);
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("csrfToken", CSRF);
        mvc.perform(post("/auth/login.do").session(s).param("_csrf", CSRF).param("email", "admin@t.com"))
                .andExpect(status().isMethodNotAllowed());
        assertNull(s.getAttribute("loginUser"));
        // 매핑된 컨트롤러가 없어 정적 리소스 핸들러(default servlet)로 넘어간다 → 실제 컨테이너에서는 404
        mvc.perform(get("/auth/register.do"))
                .andExpect(r -> assertFalse(r.getHandler() instanceof org.springframework.web.method.HandlerMethod));
    }

    // ------------------------------------------------------------------ 카카오 가입 (#1, #8)

    @Test
    void kakao_newUserGetsIdAndBlogMemberRole() {
        BlogUserVO u = userService.registerKakaoUser("k1", "new@t.com", true, "새회원");
        assertNotNull(u.getUserId());
        assertEquals("BLOG_MEMBER", u.getRole());
        assertFalse(u.isApproved());
        assertEquals(u.getUserId(), userService.registerKakaoUser("k1", "new@t.com", true, "x").getUserId());
    }

    @Test
    void kakao_verifiedEmailLinksExistingAccount() {
        long existing = user("old@t.com", "BLOG_ADMIN", true, true, false);
        BlogUserVO u = userService.registerKakaoUser("k2", "old@t.com", true, "x");
        assertEquals(existing, u.getUserId().longValue());
        assertEquals("k2", u.getKakaoId());
        assertEquals("BLOG_ADMIN", u.getRole());
    }

    @Test
    void kakao_unverifiedEmailDoesNotLinkExistingAccount() {
        long existing = user("victim@t.com", "SUPER_ADMIN", true, true, true);
        BlogUserVO u = userService.registerKakaoUser("k3", "victim@t.com", false, "attacker");
        assertFalse(u.getUserId().equals(existing));
        assertEquals("kakao_k3", u.getEmail());
        assertEquals("BLOG_MEMBER", u.getRole());
        assertNull(userService.getUserById(existing).getKakaoId());
    }

    // ------------------------------------------------------------------ 멤버 상태 IDOR (#3)

    @Test
    void memberStatus_cannotChangeOtherBlogsMember() throws Exception {
        long ownerA = user("a@t.com", "BLOG_ADMIN", true, true, true);
        long ownerB = user("b@t.com", "BLOG_ADMIN", true, true, true);
        long blogA = blog(ownerA, "A");
        long blogB = blog(ownerB, "B");
        long m = member("m@t.com");
        long memberOfB = membership(blogB, m, "ACTIVE");

        mvc.perform(postWithCsrf("/blog/" + blogA + "/member/status.do", loginAs(ownerA))
                .param("memberId", String.valueOf(memberOfB)).param("status", "BLOCKED"))
                .andExpect(status().is3xxRedirection());
        assertEquals("ACTIVE", jdbc.queryForObject(
                "SELECT status FROM blog_member WHERE member_id = ?", String.class, memberOfB));

        mvc.perform(postWithCsrf("/blog/" + blogB + "/member/status.do", loginAs(ownerB))
                .param("memberId", String.valueOf(memberOfB)).param("status", "BLOCKED"))
                .andExpect(redirectedUrl("/blog/" + blogB + "/members.do"));
        assertEquals("BLOCKED", jdbc.queryForObject(
                "SELECT status FROM blog_member WHERE member_id = ?", String.class, memberOfB));
    }

    @Test
    void memberStatus_rejectsUnknownStatus() throws Exception {
        long owner = user("a@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long mem = membership(blogId, member("m@t.com"), "ACTIVE");
        mvc.perform(postWithCsrf("/blog/" + blogId + "/member/status.do", loginAs(owner))
                .param("memberId", String.valueOf(mem)).param("status", "ADMIN"))
                .andExpect(redirectedUrl("/blog/" + blogId + "/members.do?error=status"));
        assertEquals("ACTIVE", jdbc.queryForObject(
                "SELECT status FROM blog_member WHERE member_id = ?", String.class, mem));
    }

    // ------------------------------------------------------------------ 답글 권한 (#4)

    @Test
    void reply_onlyOwnerCanReply() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        long writer = member("w@t.com");
        membership(blogId, writer, "ACTIVE");
        long root = insertPost(blogId, boardId, writer, "원글", "내용", null);

        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + boardId + "/reply.do", loginAs(writer))
                .param("parentPostId", String.valueOf(root)).param("title", "t").param("content", "c"))
                .andExpect(redirectedUrl("/post/view/" + root + ".do?error=forbidden"));
        assertEquals(0, count("SELECT COUNT(*) FROM post WHERE parent_post_id = ?", root));

        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + boardId + "/reply.do", loginAs(owner))
                .param("parentPostId", String.valueOf(root)).param("title", "t").param("content", "답변"))
                .andExpect(redirectedUrl("/post/view/" + root + ".do"));
        assertEquals(1, count("SELECT COUNT(*) FROM post WHERE parent_post_id = ?", root));
    }

    @Test
    void reply_parentMustBelongToBoard() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long board1 = board(blogId, "1");
        long board2 = board(blogId, "2");
        long root = insertPost(blogId, board1, owner, "원글", "내용", null);
        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + board2 + "/reply.do", loginAs(owner))
                .param("parentPostId", String.valueOf(root)).param("title", "t").param("content", "c"))
                .andExpect(redirectedUrl("/post/" + blogId + "/board/" + board2 + "/list.do"));
        assertEquals(0, count("SELECT COUNT(*) FROM post WHERE parent_post_id = ?", root));
    }

    // ------------------------------------------------------------------ 폼 바인딩 (#2)

    @Test
    void postWrite_ignoresInjectedFields() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long other = member("x@t.com");
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        long writer = member("w@t.com");
        membership(blogId, writer, "ACTIVE");

        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + boardId + "/write.do", loginAs(writer))
                .param("title", "제목").param("content", "본문")
                .param("writerUserId", String.valueOf(other)).param("blogId", "999").param("postId", "12345"))
                .andExpect(redirectedUrl("/post/" + blogId + "/board/" + boardId + "/list.do"));
        Map<String, Object> row = jdbc.queryForMap("SELECT * FROM post WHERE title = '제목'");
        assertEquals(writer, ((Number) row.get("writer_user_id")).longValue());
        assertEquals(blogId, ((Number) row.get("blog_id")).longValue());
    }

    @Test
    void postUpdate_keepsOriginalWriterAndBlog() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        long writer = member("w@t.com");
        long other = member("x@t.com");
        membership(blogId, writer, "ACTIVE");
        long p = insertPost(blogId, boardId, writer, "old", "old", null);

        mvc.perform(postWithCsrf("/post/update.do", loginAs(writer)).param("postId", String.valueOf(p))
                .param("title", "new").param("content", "new").param("writerUserId", String.valueOf(other)))
                .andExpect(redirectedUrl("/post/view/" + p + ".do"));
        Map<String, Object> row = jdbc.queryForMap("SELECT * FROM post WHERE post_id = ?", p);
        assertEquals("new", row.get("title"));
        assertEquals(writer, ((Number) row.get("writer_user_id")).longValue());

        mvc.perform(postWithCsrf("/post/update.do", loginAs(other)).param("postId", String.valueOf(p))
                .param("title", "hacked").param("content", "hacked"))
                .andExpect(redirectedUrl("/post/view/" + p + ".do?error=forbidden"));
        assertEquals("new", jdbc.queryForObject("SELECT title FROM post WHERE post_id = ?", String.class, p));
    }

    @Test
    void adminBlogUpdate_savesLimitsWithinBounds() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "원래이름");

        mvc.perform(postWithCsrf("/admin/blog/update.do", loginAs(admin))
                .param("blogId", String.valueOf(blogId)).param("blogName", "새이름").param("isPublic", "false")
                .param("boardLimit", "7").param("imageLimitCount", "-3").param("imageSizeLimitBytes", "999999999999")
                .param("ownerUserId", String.valueOf(admin)))
                .andExpect(redirectedUrl("/admin/blogs.do"));
        Map<String, Object> row = jdbc.queryForMap("SELECT * FROM blog WHERE blog_id = ?", blogId);
        assertEquals("새이름", row.get("blog_name"));
        assertEquals(7, ((Number) row.get("board_limit")).intValue());
        assertEquals(0, ((Number) row.get("image_limit_count")).intValue());
        assertEquals(100L * 1024 * 1024, ((Number) row.get("image_size_limit_bytes")).longValue());
        assertEquals(owner, ((Number) row.get("owner_user_id")).longValue());
        assertEquals(Boolean.FALSE, row.get("is_public"));
    }

    @Test
    void adminApprove_clampsValues() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long u = user("u@t.com", "BLOG_MEMBER", false, false, false);
        mvc.perform(postWithCsrf("/admin/user/approve.do", loginAs(admin))
                .param("userId", String.valueOf(u)).param("approved", "true")
                .param("boardCreateQuota", "-1").param("imageUploadLimitCount", "100000")
                .param("role", "SUPER_ADMIN"))
                .andExpect(redirectedUrl("/admin/dashboard.do"));
        BlogUserVO vo = userService.getUserById(u);
        assertTrue(vo.isApproved());
        assertEquals(0, vo.getBoardCreateQuota());
        assertEquals(100, vo.getImageUploadLimitCount());
        assertEquals("BLOG_MEMBER", vo.getRole());
    }

    // ------------------------------------------------------------------ 관리자 검색 / 위반 기록 (#6, #7)

    @Test
    void violation_isRecordedEvenThoughPostIsRejected() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        jdbc.update("INSERT INTO blacklist_word (word, blog_id) VALUES ('금지어', NULL)");

        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + boardId + "/write.do", loginAs(owner))
                .param("title", "제목").param("content", "이건 금지어 포함"))
                .andExpect(redirectedUrl("/post/" + blogId + "/board/" + boardId + "/list.do?error=validation"));
        assertEquals(0, count("SELECT COUNT(*) FROM post"));
        Map<String, Object> v = jdbc.queryForMap("SELECT * FROM moderation_violations");
        assertEquals("BLOCKED_WORD", v.get("violation_type"));
        assertEquals("금지어", v.get("matched_word"));
        assertEquals(owner, ((Number) v.get("user_id")).longValue());

        mvc.perform(postWithCsrf("/post/" + blogId + "/board/" + boardId + "/write.do", loginAs(owner))
                .param("title", "연락처").param("content", "010-1234-5678"))
                .andExpect(redirectedUrl("/post/" + blogId + "/board/" + boardId + "/list.do?error=validation"));
        assertEquals(1, count("SELECT COUNT(*) FROM moderation_violations WHERE violation_type = 'PERSONAL_INFO'"));
    }

    @Test
    void adminSearch_allTypesWork() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long owner = user("hong@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "홍길동블로그");
        long boardId = board(blogId, "기본");
        insertPost(blogId, boardId, owner, "홍길동 일기", "오늘은 나쁜말 을 했다", null);
        jdbc.update("INSERT INTO blacklist_word (word, blog_id) VALUES ('나쁜말', NULL)");
        jdbc.update("INSERT INTO moderation_violations (blog_id, board_id, user_id, violation_type, matched_word, "
                + "attempted_title) VALUES (?, ?, ?, 'BLOCKED_WORD', '나쁜말', '홍길동 시도')", blogId, boardId, owner);

        assertType(adminSearchService.search("POST", "홍길동", 50), "POST", 1);
        assertType(adminSearchService.search("USER", "hong", 50), "USER", 1);
        assertType(adminSearchService.search("BLOG", "홍길동", 50), "BLOG", 1);
        assertType(adminSearchService.search("FORBIDDEN", "", 50), "FORBIDDEN", 1);
        assertType(adminSearchService.search("VIOLATION", "", 50), "VIOLATION", 1);
        assertType(adminSearchService.search("VIOLATION", "홍길동", 50), "VIOLATION", 1);
        List<AdminSearchResultVO> all = adminSearchService.search("ALL", "홍길동", 50);
        assertTrue(all.size() >= 3, "ALL 결과: " + all.size());
        assertEquals(0, adminSearchService.search("USER", "100%", 50).size());

        for (String type : new String[] {"ALL", "POST", "USER", "BLOG", "FORBIDDEN", "VIOLATION"}) {
            mvc.perform(get("/admin/search.do").session(loginAs(admin)).param("searchType", type)
                    .param("keyword", "홍길동"))
                    .andExpect(status().isOk()).andExpect(viewName("admin/search"))
                    .andExpect(r -> assertNotNull(r.getModelAndView().getModel().get("results")));
        }
        Map<String, String> report = reportService.buildReportData();
        assertNotNull(report);
    }

    private static org.springframework.test.web.servlet.ResultMatcher viewName(String expected) {
        return r -> assertEquals(expected, r.getModelAndView().getViewName());
    }

    private static void assertType(List<AdminSearchResultVO> rows, String type, int expected) {
        assertEquals(expected, rows.size(), type + " 결과 수");
        for (AdminSearchResultVO r : rows) {
            assertEquals(type, r.getResultType());
        }
    }

    // ------------------------------------------------------------------ 삭제 FK (#9)

    @Test
    void deleteBlog_removesPostsBoardsAndMembers() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        long m = member("m@t.com");
        membership(blogId, m, "ACTIVE");
        long root = insertPost(blogId, boardId, m, "원글", "c", null);
        insertPost(blogId, boardId, owner, "답글", "c", root);
        jdbc.update("INSERT INTO blacklist_word (word, blog_id) VALUES ('w', ?)", blogId);
        jdbc.update("INSERT INTO moderation_violations (blog_id, board_id, user_id, violation_type) "
                + "VALUES (?, ?, ?, 'BLOCKED_WORD')", blogId, boardId, m);

        mvc.perform(postWithCsrf("/admin/blog/delete.do", loginAs(admin)).param("blogId", String.valueOf(blogId)))
                .andExpect(redirectedUrl("/admin/blogs.do"));
        assertEquals(0, count("SELECT COUNT(*) FROM blog"));
        assertEquals(0, count("SELECT COUNT(*) FROM board"));
        assertEquals(0, count("SELECT COUNT(*) FROM post"));
        assertEquals(0, count("SELECT COUNT(*) FROM blog_member"));
        assertEquals(0, count("SELECT COUNT(*) FROM blacklist_word"));
        assertEquals(1, count("SELECT COUNT(*) FROM moderation_violations WHERE blog_id IS NULL"));
        assertEquals(3, count("SELECT COUNT(*) FROM blog_user"));
    }

    @Test
    void deleteUser_removesOwnedBlogAndOwnPostsElsewhere() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long target = user("t@t.com", "BLOG_ADMIN", true, true, true);
        long otherOwner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long ownBlog = blog(target, "mine");
        long ownBoard = board(ownBlog, "기본");
        insertPost(ownBlog, ownBoard, target, "내 글", "c", null);
        long otherBlog = blog(otherOwner, "other");
        long otherBoard = board(otherBlog, "기본");
        membership(otherBlog, target, "ACTIVE");
        long targetsPost = insertPost(otherBlog, otherBoard, target, "남의 블로그 글", "c", null);
        insertPost(otherBlog, otherBoard, otherOwner, "답글", "c", targetsPost);
        insertPost(otherBlog, otherBoard, otherOwner, "주인 글", "c", null);

        mvc.perform(postWithCsrf("/admin/user/delete.do", loginAs(admin)).param("userId", String.valueOf(target)))
                .andExpect(redirectedUrl("/admin/dashboard.do"));
        assertNull(userService.getUserById(target));
        assertEquals(0, count("SELECT COUNT(*) FROM blog WHERE blog_id = ?", ownBlog));
        assertEquals(1, count("SELECT COUNT(*) FROM post"));
        assertEquals("주인 글", jdbc.queryForObject("SELECT title FROM post", String.class));
        assertEquals(0, count("SELECT COUNT(*) FROM blog_member"));
    }

    @Test
    void deleteUser_adminCannotDeleteSelf() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        mvc.perform(postWithCsrf("/admin/user/delete.do", loginAs(admin)).param("userId", String.valueOf(admin)))
                .andExpect(redirectedUrl("/admin/dashboard.do"));
        assertNotNull(userService.getUserById(admin));
    }

    // ------------------------------------------------------------------ 세션 갱신 (#10)

    @Test
    void session_approvalAppliesWithoutRelogin() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", false, true, true);
        long blogId = blog(owner, "A");
        MockHttpSession s = loginAs(owner);
        mvc.perform(get("/").session(s)).andExpect(redirectedUrl("/auth/pendingApproval.do"));

        jdbc.update("UPDATE blog_user SET is_approved = 1 WHERE user_id = ?", owner);
        mvc.perform(get("/").session(s)).andExpect(redirectedUrl("/blog/" + blogId + "/view.do"));
        assertTrue(((BlogUserVO) s.getAttribute("loginUser")).isApproved());
    }

    @Test
    void session_demotedAdminLosesAdminAccessImmediately() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        MockHttpSession s = loginAs(admin);
        mvc.perform(get("/admin/dashboard.do").session(s)).andExpect(status().isOk());

        jdbc.update("UPDATE blog_user SET role = 'BLOG_MEMBER' WHERE user_id = ?", admin);
        mvc.perform(get("/admin/dashboard.do").session(s))
                .andExpect(redirectedUrl("/auth/login.do?error=admin_only"));
    }

    @Test
    void session_promotedUserGetsAdminAccessImmediately() throws Exception {
        long u = member("u@t.com");
        MockHttpSession s = loginAs(u);
        jdbc.update("UPDATE blog_user SET role = 'SUPER_ADMIN' WHERE user_id = ?", u);
        mvc.perform(get("/admin/dashboard.do").session(s)).andExpect(status().isOk());
    }

    @Test
    void session_deletedUserIsLoggedOut() throws Exception {
        long u = member("u@t.com");
        MockHttpSession s = loginAs(u);
        jdbc.update("DELETE FROM blog_user WHERE user_id = ?", u);
        MvcResult r = mvc.perform(get("/").session(s)).andReturn();
        assertEquals("/auth/login.do", r.getResponse().getRedirectedUrl());
        assertNull(s.getAttribute("loginUser"));
    }

    // ------------------------------------------------------------------ 세션 고정·로그아웃·카카오 호출 (보안 보강)

    @Test
    void kakaoCallback_regeneratesSessionIdAndCsrfToken() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(System.getenv("KAKAO_REST_API_KEY") != null,
                "KAKAO_REST_API_KEY 가 없으면 콜백 테스트를 건너뜀(더미 값이면 충분)");
        org.springframework.web.client.RestTemplate rt = (org.springframework.web.client.RestTemplate)
                org.springframework.test.util.ReflectionTestUtils.getField(wac.getBean(AuthController.class), "restTemplate");
        org.springframework.http.client.ClientHttpRequestFactory original = rt.getRequestFactory();
        org.springframework.test.web.client.MockRestServiceServer kakao =
                org.springframework.test.web.client.MockRestServiceServer.bindTo(rt).build();
        try {
            kakao.expect(req -> assertEquals("https://kauth.kakao.com/oauth/token", req.getURI().toString()))
                    .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(
                            "{\"access_token\":\"tok\"}", org.springframework.http.MediaType.APPLICATION_JSON));
            kakao.expect(req -> assertEquals("https://kapi.kakao.com/v2/user/me", req.getURI().toString()))
                    .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(
                            "{\"id\":777,\"kakao_account\":{\"email\":\"k@t.com\",\"is_email_valid\":true,"
                                    + "\"is_email_verified\":true,\"profile\":{\"nickname\":\"카카오\"}}}",
                            org.springframework.http.MediaType.APPLICATION_JSON));

            MockHttpSession s = new MockHttpSession();
            s.setAttribute("KAKAO_OAUTH_STATE", "st");
            s.setAttribute("csrfToken", "attacker-known-token");
            String before = s.getId();

            MvcResult r = mvc.perform(get("/auth/kakao/callback.do").session(s).param("code", "c").param("state", "st"))
                    .andExpect(status().isOk()).andReturn();
            kakao.verify();
            assertEquals("auth/pendingApproval", r.getModelAndView().getViewName());
            assertFalse(before.equals(s.getId()), "로그인 후 세션 ID 가 바뀌어야 함");
            assertNotNull(s.getAttribute("loginUser"));
            assertNull(s.getAttribute("csrfToken"), "로그인 전 CSRF 토큰은 폐기되어야 함");
            assertNull(s.getAttribute("KAKAO_OAUTH_STATE"));
        } finally {
            rt.setRequestFactory(original);
        }
    }

    @Test
    void kakaoRestTemplate_hasTimeouts() {
        org.springframework.web.client.RestTemplate rt = (org.springframework.web.client.RestTemplate)
                org.springframework.test.util.ReflectionTestUtils.getField(wac.getBean(AuthController.class), "restTemplate");
        Object factory = rt.getRequestFactory();
        assertTrue(factory instanceof org.springframework.http.client.SimpleClientHttpRequestFactory);
        assertEquals(3000, org.springframework.test.util.ReflectionTestUtils.getField(factory, "connectTimeout"));
        assertEquals(5000, org.springframework.test.util.ReflectionTestUtils.getField(factory, "readTimeout"));
    }

    @Test
    void logout_invalidatesSession() throws Exception {
        long u = member("u@t.com");
        MockHttpSession s = loginAs(u);
        mvc.perform(get("/auth/logout.do").session(s)).andExpect(redirectedUrl("/auth/login.do"));
        assertTrue(s.isInvalid());
    }

    @Test
    void provisionBlog_refusesSuperAdmin() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        long other = user("admin2@t.com", "SUPER_ADMIN", true, true, true);
        long normal = member("m@t.com");
        mvc.perform(postWithCsrf("/admin/user/provisionBlog.do", loginAs(admin)).param("userId", String.valueOf(other)))
                .andExpect(redirectedUrl("/admin/dashboard.do"));
        assertEquals(0, count("SELECT COUNT(*) FROM blog WHERE owner_user_id = ?", other));
        mvc.perform(postWithCsrf("/admin/user/provisionBlog.do", loginAs(admin)).param("userId", String.valueOf(normal)))
                .andExpect(redirectedUrl("/admin/dashboard.do"));
        assertEquals(1, count("SELECT COUNT(*) FROM blog WHERE owner_user_id = ?", normal));
    }

    // ------------------------------------------------------------------ 페이지 나누기 (#7)

    @SuppressWarnings("unchecked")
    private static <T> T modelAttr(MvcResult r, String name) {
        return (T) r.getModelAndView().getModel().get(name);
    }

    @Test
    void paging_postListShowsTenPerPageAndClampsPage() throws Exception {
        long owner = user("o@t.com", "BLOG_ADMIN", true, true, true);
        long blogId = blog(owner, "A");
        long boardId = board(blogId, "기본");
        for (int i = 1; i <= 15; i++) {
            long root = insertPost(blogId, boardId, owner, "글" + i, "c", null);
            if (i == 15) {
                insertPost(blogId, boardId, owner, "답글", "c", root);
            }
        }
        String url = "/post/" + blogId + "/board/" + boardId + "/list.do";

        MvcResult p1 = mvc.perform(get(url)).andExpect(status().isOk()).andReturn();
        List<?> posts1 = modelAttr(p1, "posts");
        org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo info = modelAttr(p1, "paginationInfo");
        assertEquals(10, posts1.size());
        assertEquals(15, info.getTotalRecordCount(), "답글은 목록 개수에서 제외");
        assertEquals(2, info.getTotalPageCount());

        MvcResult p2 = mvc.perform(get(url).param("pageIndex", "2")).andReturn();
        assertEquals(5, ((List<?>) modelAttr(p2, "posts")).size());

        MvcResult over = mvc.perform(get(url).param("pageIndex", "99")).andReturn();
        assertEquals(2, ((org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo)
                modelAttr(over, "paginationInfo")).getCurrentPageNo());
        assertEquals(5, ((List<?>) modelAttr(over, "posts")).size());

        MvcResult under = mvc.perform(get(url).param("pageIndex", "-3")).andReturn();
        assertEquals(10, ((List<?>) modelAttr(under, "posts")).size());
    }

    @Test
    void paging_exploreAndAdminLists() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        user("admin2@t.com", "SUPER_ADMIN", true, true, true);
        for (int i = 1; i <= 12; i++) {
            blog(user("u" + i + "@t.com", "BLOG_ADMIN", true, true, true), "블로그" + i);
        }
        MvcResult explore = mvc.perform(get("/blog/explore.do").param("pageIndex", "2")).andReturn();
        assertEquals(2, ((List<?>) modelAttr(explore, "blogs")).size());

        MvcResult dash = mvc.perform(get("/admin/dashboard.do").session(loginAs(admin))
                .param("blogPage", "2").param("userPage", "1")).andExpect(status().isOk()).andReturn();
        assertEquals(2, ((List<?>) modelAttr(dash, "blogs")).size());
        List<BlogUserVO> users = modelAttr(dash, "users");
        assertEquals(10, users.size());
        for (BlogUserVO u : users) {
            assertFalse("SUPER_ADMIN".equals(u.getRole()), "회원 목록에서 슈퍼 관리자 제외");
        }
        assertEquals(12, ((org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo)
                modelAttr(dash, "userPaginationInfo")).getTotalRecordCount());

        MvcResult list = mvc.perform(get("/admin/blogs.do").session(loginAs(admin)).param("blogPage", "2")).andReturn();
        assertEquals(2, ((List<?>) modelAttr(list, "blogs")).size());
    }

    // ------------------------------------------------------------------ 오류 화면 / 뷰 경로

    @Test
    void adminReportPageResolves() throws Exception {
        long admin = user("admin@t.com", "SUPER_ADMIN", true, true, true);
        mvc.perform(get("/admin/report.do").session(loginAs(admin)))
                .andExpect(status().isOk()).andExpect(viewName("admin/report"));
    }
}
