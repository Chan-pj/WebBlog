package com.ylove.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylove.service.BlogService;
import com.ylove.service.LogService;
import com.ylove.service.UserService;
import com.ylove.vo.BlogUserVO;
import com.ylove.vo.BlogVO;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_STATE_SESSION_KEY = "KAKAO_OAUTH_STATE";
    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    private static final String VIEW_PENDING_APPROVAL = "auth/pendingApproval";

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private LogService logService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/login.do")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login.do")
    public String login(@RequestParam("email") String email,
                        HttpServletRequest request,
                        Model model) {
        if (email == null || email.trim().isEmpty()) {
            LOGGER.debug("Login rejected: empty email");
            model.addAttribute("message", "이메일을 입력해주세요.");
            return "auth/login";
        }

        String trimmedEmail = email.trim();
        BlogUserVO user = userService.getUserByEmail(trimmedEmail);
        if (user == null) {
            LOGGER.info("Login failed: unknown email={}", trimmedEmail);
            // 로그인 실패 로그
            logService.log(null, "LOGIN_FAIL", request.getRemoteAddr());
            model.addAttribute("message", "등록되지 않은 이메일입니다.");
            return "auth/login";
        }

        LOGGER.info("Login success: userId={}, role={}, email={}",
                user.getUserId(), user.getRole(), user.getEmail());
        // 로그인 성공 로그
        logService.log(user.getUserId(), "LOGIN", request.getRemoteAddr());

        HttpSession session = request.getSession();
        String next = buildPostLoginRedirect(user);
        if (shouldEstablishLoginSession(next)) {
            session.setAttribute(LOGIN_USER_SESSION_KEY, user);
        }
        return next;
    }

    @GetMapping("/pendingApproval.do")
    public String pendingApprovalPage(HttpSession session) {
        BlogUserVO user = (BlogUserVO) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (user == null) {
            LOGGER.debug("pendingApproval: no session, redirect login");
            return "redirect:/auth/login.do";
        }
        if ("SUPER_ADMIN".equals(user.getRole())) {
            LOGGER.info("pendingApproval: super admin userId={} -> dashboard", user.getUserId());
            return "redirect:/admin/dashboard.do";
        }
        if (!user.isApproved()) {
            LOGGER.info("pendingApproval: not approved userId={}", user.getUserId());
            return VIEW_PENDING_APPROVAL;
        }
        BlogVO owned = blogService.getBlogByOwnerUserId(user.getUserId());
        if (owned != null) {
            LOGGER.info("pendingApproval: owned blog exists userId={} blogId={} -> blog view",
                    user.getUserId(), owned.getBlogId());
            return "redirect:/blog/" + owned.getBlogId() + "/view.do";
        }
        Long memberBlogId = blogService.findActiveMemberBlogId(user.getUserId());
        if (memberBlogId != null) {
            LOGGER.info("pendingApproval: active membership userId={} blogId={} -> blog view",
                    user.getUserId(), memberBlogId);
            return "redirect:/blog/" + memberBlogId + "/view.do";
        }
        LOGGER.warn("pendingApproval: approval wait page userId={}, email={}",
                user.getUserId(), user.getEmail());
        return VIEW_PENDING_APPROVAL;
    }

    @GetMapping("/register.do")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register.do")
    public String register(@ModelAttribute BlogUserVO user,
                           @RequestParam(value = "registerRole", required = false, defaultValue = "BLOG_MEMBER")
                           String registerRole,
                           HttpServletRequest request,
                           Model model) {
        user.setRole("BLOG_ADMIN".equalsIgnoreCase(registerRole) ? "BLOG_ADMIN" : "BLOG_MEMBER");
        user.setUseKakaoAuth(false);
        userService.registerUser(user);
        // 회원가입 로그
        logService.log(null, "REGISTER", request.getRemoteAddr());
        model.addAttribute("message", "가입 신청이 접수되었습니다. 슈퍼 관리자 승인 후 로그인하여 이용할 수 있습니다.");
        return "auth/login";
    }

    @GetMapping("/kakao/login.do")
    public String kakaoLogin(HttpServletRequest request) {
        String restApiKey = System.getenv("KAKAO_REST_API_KEY");
        if (restApiKey == null || restApiKey.trim().isEmpty()) {
            LOGGER.warn("Kakao login blocked: KAKAO_REST_API_KEY not set");
            return "redirect:/auth/login.do?error=kakao_key_missing";
        }

        HttpSession session = request.getSession();
        String state = generateOAuthState();
        LOGGER.info("state URL={}", state);
        session.setAttribute(KAKAO_STATE_SESSION_KEY, state);

        String redirectUri = buildCallbackUri(request);
        LOGGER.info("redirectUriL={}", redirectUri);
        String authorizeUrl = UriComponentsBuilder.fromHttpUrl(KAKAO_AUTH_URL)
                .queryParam("client_id", restApiKey)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build(true)
                .toUriString();

        LOGGER.info("authorizeUrl={}", authorizeUrl);
        LOGGER.debug("Kakao authorize redirect, state set in session");
        return "redirect:" + authorizeUrl;
    }

    @GetMapping("/kakao/callback.do")
    public String kakaoCallback(@RequestParam(value = "code", required = false) String code,
                                @RequestParam(value = "state", required = false) String state,
                                @RequestParam(value = "error", required = false) String error,
                                HttpServletRequest request,
                                Model model) {
        if (error != null && !error.trim().isEmpty()) {
            LOGGER.info("Kakao callback error param: {}", error);
            model.addAttribute("message", "카카오 인증이 취소되었거나 실패했습니다: " + error);
            return "auth/login";
        }

        HttpSession session = request.getSession(false);
        if (session == null || !isValidState(session, state)) {
            LOGGER.warn("Kakao callback: invalid or missing state");
            model.addAttribute("message", "카카오 인증 요청의 state 검증에 실패했습니다.");
            return "auth/login";
        }

        if (code == null || code.trim().isEmpty()) {
            LOGGER.warn("Kakao callback: missing code");
            model.addAttribute("message", "카카오 인증 코드가 없어 로그인에 실패했습니다.");
            return "auth/login";
        }

        try {
            String restApiKey = System.getenv("KAKAO_REST_API_KEY");
            if (restApiKey == null || restApiKey.trim().isEmpty()) {
                LOGGER.warn("Kakao callback: REST API key missing at token step");
                model.addAttribute("message", "서버 카카오 REST API 키 설정이 필요합니다.");
                return "auth/login";
            }

            String redirectUri = buildCallbackUri(request);
            String accessToken = requestAccessToken(restApiKey, code, redirectUri);
            Map<String, Object> userInfo = requestKakaoUserInfo(accessToken);
            BlogUserVO kakaoUser = saveOrGetKakaoUser(userInfo);

            LOGGER.info("Kakao login success: userId={}, role={}, email={}",
                    kakaoUser.getUserId(), kakaoUser.getRole(), kakaoUser.getEmail());
            // 카카오 로그인 로그
            logService.log(kakaoUser.getUserId(), "LOGIN_KAKAO", request.getRemoteAddr());

            String next = buildPostLoginRedirect(kakaoUser);
            if (shouldEstablishLoginSession(next)) {
                session.setAttribute(LOGIN_USER_SESSION_KEY, kakaoUser);
            }
            return next;

        } catch (Exception e) {
            LOGGER.error("Kakao callback processing failed", e);
            model.addAttribute("message", "카카오 로그인 처리 중 오류가 발생했습니다.");
            return "auth/login";
        }
    }

    @GetMapping("/logout.do")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object u = session.getAttribute(LOGIN_USER_SESSION_KEY);
            session.removeAttribute(LOGIN_USER_SESSION_KEY);
            if (u instanceof BlogUserVO) {
                BlogUserVO user = (BlogUserVO) u;
                LOGGER.info("Logout: userId={}, email={}", user.getUserId(), user.getEmail());
                // 로그아웃 로그
                logService.log(user.getUserId(), "LOGOUT", request.getRemoteAddr());
            }
        }
        return "redirect:/auth/login.do";
    }

    private boolean isValidState(HttpSession session, String state) {
        Object storedState = session.getAttribute(KAKAO_STATE_SESSION_KEY);
        session.removeAttribute(KAKAO_STATE_SESSION_KEY);
        return storedState != null && storedState.equals(state);
    }

    private String requestAccessToken(String restApiKey, String code, String redirectUri) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + urlEncode(restApiKey)
                + "&redirect_uri=" + urlEncode(redirectUri)
                + "&code=" + urlEncode(code);

        HttpEntity<String> tokenRequest = new HttpEntity<String>(requestBody, headers);
        ResponseEntity<String> tokenResponse = restTemplate.exchange(
                KAKAO_TOKEN_URL, HttpMethod.POST, tokenRequest, String.class);

        Map<String, Object> tokenMap = objectMapper.readValue(tokenResponse.getBody(),
                new TypeReference<Map<String, Object>>() {});
        Object accessToken = tokenMap.get("access_token");
        if (accessToken == null) {
            throw new IllegalStateException("access_token not found");
        }
        return accessToken.toString();
    }

    private Map<String, Object> requestKakaoUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<String>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                KAKAO_USER_INFO_URL, HttpMethod.GET, userInfoRequest, String.class);

        return objectMapper.readValue(userInfoResponse.getBody(), new TypeReference<Map<String, Object>>() {});
    }

    @SuppressWarnings("unchecked")
    private BlogUserVO saveOrGetKakaoUser(Map<String, Object> userInfo) {
        Object idObj = userInfo.get("id");
        if (idObj == null) {
            throw new IllegalStateException("kakao user id not found");
        }

        String kakaoId = String.valueOf(idObj);
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.getOrDefault("kakao_account",
                Collections.emptyMap());
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile",
                Collections.emptyMap());

        Object emailObj = kakaoAccount.get("email");
        String email = emailObj != null ? String.valueOf(emailObj) : "kakao_user_" + kakaoId + "@kakao.local";

        Object nicknameObj = profile.get("nickname");
        String nickname = nicknameObj != null ? String.valueOf(nicknameObj) : "카카오회원";

        return userService.registerKakaoUser(kakaoId, email, nickname);
    }

    private String buildCallbackUri(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
        return baseUrl + "/auth/kakao/callback.do";
    }

    private String generateOAuthState() {
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding is not supported", e);
        }
    }

    private String buildPostLoginRedirect(BlogUserVO user) {
        Long userId = user.getUserId();
        if ("SUPER_ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard.do";
        }
        if (!user.isApproved()) {
            return VIEW_PENDING_APPROVAL;
        }
        BlogVO owned = blogService.getBlogByOwnerUserId(userId);
        if (owned != null) {
            return "redirect:/blog/" + owned.getBlogId() + "/view.do";
        }
        Long memberBlogId = blogService.findActiveMemberBlogId(userId);
        if (memberBlogId != null) {
            return "redirect:/blog/" + memberBlogId + "/view.do";
        }
        return VIEW_PENDING_APPROVAL;
    }

    private boolean shouldEstablishLoginSession(String next) {
        return next != null
                && (next.startsWith("redirect:") || VIEW_PENDING_APPROVAL.equals(next));
    }
}